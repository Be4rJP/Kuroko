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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
    
    
    public NPC(NPCData npcData, KurokoPlayer kurokoPlayer, PlayerChunkBaseNPCMap playerChunkBaseNPCMap){
        this.npcData = npcData;
        this.kurokoPlayer = kurokoPlayer;
        this.scenePlayer = new ScenePlayer(npcData.getRecordData(), npcData.getBaseLocation().getWorld(), npcData.getStartTick(), npcData.getEndTick());
        this.playerChunkBaseNPCMap = playerChunkBaseNPCMap;
        
        scenePlayer.addAudience(kurokoPlayer.getPlayer());
        scenePlayer.initialize();
        scenePlayer.start(npcData.isLoop() ? ScenePlayer.PlayMode.LOOP : ScenePlayer.PlayMode.ALL_PLAY);
        scenePlayer.getCancelRunnableSet().add(() -> NPC.this.playerChunkBaseNPCMap.getTrackedNPC().remove(NPC.this));
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
    
    public void unload(){
        scenePlayer.cancel();
    }
    
    public NPCData getNpcData() {return npcData;}
    
    public int getCurrentTalkIndex() {return currentTalkIndex;}
    
    public Speech getCurrentSpeech() {return currentSpeech;}
    
    public ScenePlayer getScenePlayer() {return scenePlayer;}
    
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
