package be4rjp.kuroko.npc;

import be4rjp.cinema4c.player.ScenePlayer;
import be4rjp.cinema4c.util.TaskHandler;
import be4rjp.kuroko.Kuroko;
import be4rjp.kuroko.event.AsyncNPCSpeechEndEvent;
import be4rjp.kuroko.event.AsyncNPCSpeechInitializeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NPC {
    
    //NPCの基礎データ
    private final NPCData npcData;
    //再生に使用する映像プレイヤー
    private final ScenePlayer scenePlayer;
    
    //現在のセリフ
    private Speech currentSpeech;
    //現在のtalk index
    private int currentTalkIndex = 0;
    
    //話しかけるときのクールタイム
    private long talkCoolTime = 0;
    
    
    public NPC(NPCData npcData, Player player){
        this.npcData = npcData;
        this.scenePlayer = new ScenePlayer(npcData.getRecordData(), npcData.getBaseLocation(), npcData.getStartTick(), npcData.getEndTick());
        
        scenePlayer.addAudience(player);
        scenePlayer.initialize();
        scenePlayer.start(npcData.isLoop() ? ScenePlayer.PlayMode.LOOP : ScenePlayer.PlayMode.ALL_PLAY);
    }
    
    public synchronized void talk(Player player){
        if(talkCoolTime > System.currentTimeMillis()) return;
        
        if(currentSpeech == null){
            currentSpeech = npcData.getRandomSpeech();
            AsyncNPCSpeechInitializeEvent initializeEvent = new AsyncNPCSpeechInitializeEvent(this, currentSpeech, player);
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
            AsyncNPCSpeechEndEvent speechEndEvent = new AsyncNPCSpeechEndEvent(this, npcData.getSpeeches().indexOf(currentSpeech), player);
            Bukkit.getPluginManager().callEvent(speechEndEvent);
            currentSpeech = null;
            currentTalkIndex = 0;
            Bukkit.getScheduler().runTaskLaterAsynchronously(Kuroko.getPlugin(), () -> scenePlayer.setPause(false), 12);
            
            talkCoolTime = System.currentTimeMillis() + 2000;
        }else{
            talkCoolTime = System.currentTimeMillis() + 500;
        }
    }
    
    public void unload(){
        scenePlayer.cancel();
    }
    
    public NPCData getNpcData() {return npcData;}
    
    public int getCurrentTalkIndex() {return currentTalkIndex;}
    
    public Speech getCurrentSpeech() {return currentSpeech;}
    
    public ScenePlayer getScenePlayer() {return scenePlayer;}
}
