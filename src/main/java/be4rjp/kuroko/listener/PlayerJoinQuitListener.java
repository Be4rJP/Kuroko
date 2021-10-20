package be4rjp.kuroko.listener;

import be4rjp.cinema4c.nms.NMSUtil;
import be4rjp.cinema4c.player.ScenePlayer;
import be4rjp.cinema4c.recorder.RecordManager;
import be4rjp.cinema4c.util.TaskHandler;
import be4rjp.kuroko.Config;
import be4rjp.kuroko.Kuroko;
import be4rjp.kuroko.event.AsyncPlayerNPCDataLoadEvent;
import be4rjp.kuroko.npc.NPCData;
import be4rjp.kuroko.packet.PacketHandler;
import be4rjp.kuroko.player.KurokoPlayer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerJoinQuitListener implements Listener {
    
    private static Set<NPCData> defaultLoadData = null;
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        KurokoPlayer kurokoPlayer = KurokoPlayer.addPlayer(player);
        
        TaskHandler.runAsync(() -> {
            if(defaultLoadData == null){
                Set<NPCData> defaultLoadDataSet = ConcurrentHashMap.newKeySet();
                for(String name : Config.getDefaultLoadNPCList()){
                    NPCData npcData = NPCData.getNPCData(name);
                    if(npcData == null){
                        Kuroko.getPlugin().getLogger().warning(name + " is not found.");
                        continue;
                    }
                    
                    defaultLoadDataSet.add(npcData);
                }
                
                defaultLoadData = defaultLoadDataSet;
            }
            
            defaultLoadData.forEach(kurokoPlayer::addNPC);
            
            AsyncPlayerNPCDataLoadEvent loadEvent = new AsyncPlayerNPCDataLoadEvent(kurokoPlayer);
            Bukkit.getPluginManager().callEvent(loadEvent);
        });
    
        PacketHandler packetHandler = new PacketHandler(kurokoPlayer);
    
        try {
            ChannelPipeline pipeline = NMSUtil.getChannel(player).pipeline();
            pipeline.addBefore("packet_handler", Kuroko.getPlugin().getName() + "PacketInjector:" + player.getName(), packetHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        KurokoPlayer.getKurokoPlayer(player).unload();
    
        try {
            Channel channel = NMSUtil.getChannel(player);
        
            channel.eventLoop().submit(() -> {
                channel.pipeline().remove(Kuroko.getPlugin().getName() + "PacketInjector:" + player.getName());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
