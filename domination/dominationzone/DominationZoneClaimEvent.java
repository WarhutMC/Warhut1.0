package cyclegame.games.domination.dominationzone;

import cyclegame.templates.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DominationZoneClaimEvent extends Event {
		
	private DominationZone zone;
	private Team team;

	public DominationZoneClaimEvent(DominationZone zone, Team team){
		this.zone = zone;
		this.team = team;

		Team beforeTeam = zone.getOwnerTeam();
		int beforeProgress = zone.getProgress();

		zone.addToProgress(team);
		
		if((beforeTeam == null || beforeTeam != team) && (beforeProgress < zone.getProgress() && zone.getProgress() == zone.radius)){ //stops repeating capture messages
			Bukkit.getServer().getPluginManager().callEvent(new DominationZoneCompleteCaptureEvent(zone, team));
		}
	}
	
	/*
	 * Getters
	 */
	
	public Team getTeam(){
		return this.team;
	}
	
	public DominationZone getDominationZone(){
		return this.zone;
	}

	private static final HandlerList handlers = new HandlerList();
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
}
