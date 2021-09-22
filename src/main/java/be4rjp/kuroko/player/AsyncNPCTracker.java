package be4rjp.kuroko.player;

import be4rjp.kuroko.Kuroko;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncNPCTracker extends BukkitRunnable {
    
    private final KurokoPlayer kurokoPlayer;
    
    private World previousWorld;
    
    public AsyncNPCTracker(KurokoPlayer kurokoPlayer){
        this.kurokoPlayer = kurokoPlayer;
        this.previousWorld = kurokoPlayer.getPlayer().getWorld();
    }
    
    @Override
    public void run() {
        World currentWorld = kurokoPlayer.getPlayer().getWorld();
        
        if(previousWorld != currentWorld){
            kurokoPlayer.getPlayerChunkBaseNPCMaps(previousWorld).switchWorldUnloadAllChunkNPC(kurokoPlayer);
        }
        
        kurokoPlayer.getPlayerChunkBaseNPCMaps(currentWorld).updateChunkNPC(kurokoPlayer);
        
        this.previousWorld = currentWorld;
    }
    
    public void start(){
        this.runTaskTimerAsynchronously(Kuroko.getPlugin(), 0, 50);
    }
}
