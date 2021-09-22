package be4rjp.kuroko.event;

import be4rjp.kuroko.npc.NPC;
import be4rjp.kuroko.player.KurokoPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncNPCTalkEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final NPC npc;
    
    private final KurokoPlayer kurokoPlayer;
    
    public AsyncNPCTalkEvent(NPC npc, KurokoPlayer kurokoPlayer){
        super(true);
        this.npc = npc;
        this.kurokoPlayer = kurokoPlayer;
    }
    
    public NPC getNpc() {return npc;}
    
    public KurokoPlayer getPlayer() {return kurokoPlayer;}
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    
}
