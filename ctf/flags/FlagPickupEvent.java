package cyclegame.games.ctf.flags;

import cyclegame.GameAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FlagPickupEvent extends Event {

	private Flag flag;
	private Player player;

	public FlagPickupEvent(Flag flag, Player player){
		this.flag = flag;
		this.player = player;
		
		if(!flag.getTeam().equals(GameAPI.getMatch().game.getTeam(player))){
			flag.setCarrier(player);
		}
	}
	
	/*
	 * Getters
	 */
	
	public Flag getFlag(){
		return this.flag;
	}
	
	public Player getPlayer(){
		return this.player;
	}

	private static final HandlerList handlers = new HandlerList();
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
}