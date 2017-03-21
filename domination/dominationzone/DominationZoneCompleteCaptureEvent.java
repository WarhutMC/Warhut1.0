package cyclegame.games.domination.dominationzone;

import cyclegame.templates.game.Team;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import common.util.ActionBar;

public class DominationZoneCompleteCaptureEvent extends Event {

	private DominationZone zone;
	private Team team;
	
	public DominationZoneCompleteCaptureEvent(DominationZone zone, Team team){
		this.zone = zone;
		this.team = team;

		zone.setOwnerTeam(team);
		
		ActionBar bar = new ActionBar(team.color + team.name);
		bar.sendToServer();
	}
	
	public DominationZone getDominationZone(){
		return this.zone;
	}
	
	public Team getTeam(){
		return this.team;
	}
	
	private static final HandlerList handlers = new HandlerList();
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
}
