package be4rjp.kuroko.listener;

import be4rjp.cinema4c.data.record.RecordData;
import be4rjp.cinema4c.data.record.tracking.PlayerTrackData;
import be4rjp.cinema4c.data.record.tracking.TrackData;
import be4rjp.cinema4c.recorder.RecordManager;
import be4rjp.cinema4c.recorder.SceneRecorder;
import be4rjp.cinema4c.util.TaskHandler;
import be4rjp.kuroko.Kuroko;
import be4rjp.kuroko.npc.NPCData;
import be4rjp.kuroko.player.KurokoPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.io.File;
import java.io.IOException;

public class PlayerItemSwitchListener implements Listener {
    
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();
        KurokoPlayer kurokoPlayer = KurokoPlayer.getKurokoPlayer(player);
        
        String npcName = kurokoPlayer.getCurrentSetupNPCName();
        if(npcName == null) return;
        event.setCancelled(true);
    
        Location location = player.getLocation();
        
        if(kurokoPlayer.getSceneRecorder() == null) {
            RecordData recordData = RecordManager.createNewRecordData(npcName);
            PlayerTrackData playerTrackData = new PlayerTrackData(player);
            recordData.addTrackData(playerTrackData);
            SceneRecorder sceneRecorder = RecordManager.createSceneRecorder(recordData, location, location);
            kurokoPlayer.setSceneRecorder(sceneRecorder);
            sceneRecorder.runTaskTimerAsynchronously(Kuroko.getPlugin(), 0, 1);
            player.sendMessage(ChatColor.GREEN + "記録を開始しました");
        }else{
            SceneRecorder sceneRecorder = kurokoPlayer.getSceneRecorder();
            sceneRecorder.cancel();
            TaskHandler.runAsync(() -> sceneRecorder.getRecordData().saveData());
            
            kurokoPlayer.setSceneRecorder(null);
            kurokoPlayer.setCurrentSetupNPCName(null);
            
            RecordData recordData = sceneRecorder.getRecordData();
            int endTick = 0;
            for(TrackData trackData : recordData.getTrackData()){
                endTick = trackData.getEndTick();
            }
    
            NPCData npcData = new NPCData(npcName, sceneRecorder.getRecordData(), location, endTick);
            File file = new File("plugins/Kuroko/data", npcName + ".yml");
            YamlConfiguration yml = new YamlConfiguration();
            npcData.save(yml);
            TaskHandler.runAsync(() -> {
                try {
                    yml.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            player.sendMessage(ChatColor.GREEN + "記録を保存しました");
        }
    }
    
}
