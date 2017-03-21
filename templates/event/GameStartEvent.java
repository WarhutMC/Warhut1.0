package cyclegame.templates.event;

import cyclegame.players.ProPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by luke on 10/18/15.
 */
public class GameStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public GameStartEvent() {
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


}
