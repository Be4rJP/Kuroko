package be4rjp.kuroko.npc;

import be4rjp.cinema4c.player.ScenePlayer;
import be4rjp.cinema4c.util.TaskHandler;
import be4rjp.kuroko.Kuroko;
import be4rjp.kuroko.event.AsyncNPCSpeechEndEvent;
import be4rjp.kuroko.event.AsyncNPCSpeechInitializeEvent;
import be4rjp.kuroko.player.KurokoPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NPC {
    
    //NPCの基礎データ
    private final NPCData npcData;
    //このNPCを見せるプレイヤー
    private final KurokoPlayer kurokoPlayer;
    //再生に使用する映像プレイヤー
    private final ScenePlayer scenePlayer;
    
    //現在のセリフ
    private Speech currentSpeech;
    //現在のtalk index
    private int currentTalkIndex = 0;
    
    //話しかけるときのクールタイム
    private long talkCoolTime = 0;
    
    
    public NPC(NPCData npcData, KurokoPlayer kurokoPlayer){
        this.npcData = npcData;
        this.kurokoPlayer= kurokoPlayer;
        this.scenePlayer = new ScenePlayer(npcData.getRecordData(), npcData.getBaseLocation(), npcData.getStartTick(), npcData.getEndTick());
        
        scenePlayer.addAudience(kurokoPlayer.getPlayer());
        scenePlayer.initialize();
        scenePlayer.start(npcData.isLoop() ? ScenePlayer.PlayMode.LOOP : ScenePlayer.PlayMode.ALL_PLAY);
    }
    
    public synchronized void talk(){
        if(talkCoolTime > System.currentTimeMillis() || npcData.getSpeeches() == null) return;
        
        Player player = kurokoPlayer.getPlayer();
        
        if(currentSpeech == null){
            currentSpeech = npcData.getRandomSpeech();
            AsyncNPCSpeechInitializeEvent initializeEvent = new AsyncNPCSpeechInitializeEvent(this, currentSpeech, kurokoPlayer);
            Bukkit.getPluginManager().callEvent(initializeEvent);
            currentSpeech = initializeEvent.getSpeech();
            
            currentTalkIndex = 0;
            
            scenePlayer.setPause(true);
        }
        
        
        String line = currentSpeech.getLine(currentTalkIndex);
        line = line.replace("%player", player.getName());
        for(String talkLine : line.split("/n/")){
            if(talkLine.contains("command{")){
                
                String temp = talkLine;
                talkLine = talkLine.replace("command{", "").replace("}", "");
                
                if(talkLine.contains("console/")){
                    String finalTalkLine = talkLine;
                    TaskHandler.runSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalTalkLine.replace("console/", "")));
                }else if(talkLine.contains("player/")){
                    String finalTalkLine1 = talkLine;
                    TaskHandler.runSync(() -> player.performCommand(finalTalkLine1.replace("player/", "")));
                }else{
                    Kuroko.getPlugin().getLogger().warning("Syntax error => " + temp);
                }
                
            }else {
                player.sendMessage(talkLine);
            }
        }
        
        
        currentTalkIndex++;
    
        
        String nextLine = currentSpeech.getLine(currentTalkIndex);
        if(nextLine == null){
            AsyncNPCSpeechEndEvent speechEndEvent = new AsyncNPCSpeechEndEvent(this, currentSpeech, kurokoPlayer);
            Bukkit.getPluginManager().callEvent(speechEndEvent);
            currentSpeech = null;
            currentTalkIndex = 0;
            Bukkit.getScheduler().runTaskLaterAsynchronously(Kuroko.getPlugin(), () -> scenePlayer.setPause(false), 25);
            
            talkCoolTime = System.currentTimeMillis() + 2000;
        }else{
            talkCoolTime = System.currentTimeMillis() + 500;
        }
    }
    
    public void resetSpeech(){
        if(currentSpeech == null) return;
        
        AsyncNPCSpeechEndEvent speechEndEvent = new AsyncNPCSpeechEndEvent(this, currentSpeech, kurokoPlayer);
        Bukkit.getPluginManager().callEvent(speechEndEvent);
        currentSpeech = null;
        currentTalkIndex = 0;
        scenePlayer.setPause(false);
    }
    
    public void unload(){scenePlayer.cancel();}
    
    public NPCData getNpcData() {return npcData;}
    
    public int getCurrentTalkIndex() {return currentTalkIndex;}
    
    public Speech getCurrentSpeech() {return currentSpeech;}
    
    public ScenePlayer getScenePlayer() {return scenePlayer;}
}
