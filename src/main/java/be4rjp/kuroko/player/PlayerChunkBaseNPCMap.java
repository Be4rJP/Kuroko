package be4rjp.kuroko.player;

import be4rjp.cinema4c.data.record.tracking.PlayerTrackData;
import be4rjp.cinema4c.data.record.tracking.TrackData;
import be4rjp.cinema4c.nms.NMSUtil;
import be4rjp.cinema4c.util.LocationUtil;
import be4rjp.cinema4c.util.TaskHandler;
import be4rjp.kuroko.Config;
import be4rjp.kuroko.npc.NPC;
import be4rjp.kuroko.npc.NPCData;
import be4rjp.kuroko.util.ChunkPosition;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerChunkBaseNPCMap {
    
    private final World world;
    
    private final Map<ChunkPosition, Set<NPCData>> chunkNPCDataMap = new ConcurrentHashMap<>();
    
    private final Set<NPC> trackedNPC = ConcurrentHashMap.newKeySet();
    
    private final Set<NPC> hideNPC = ConcurrentHashMap.newKeySet();
    
    public PlayerChunkBaseNPCMap(World world){
        this.world = world;
    }
    
    
    public void addNPC(NPCData npcData){
        Location baseLocation = npcData.getBaseLocation();
        ChunkPosition chunkPosition = new ChunkPosition(baseLocation.getBlockX() >> 4, baseLocation.getBlockZ() >> 4);
        chunkNPCDataMap.computeIfAbsent(chunkPosition, k -> ConcurrentHashMap.newKeySet()).add(npcData);
    }
    
    
    public void updateChunkNPC(KurokoPlayer kurokoPlayer){
        Set<ChunkPosition> chunkPositions = getRangeChunks(kurokoPlayer);
        
        for(NPC npc : trackedNPC){
            Object nmsNPC = null;
            for(TrackData trackData : npc.getScenePlayer().getRecordData().getTrackData()){
                if(trackData instanceof PlayerTrackData){
                    PlayerTrackData playerTrackData = (PlayerTrackData) trackData;
                    nmsNPC = playerTrackData.getNPC(npc.getScenePlayer().getID());
                }
            }
            if(nmsNPC == null) continue;
            
            try {
                Location npcLocation = NMSUtil.getEntityLocation(nmsNPC);
                ChunkPosition npcChunk = new ChunkPosition(npcLocation.getBlockX() >> 4, npcLocation.getBlockZ() >> 4);
                if(!chunkPositions.contains(npcChunk)){
                    if(npc.getNpcData().isDistanceUnload()) {
                        npc.unload();
                        trackedNPC.remove(npc);
                    }else{
                        NMSUtil.sendPacket(kurokoPlayer.getPlayer(), NMSUtil.createEntityDestroyPacket(nmsNPC));
                        hideNPC.add(npc);
                    }
                }
    
                
                double distance = npc.getNpcData().getSpeechResetDistance();
                if(distance > 0){
                    if(LocationUtil.distanceSquaredSafeDifferentWorld(kurokoPlayer.getPlayer().getLocation(), npcLocation) > distance * distance){
                        npc.resetSpeech();
                    }
                }
                
            }catch (Exception e){e.printStackTrace();}
        }
    
        for(ChunkPosition chunkPosition : chunkPositions){
            Set<NPCData> npcDataSet = chunkNPCDataMap.get(chunkPosition);
            if(npcDataSet == null) continue;
            
            for(NPCData npcData : chunkNPCDataMap.get(chunkPosition)){
                
                NPC npcInstance = null;
                for(NPC npc : trackedNPC){
                    if(npc.getNpcData() == npcData){
                        npcInstance = npc;
                        break;
                    }
                }
        
                if(npcInstance == null){
                    TaskHandler.runSync(() -> trackedNPC.add(new NPC(npcData, kurokoPlayer, PlayerChunkBaseNPCMap.this)));
                }else{
                    
                    boolean isHide = false;
                    for(NPC npc : hideNPC){
                        if(npc.getNpcData() == npcData){
                            isHide = true;
                            break;
                        }
                    }
                    
                    if(!npcData.isDistanceUnload() && isHide) {
                        for(TrackData trackData : npcInstance.getScenePlayer().getRecordData().getTrackData()){
                            if(trackData instanceof PlayerTrackData){
                                PlayerTrackData playerTrackData = (PlayerTrackData) trackData;
                                playerTrackData.spawnNPC(npcInstance.getScenePlayer());
                            }
                        }
                        hideNPC.remove(npcInstance);
                    }
                }
            }
        }
    }
    
    
    public void switchWorldUnloadAllChunkNPC(KurokoPlayer kurokoPlayer){
        for(NPC npc : trackedNPC){
            if(npc.getNpcData().isDistanceUnload()){
                npc.unload();
            }else{
                Object nmsNPC = null;
                for(TrackData trackData : npc.getScenePlayer().getRecordData().getTrackData()){
                    if(trackData instanceof PlayerTrackData){
                        PlayerTrackData playerTrackData = (PlayerTrackData) trackData;
                        nmsNPC = playerTrackData.getNPC(npc.getScenePlayer().getID());
                    }
                }
                if(nmsNPC == null) continue;
    
                try {
                    NMSUtil.sendPacket(kurokoPlayer.getPlayer(), NMSUtil.createEntityDestroyPacket(nmsNPC));
                    hideNPC.add(npc);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }
    
    public void unloadAllChunkNPC(){trackedNPC.forEach(NPC::unload);}
    
    public Set<NPC> getTrackedNPC() {return trackedNPC;}
    
    public void removeNPCData(NPCData npcData){
        for(Map.Entry<ChunkPosition, Set<NPCData>> entry : chunkNPCDataMap.entrySet()){
            ChunkPosition chunkPosition = entry.getKey();
            Set<NPCData> npcDataSet = entry.getValue();
            
            npcDataSet.remove(npcData);
        }
    }
    
    public static Set<ChunkPosition> getRangeChunks(KurokoPlayer kurokoPlayer){
        int range = Config.getViewDistance();
        Location location = kurokoPlayer.getPlayer().getLocation();
        Set<ChunkPosition> chunkPositions = new HashSet<>();
        for(int x = -range; x < range; x++){
            for(int z = -range; z < range; z++){
                chunkPositions.add(new ChunkPosition((location.getBlockX() >> 4) + x, (location.getBlockZ() >> 4) + z));
            }
        }
        
        return chunkPositions;
    }
}
