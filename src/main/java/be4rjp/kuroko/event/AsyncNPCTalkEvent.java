package be4rjp.kuroko.event;

import be4rjp.kuroko.npc.NPC;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncNPCTalkEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    public AsyncNPCTalkEvent(NPC npc){
        super(true);
        
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    
}
