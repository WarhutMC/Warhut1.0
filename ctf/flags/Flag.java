package cyclegame.games.ctf.flags;

import cyclegame.games.ctf.Ctf;
import cyclegame.templates.game.Game.GameState;
import main.Main;
import cyclegame.templates.game.Team;

import cyclegame.GameAPI;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;

import cyclegame.players.ProPlayer;
import common.util.ActionBar;
import common.util.ColorConverter;
import common.util.LocationUtils;
import common.util.communication.C;
import common.util.fireworks.InstantFirework;

public class Flag {
	
	private Location spawnLocation;
	
	private Team team;
			
	private ItemStack bannerItem;

	private Ctf ctf;
	
	private boolean isAtSpawn;
	
	private BlockFace flagDirection;
	
	private Player carrier;
	
	private Location currentLocation;
	
	private int resetingId;
	
	public Flag (Location location, Team team, BlockFace direction, Ctf ctf){
		
		this.spawnLocation = location;
		
		this.team = team;
		
		this.flagDirection = direction;
		
		this.ctf = ctf;
						
		ItemStack bannerItem = new ItemStack(Material.BANNER, 1, team.getBlockData());
		
		this.bannerItem = bannerItem;
		
		this.isAtSpawn = true;
		
		this.carrier = null;
		
		setCurrentLocation(location);
		
	}
	
	public void setCurrentLocation(Location location){
		this.currentLocation = location;
	}
	
	public void setCarrier(Player player){
		
		if(this.isReturning()){
			Bukkit.getServer().getScheduler().cancelTask(this.resetingId);
		}
		
		this.carrier = player;
		
		player.getInventory().clear();
		
		for(ItemStack item : player.getInventory().getArmorContents()){
			
			item.setType(Material.AIR);
			
		}
		
		player.getInventory().setHelmet(this.bannerItem);
		
		new InstantFirework(FireworkEffect.builder().with(FireworkEffect.Type.BURST).withColor(ColorConverter.getColor(this.team.color)).build(), player.getLocation());
		
		setCurrentLocation(player.getLocation());
		
		isAtSpawn = false;
		
	}
	
	public void dropFlag(Player player){
		if(player.getLocation().getBlock().getType().equals(Material.AIR)){
			
			if(!LocationUtils.add(player.getLocation(), 0, -1, 0).getBlock().getType().equals(Material.AIR)){
				
			
			Block block = player.getLocation().getBlock();
			
			Banner bannerBlock = (Banner) block.getState();
			bannerBlock.setBaseColor(DyeColor.getByData(team.getBlockData()));
			
	        ((Directional) bannerBlock.getData()).setFacingDirection(this.flagDirection);
			
			bannerBlock.update();
			
			this.carrier = null;
			
			setCurrentLocation(player.getLocation());
			
			this.resetingId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable(){

				@Override
				public void run() {
					
					returnToSpawn();
					
				}
				
			}, 10*20);
			
			}else{
				returnToSpawn();
			}
			
		}else{
			returnToSpawn();
		}
	}
	
	public void returnToSpawn(){
		if(GameAPI.getMatch().game.gameState.equals(GameState.PLAYING)){
			if(!isAtSpawn){
			
				placeSpawnBlock();
			
				new InstantFirework(FireworkEffect.builder().with(FireworkEffect.Type.BURST).withColor(ColorConverter.getColor(this.team.color)).build(), this.spawnLocation);
			
				isAtSpawn = true;
			}
		}
	}
	
	public void placeSpawnBlock(){
		
		Banner bannerBlock = (Banner) this.spawnLocation.getBlock().getState();
		bannerBlock.setBaseColor(DyeColor.getByData(team.getBlockData()));
		
        ((Directional) bannerBlock.getData()).setFacingDirection(this.flagDirection);
		
		bannerBlock.update();
				
	}
	
	public void setCaptured(){
		if(GameAPI.getMatch().game.gameState.equals(GameState.PLAYING)){
			if(this.carrier != null){
			
				Team team = GameAPI.getMatch().game.getTeam(this.carrier);
			
				ProPlayer pplayer = GameAPI.getPlayerHandler().getProPlayer(this.carrier);
				
				pplayer.getKit().apply(this.carrier, team.color);
			
				this.ctf.flagManager.points.put(team, this.ctf.flagManager.points.get(team) + 1);
			
				new InstantFirework(FireworkEffect.builder().with(FireworkEffect.Type.BURST).withColor(ColorConverter.getColor(team.color)).build(), this.spawnLocation);
			
				ActionBar bar = new ActionBar(team.color + this.carrier.getName() + C.gray + " has captured a " + this.team.color + this.team.name + C.gray + " flag!");
			
				this.carrier = null;
				
				if(this.ctf.pointsToWin == this.ctf.flagManager.points.get(team)){
					this.ctf.endGame(team);
				}else{
					returnToSpawn();
				}
			}
		}
	}
	
	/*
	 * getters
	 */
	
	public Player getCarrier(){
		if(this.carrier != null){
			return this.carrier;
		}else{
			return null;
		}
	}
	
	public boolean isAtSpawn(){
		return this.isAtSpawn;
	}
	
	public boolean isTaken(){
		if(!this.isAtSpawn && this.carrier != null){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isReturning(){
		if(!this.isAtSpawn && this.carrier == null){
			return true;
		}else{
			return false;
		}
	}
	
	public Location getFlagSpawnLocation(){
		return this.spawnLocation;
	}
	
	public Location getCurrentLocation(){
		return this.currentLocation;
	}
	
	public Team getTeam(){
		return this.team;
	}
	
	public Block getBannerBlock(){
		if(this.spawnLocation.getBlock().getType().equals(Material.BANNER)){
			return this.spawnLocation.getBlock();
		}else{
			return null;
		}
	}
	
	public ItemStack getBannerItem(){
		return this.bannerItem;
	}
	
	public String getStatus(){
		if(this.isAtSpawn){
			return C.dgreen + "Safe";
		}else{
			if(this.carrier != null){
				return C.dred + "Lost";
			}else{
				return C.gold + "Returning";
			}
		}
	}
	
}