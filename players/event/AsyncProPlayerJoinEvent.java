package cyclegame.players.event;

import cyclegame.players.ProPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by luke on 10/18/15.
 */
public class AsyncProPlayerJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public ProPlayer proPlayer;
    public boolean isNewPlayer;

    public AsyncProPlayerJoinEvent(ProPlayer proPlayer, boolean isNewPlayer) {
        this.proPlayer = proPlayer;
        this.isNewPlayer = isNewPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


}
