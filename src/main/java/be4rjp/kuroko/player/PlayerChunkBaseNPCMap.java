package be4rjp.kuroko.player;

import be4rjp.cinema4c.data.record.tracking.PlayerTrackData;
import be4rjp.cinema4c.data.record.tracking.TrackData;
import be4rjp.cinema4c.nms.NMSUtil;
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
                    npc.unload();
                    trackedNPC.remove(npc);
                }
            }catch (Exception e){e.printStackTrace();}
        }
    
        for(ChunkPosition chunkPosition : chunkPositions){
            Set<NPCData> npcDataSet = chunkNPCDataMap.get(chunkPosition);
            if(npcDataSet == null) continue;
            
            for(NPCData npcData : chunkNPCDataMap.get(chunkPosition)){
                boolean contains = false;
                for(NPC npc : trackedNPC){
                    if(npc.getNpcData() == npcData){
                        contains = true;
                        break;
                    }
                }
        
                if(!contains && npcData.isDistanceUnload()){
                    TaskHandler.runSync(() -> trackedNPC.add(new NPC(npcData, kurokoPlayer.getPlayer())));
                }
            }
        }
    }
    
    
    public void unloadAllChunkNPC(){
        trackedNPC.forEach(NPC::unload);
    }
    
    public Set<NPC> getTrackedNPC() {return trackedNPC;}
    
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
