package be4rjp.kuroko.event;

import be4rjp.kuroko.player.KurokoPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncPlayerNPCDataLoadEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    
    private final KurokoPlayer kurokoPlayer;
    
    public AsyncPlayerNPCDataLoadEvent(KurokoPlayer kurokoPlayer){
        super(true);
        this.kurokoPlayer = kurokoPlayer;
    }
    
    public KurokoPlayer getKurokoPlayer() {return kurokoPlayer;}
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    
}
