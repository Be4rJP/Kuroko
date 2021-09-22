package be4rjp.kuroko.event;

import be4rjp.kuroko.npc.NPC;
import be4rjp.kuroko.npc.Speech;
import be4rjp.kuroko.player.KurokoPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncNPCSpeechInitializeEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    
    private final NPC npc;
    private final KurokoPlayer kurokoPlayer;
    private Speech speech;
    
    public AsyncNPCSpeechInitializeEvent(NPC npc, Speech speech, KurokoPlayer kurokoPlayer){
        super(true);
        this.npc = npc;
        this.kurokoPlayer = kurokoPlayer;
        this.speech = speech;
    }
    
    public NPC getNpc() {return npc;}
    
    public Speech getSpeech() {return speech;}
    
    public void setSpeech(Speech speech) {this.speech = speech;}
    
    public KurokoPlayer getPlayer(){return kurokoPlayer;}
    
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
