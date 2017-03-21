package cyclegame.games.ctf.flags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cyclegame.games.ctf.Ctf;
import cyclegame.games.domination.Domination;
import cyclegame.templates.game.Team;
import cyclegame.GameAPI;

import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import common.util.LocationUtils;
import common.util.math.MathUtils;

public class FlagManager implements Listener {

	public HashMap<Team, Integer> points = new HashMap<>();
	
	public Ctf ctf;
	public ArrayList<Flag> flags = new ArrayList<Flag>();

	public int captureRunnableId;
	
	public FlagManager(final Ctf ctf){
		this.ctf = ctf;

		this.captureRunnableId = startBeaconLights();

		/* load flags from JSON */
		
		//TODO Luke, gunna let you do this :P
		JsonArray array = ctf.map.getJsonObject().getAsJsonArray("flag_locations");
		Iterator<JsonElement> it = array.iterator();
		while (it.hasNext()) {
			JsonObject json = it.next().getAsJsonObject();

			String name = json.get("team").getAsString();
			

			String coordsString = json.get("coords").getAsString();
			Location location = LocationUtils.convert(ctf.world, coordsString);

			//TODO include team, direction, and 
			//this.flags.add(new Flag(location, (TEAM), (DIRECTION), this.ctf));
		}

		this.points.put(ctf.blueTeam, 0);
		this.points.put(ctf.redTeam, 0);

		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
	}
	
	public int startBeaconLights(){
		return Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable(){

			@Override
			public void run() {
				for(int i = 0; i < flags.size(); i++){
					
					Flag flag = flags.get(i);

					for(int n = 0; n < 100; n++){
						flag.getCurrentLocation().getWorld().playEffect(LocationUtils.add(flag.getCurrentLocation(), 0, n/2, 0), Effect.STEP_SOUND, flag.getTeam().getBlockData());
					}
				}
			}
			
		}, 10, 10);
	}
	
	/*
	 * Events
	 */
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Player player = e.getPlayer();
		
		Team team = GameAPI.getMatch().game.getTeam(player);
		
		for(Flag flag : this.flags){
			
			//Flag Capture
			if(this.isInsideCaptureRadiusOfHomeTeam(flag.getCurrentLocation(), team)){
				if(flag.isTaken()){
					if(flag.getCarrier() != null){
						if(flag.getCarrier() == player){
							
							Bukkit.getServer().getPluginManager().callEvent(new FlagCaptureEvent(flag, player));
							
						}
					}
				}
			}

			
			//Flag Pickup
			if(!team.equals(flag.getTeam())){
				if(!flag.isTaken()){
					Location location = flag.getCurrentLocation();
					if(player.getLocation().distance(location) <= 1.5){
						
						Bukkit.getServer().getPluginManager().callEvent(new FlagPickupEvent(flag, player));
						
					}
				}
			}
		}

	}
	
	public ArrayList<Flag> getFlags(){
		return this.flags;
	}
	
	public void unload(){
		flags.clear();
	}
	
	public boolean isInsideCapturePoint(Location location) {
		for (Flag flag : this.flags) {
			int minx = (int) MathUtils.min(flag.getFlagSpawnLocation().getX(), location.getX());
			int maxX = (int) MathUtils.max(flag.getFlagSpawnLocation().getX(), location.getX());

			int minZ = (int) MathUtils.min(flag.getFlagSpawnLocation().getZ(), location.getZ());
			int maxZ = (int) MathUtils.max(flag.getFlagSpawnLocation().getZ(), location.getZ());

			if (maxX - minx <= ctf.captureRadius && maxZ - minZ <= ctf.captureRadius) {
				return true;
			}
		}
		return false;
	}

	public boolean isInsideCaptureRadiusOfHomeTeam(Location location, Team team) {
		for (Flag flag : this.flags) {
			int minx = (int) MathUtils.min(flag.getFlagSpawnLocation().getX(), location.getX());
			int maxX = (int) MathUtils.max(flag.getFlagSpawnLocation().getX(), location.getX());

			int minZ = (int) MathUtils.min(flag.getFlagSpawnLocation().getZ(), location.getZ());
			int maxZ = (int) MathUtils.max(flag.getFlagSpawnLocation().getZ(), location.getZ());

			if (maxX - minx <= ctf.captureRadius && maxZ - minZ <= ctf.captureRadius) {
				if(team.equals(flag.getTeam())){
					return true;
				}
			}
		}
		return false;
	}
	
}