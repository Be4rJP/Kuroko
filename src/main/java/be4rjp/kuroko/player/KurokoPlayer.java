package be4rjp.kuroko.player;

import be4rjp.cinema4c.recorder.SceneRecorder;
import be4rjp.cinema4c.util.TaskHandler;
import be4rjp.kuroko.npc.NPCData;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class KurokoPlayer {
    
    private static final Map<Player, KurokoPlayer> playerMap = new HashMap<>();
    
    public static KurokoPlayer getKurokoPlayer(Player player){return playerMap.get(player);}
    
    public static KurokoPlayer addPlayer(Player player){return playerMap.computeIfAbsent(player, KurokoPlayer::new);}
    
    private static void removePlayer(Player player){playerMap.remove(player);}
    
    public static void reload(){playerMap.clear();}
    
    
    
    private final Player player;
    
    private final AsyncNPCTracker asyncNPCTracker;
    
    private final Map<World, PlayerChunkBaseNPCMap> chunkBaseNPCMaps = new HashMap<>();
    
    private boolean unloaded = false;

    private String currentSetupNPCName = null;
    
    private SceneRecorder sceneRecorder = null;
    
    public synchronized PlayerChunkBaseNPCMap getPlayerChunkBaseNPCMaps(World world) {return chunkBaseNPCMaps.computeIfAbsent(world, PlayerChunkBaseNPCMap::new);}
    
    public synchronized PlayerChunkBaseNPCMap getPlayerChunkBaseNPCMapsNotCompute(World world) {return chunkBaseNPCMaps.get(world);}
    
    public KurokoPlayer(Player player){
        this.player = player;
        this.asyncNPCTracker = new AsyncNPCTracker(this);
        asyncNPCTracker.start();
    }
    
    public Player getPlayer() {return player;}
    
    public synchronized void addNPC(NPCData npcData){
        if(unloaded) return;
        
        PlayerChunkBaseNPCMap playerChunkBaseNPCMap = this.getPlayerChunkBaseNPCMaps(npcData.getBaseLocation().getWorld());
        playerChunkBaseNPCMap.addNPC(npcData);
    }
    
    public synchronized void removeNPC(NPCData npcData){
        PlayerChunkBaseNPCMap playerChunkBaseNPCMap = this.getPlayerChunkBaseNPCMapsNotCompute(npcData.getBaseLocation().getWorld());
        if(playerChunkBaseNPCMap != null) playerChunkBaseNPCMap.removeNPCData(npcData);
    }
    
    public synchronized void unload(){
        if(unloaded) return;
        
        asyncNPCTracker.cancel();
        removePlayer(player);
        
        unloaded = true;
    
        TaskHandler.runAsync(() -> {
            chunkBaseNPCMaps.values().forEach(PlayerChunkBaseNPCMap::unloadAllChunkNPC);
        });
    }

    public String getCurrentSetupNPCName() {return currentSetupNPCName;}

    public void setCurrentSetupNPCName(String currentSetupNPCName) {this.currentSetupNPCName = currentSetupNPCName;}
    
    public SceneRecorder getSceneRecorder() {return sceneRecorder;}
    
    public void setSceneRecorder(SceneRecorder sceneRecorder) {this.sceneRecorder = sceneRecorder;}
}
