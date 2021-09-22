package be4rjp.kuroko.event;

import be4rjp.kuroko.npc.NPC;
import be4rjp.kuroko.npc.Speech;
import be4rjp.kuroko.player.KurokoPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncNPCSpeechEndEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    
    private final NPC npc;
    private final Speech endSpeech;
    private final KurokoPlayer kurokoPlayer;
    
    public AsyncNPCSpeechEndEvent(NPC npc, Speech endSpeech, KurokoPlayer kurokoPlayer){
        super(true);
        this.npc = npc;
        this.endSpeech = endSpeech;
        this.kurokoPlayer = kurokoPlayer;
    }
    
    public NPC getNpc() {return npc;}
    
    public Speech getEndSpeech() {return endSpeech;}
    
    public KurokoPlayer getPlayer() {return kurokoPlayer;}
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
