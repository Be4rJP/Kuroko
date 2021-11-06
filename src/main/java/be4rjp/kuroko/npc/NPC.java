package be4rjp.kuroko.npc;

import be4rjp.cinema4c.data.record.tracking.PlayerTrackData;
import be4rjp.cinema4c.data.record.tracking.TrackData;
import be4rjp.cinema4c.nms.NMSUtil;
import be4rjp.cinema4c.player.ScenePlayer;
import be4rjp.cinema4c.util.TaskHandler;
import be4rjp.kuroko.Kuroko;
import be4rjp.kuroko.event.AsyncNPCSpeechEndEvent;
import be4rjp.kuroko.event.AsyncNPCSpeechInitializeEvent;
import be4rjp.kuroko.player.KurokoPlayer;
import be4rjp.kuroko.player.PlayerChunkBaseNPCMap;
import be4rjp.kuroko.script.NPCScript;
import be4rjp.kuroko.script.ScriptRunner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NPC {
    
    //NPCの基礎データ
    private final NPCData npcData;
    //このNPCを見せるプレイヤー
    private final KurokoPlayer kurokoPlayer;
    //再生に使用する映像プレイヤー
    private final ScenePlayer scenePlayer;
    //NPCTracker
    private final PlayerChunkBaseNPCMap playerChunkBaseNPCMap;
    
    //現在のセリフ
    private Speech currentSpeech;
    //現在のtalk index
    private int currentTalkIndex = 0;
    
    //話しかけるときのクールタイム
    private long talkCoolTime = 0;

    //スクリプト
    private final ScriptRunner scriptRunner;
    //スパイク
    private Map<Integer, String> spikeMap = new ConcurrentHashMap<>();
    
    
    public NPC(NPCData npcData, KurokoPlayer kurokoPlayer, PlayerChunkBaseNPCMap playerChunkBaseNPCMap){
        this.npcData = npcData;
        this.kurokoPlayer = kurokoPlayer;
        this.scenePlayer = new ScenePlayer(npcData.getRecordData(), npcData.getBaseLocation().getWorld(), npcData.getStartTick(), npcData.getEndTick());
        this.playerChunkBaseNPCMap = playerChunkBaseNPCMap;

        NPCScript npcScript = npcData.getNpcScript();
        if(npcScript == null){
            this.scriptRunner = null;
        }else{
            this.scriptRunner = npcScript.createScriptRunner();
        }

        this.runScriptFunction("initialize", this);

        scenePlayer.addAudience(kurokoPlayer.getPlayer());
        scenePlayer.initialize();
        scenePlayer.getRunnableSet().add(() -> {
            int tick = scenePlayer.getTick();
            if(spikeMap.containsKey(tick)){
                runScriptFunction(spikeMap.get(tick));
            }
        });
        scenePlayer.start(npcData.isLoop() ? ScenePlayer.PlayMode.LOOP : ScenePlayer.PlayMode.ALL_PLAY);
        scenePlayer.getCancelRunnableSet().add(() -> {
            if(npcData.isLoop()) NPC.this.playerChunkBaseNPCMap.getTrackedNPC().remove(NPC.this);
            else {
                NPC.this.playerChunkBaseNPCMap.removeNPCData(npcData);
                NPC.this.playerChunkBaseNPCMap.getTrackedNPC().remove(NPC.this);
            }
        });
    }

    public void playAnimation(String animation){
        this.playAnimation(Animation.valueOf(animation));
    }
    
    public void playAnimation(Animation animation){
        Object npc = this.getEntityPlayerInstance();
        if(npc == null) return;
    
        try {
            if (animation.isPose()) {
                
                Object dataWatcher = NMSUtil.getDataWatcher(npc);
                NMSUtil.setEntityPose(dataWatcher, animation.getEntityPoseEnumName());
                Object metadataPacket = NMSUtil.createEntityMetadataPacket(npc);
                
                for(Player audience : scenePlayer.getAudiences()){
                    NMSUtil.sendPacket(audience, metadataPacket);
                }
                
            } else {
                
                Object animationPacket = NMSUtil.createEntityAnimationPacket(npc, animation.getAnimationNumber());
                
                for(Player audience : scenePlayer.getAudiences()){
                    NMSUtil.sendPacket(audience, animationPacket);
                }
                
            }
        }catch (Exception e){e.printStackTrace();}
    }
    
    public synchronized void talk(){
        if(talkCoolTime > System.currentTimeMillis() || npcData.getSpeeches() == null) return;

        this.runScriptFunction("onClick", kurokoPlayer, this);

        if(npcData.isDisableSpeech()) return;

        Player player = kurokoPlayer.getPlayer();
        
        if(currentSpeech == null){
            currentSpeech = npcData.getRandomSpeech();
            AsyncNPCSpeechInitializeEvent initializeEvent = new AsyncNPCSpeechInitializeEvent(this, currentSpeech, kurokoPlayer);
            Bukkit.getPluginManager().callEvent(initializeEvent);
            currentSpeech = initializeEvent.getSpeech();
            
            currentTalkIndex = 0;
            
            if(npcData.getNpcScript() == null) scenePlayer.setPause(true);
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
                
            } else if (talkLine.contains("animation{")) {
    
                String temp = talkLine;
                talkLine = talkLine.replace("animation{", "").replace("}", "");
                try {
                    Animation animation = Animation.valueOf(talkLine);
                    this.playAnimation(animation);
                }catch (Exception e){
                    Kuroko.getPlugin().getLogger().warning("Syntax error => " + temp);
                    Kuroko.getPlugin().getLogger().warning(e.getMessage());
                }
                
            }else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', talkLine));
            }
        }
        
        
        currentTalkIndex++;
    
        
        String nextLine = currentSpeech.getLine(currentTalkIndex);
        if(nextLine == null){
            AsyncNPCSpeechEndEvent speechEndEvent = new AsyncNPCSpeechEndEvent(this, currentSpeech, kurokoPlayer);
            Bukkit.getPluginManager().callEvent(speechEndEvent);
            currentSpeech = null;
            currentTalkIndex = 0;
            if(npcData.getNpcScript() == null) Bukkit.getScheduler().runTaskLaterAsynchronously(Kuroko.getPlugin(), () -> scenePlayer.setPause(false), 25);
            
            this.runScriptFunction("onSpeechEnd", kurokoPlayer, this);
            
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

    public void runScriptFunction(String function, Object... objects){
        if(this.scriptRunner == null) return;
        this.scriptRunner.runFunction(function, objects);
    }
    
    public void unload(){scenePlayer.cancel();}
    
    public NPCData getNpcData() {return npcData;}
    
    public int getCurrentTalkIndex() {return currentTalkIndex;}
    
    public Speech getCurrentSpeech() {return currentSpeech;}
    
    public ScenePlayer getScenePlayer() {return scenePlayer;}

    public KurokoPlayer getKurokoPlayer() {return kurokoPlayer;}

    public void setSpikeFunction(int i, String function) {this.spikeMap.put(i, function);}
    
    public void removeSpikeFunction(int i){this.spikeMap.remove(i);}

    public void setPause(boolean is){this.scenePlayer.setPause(is);}
    
    public int getTick(){return this.scenePlayer.getTick();}

    public void setSpeech(Speech speech){
        this.currentSpeech = speech;
        this.currentTalkIndex = 0;
        this.setPause(true);
    }
    
    public boolean hasSpeech(){return currentSpeech != null;}
    
    public Object getEntityPlayerInstance(){
        for(TrackData trackData : scenePlayer.getRecordData().getTrackData()){
            if(trackData instanceof PlayerTrackData){
                PlayerTrackData playerTrackData = (PlayerTrackData) trackData;
                return playerTrackData.getNPC(scenePlayer.getID());
            }
        }
        
        return null;
    }
}
