package cyclegame.games.domination.dominationzone;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cyclegame.players.ProPlayer;
import common.util.LocationUtils;
import common.util.communication.C;
import common.util.communication.F;
import common.util.communication.S;
import common.util.math.MathUtils;
import cyclegame.games.domination.Domination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cyclegame.templates.game.Team;
import cyclegame.GameAPI;

import cyclegame.templates.game.spectate.SpectateManager;
import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import common.util.particles.ParticleEffect;

public class DominationZoneManager implements Listener {
	public HashMap<Team, Integer> points = new HashMap<>();
	
	public Domination domination;
	public ArrayList<DominationZone> zones = new ArrayList<DominationZone>();

	public int captureRunnableId;
	public int pointRunnableId;

	public DominationZoneManager(final Domination domination){
		this.domination = domination;

		this.captureRunnableId = startCaptureCheck();

		/* load zones from json */
		JsonArray array = domination.map.getJsonObject().getAsJsonArray("capture_points");
		Iterator<JsonElement> it = array.iterator();
		while (it.hasNext()) {
			JsonObject json = it.next().getAsJsonObject();

			String name = json.get("name").getAsString();

			String coordsString = json.get("coords").getAsString();
			Location location = LocationUtils.convert(domination.world, coordsString);

			this.zones.add(new DominationZone(location, name, domination));
		}

		this.points.put(domination.blueTeam, 0);
		this.points.put(domination.redTeam, 0);

		this.pointRunnableId = startPointRunnable();

		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
	}

	public int startPointRunnable() {
		return GameAPI.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (DominationZone zone : zones) {
					if (zone.getOwnerTeam() != null) {
						int updatedPoints = points.get(zone.getOwnerTeam()) + 1;
						points.put(zone.getOwnerTeam(), updatedPoints);

						if (updatedPoints >= Domination.pointsToWin) {
							unload();

							domination.endGame(zone.getOwnerTeam());
							return;
						}
					}
				}
			}
		}, 20L, 20L);
	}

	@EventHandler
	public void onCapture(DominationZoneCompleteCaptureEvent e){
		for(Location l : e.getDominationZone().getBlocks()){
			ParticleEffect.PORTAL.display(1, 1, 1, 0, 3, l, 40);
		}

		S.playSound(Sound.ENDERMAN_TELEPORT);
		F.broadcast("Domination", e.getTeam().color + C.bold + e.getTeam().name + C.white + " captured " + C.yellow  + e.getDominationZone().getName());

		e.getDominationZone().getCenter().getBlock().setType(Material.STAINED_GLASS);
		e.getDominationZone().getCenter().getBlock().setData((byte) e.getTeam().getBlockData());
	}

	public int startCaptureCheck(){
		return GameAPI.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable(){

			@Override
			public void run() {


				for (DominationZone zone : zones) {
					int redCount = 0;
					int blueCount = 0;

					for (ProPlayer proPlayer : GameAPI.getPlayerHandler().getProPlayers()) {
						if (SpectateManager.isSpectator(proPlayer.player)) {
							continue;
						}
						double maxX = MathUtils.max(zone.getCenter().getX(), proPlayer.getPlayer().getLocation().getX());
						double minX = MathUtils.min(zone.getCenter().getX(), proPlayer.getPlayer().getLocation().getX());

						double maxZ = MathUtils.max(zone.getCenter().getZ(), proPlayer.getPlayer().getLocation().getZ());
						double minZ = MathUtils.min(zone.getCenter().getZ(), proPlayer.getPlayer().getLocation().getZ());

						double maxY = MathUtils.max(zone.getCenter().getY(), proPlayer.player.getLocation().getY());
						double minY = MathUtils.min(zone.getCenter().getY(), proPlayer.player.getLocation().getY());

						if (maxX - minX <= zone.radius && maxZ - minZ <= zone.radius) {
							if (maxY - minY <= 5) {
								Team team = domination.getTeam(proPlayer.player);

								if (team == domination.blueTeam) {
									blueCount++;
								} else {
									redCount++;
								}
							}
						}
					}

					if (redCount > 0 && blueCount == 0) {
						Bukkit.getServer().getPluginManager().callEvent(new DominationZoneClaimEvent(zone, domination.redTeam));
					} else if (blueCount > 0 && redCount == 0) {
						Bukkit.getServer().getPluginManager().callEvent(new DominationZoneClaimEvent(zone, domination.blueTeam));
					} else {
						//tie
					}
				}


					
			}
			
		}, 20, 20);
	}

	public boolean isInsideCapturePoint(Location location) {
		for (DominationZone zone : this.zones) {
			int minx = (int) MathUtils.min(zone.getCenter().getX(), location.getX());
			int maxX = (int) MathUtils.max(zone.getCenter().getX(), location.getX());

			int minZ = (int) MathUtils.min(zone.getCenter().getZ(), location.getZ());
			int maxZ = (int) MathUtils.max(zone.getCenter().getZ(), location.getZ());

			if (maxX - minx <= Domination.zoneBlockPlaceRadius && maxZ - minZ <= Domination.zoneBlockPlaceRadius) {
				return true;
			}
		}
		return false;
	}

	public void unload() {
		Main.getScheduler().cancelTask(this.captureRunnableId);
		Main.getScheduler().cancelTask(this.pointRunnableId);
		Main.unloadListener(this);
	}

	public HashMap<Team, Integer> getPoints() {
		return points;
	}

	public Domination getDomination() {
		return domination;
	}

	public ArrayList<DominationZone> getZones() {
		return zones;
	}

	public int getCaptureRunnableId() {
		return captureRunnableId;
	}

	public int getPointRunnableId() {
		return pointRunnableId;
	}
}
