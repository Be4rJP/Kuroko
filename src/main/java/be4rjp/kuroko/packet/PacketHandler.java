package be4rjp.kuroko.packet;

import be4rjp.cinema4c.data.record.tracking.PlayerTrackData;
import be4rjp.cinema4c.data.record.tracking.TrackData;
import be4rjp.cinema4c.nms.NMSUtil;
import be4rjp.kuroko.npc.NPC;
import be4rjp.kuroko.player.KurokoPlayer;
import io.netty.channel.*;


import java.lang.reflect.Field;

public class PacketHandler extends ChannelDuplexHandler{
    
    private static Field a;
    
    static {
        try {
            Class<?> packetPlayInUseEntity = NMSUtil.getNMSClass("PacketPlayInUseEntity");
            a = packetPlayInUseEntity.getDeclaredField("a");
            a.setAccessible(true);
        }catch (Exception e){e.printStackTrace();}
    }
    
    
    private final KurokoPlayer kurokoPlayer;
    
    public PacketHandler(KurokoPlayer kurokoPlayer){
        this.kurokoPlayer = kurokoPlayer;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
        
        if(packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")){
            
            try {
                for (NPC npc : kurokoPlayer.getPlayerChunkBaseNPCMaps(kurokoPlayer.getPlayer().getWorld()).getTrackedNPC()) {
                    for (TrackData trackData : npc.getScenePlayer().getRecordData().getTrackData()) {
                        if (trackData instanceof PlayerTrackData) {
                            PlayerTrackData playerTrackData = (PlayerTrackData) trackData;
                            if (NMSUtil.getEntityID(playerTrackData.getNPC(npc.getScenePlayer().getID())) == a.getInt(packet)) {
                                npc.talk();
                                return;
                            }
                        }
                    }
                }
            }catch (Exception e){e.printStackTrace();}
            
            return;
        }
        
        super.channelRead(channelHandlerContext, packet);
    }
}
