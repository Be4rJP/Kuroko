package be4rjp.kuroko.event;

import be4rjp.kuroko.npc.NPC;
import be4rjp.kuroko.player.KurokoPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncPlayerClickNPCEvent extends Event implements Cancellable {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final KurokoPlayer kurokoPlayer;
    
    private final NPC npc;
    
    private boolean isCancelled = false;
    
    public AsyncPlayerClickNPCEvent(KurokoPlayer kurokoPlayer, NPC npc){
        super(true);
        this.kurokoPlayer = kurokoPlayer;
        this.npc = npc;
    }
    
    public KurokoPlayer getPlayer() {return kurokoPlayer;}
    
    public NPC getNPC() {return npc;}
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
