package be4rjp.kuroko.event;

import be4rjp.kuroko.npc.NPC;
import be4rjp.kuroko.npc.Speech;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncNPCSpeechInitializeEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    
    private final NPC npc;
    private final Player player;
    private Speech speech;
    
    public AsyncNPCSpeechInitializeEvent(NPC npc, Speech speech, Player player){
        super(true);
        this.npc = npc;
        this.player = player;
        this.speech = speech;
    }
    
    public NPC getNpc() {return npc;}
    
    public Speech getSpeech() {return speech;}
    
    public void setSpeech(Speech speech) {this.speech = speech;}
    
    public Player getPlayer(){return player;}
    
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
