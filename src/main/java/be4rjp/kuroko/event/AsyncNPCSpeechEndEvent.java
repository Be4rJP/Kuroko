package be4rjp.kuroko.event;

import be4rjp.kuroko.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncNPCSpeechEndEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    
    private final NPC npc;
    private final int endSpeechIndex;
    private final Player audience;
    
    public AsyncNPCSpeechEndEvent(NPC npc, int endSpeechIndex, Player audience){
        super(true);
        this.npc = npc;
        this.endSpeechIndex = endSpeechIndex;
        this.audience = audience;
    }
    
    public NPC getNpc() {return npc;}
    
    public int getEndSpeechIndex() {return endSpeechIndex;}
    
    public Player getPlayer() {return audience;}
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
