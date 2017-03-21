package cyclegame.games.domination.dominationzone;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;

import common.util.LocationUtils;

import cyclegame.games.domination.Domination;
import cyclegame.templates.game.Team;

public class DominationZone {

	public Domination domination;
	public String name;
	public Location center;

	public Team ownerTeam;
	public int progress;
	public Team claimingTeam;

	public int radius;

	private ArrayList<Location> blocks = new ArrayList<Location>();

	public DominationZone(Location center, String name, Domination domination){
		this.center = center.add(0, -1, 0);
		this.name = name;
		this.ownerTeam = null;
		this.progress = 0;
		this.domination = domination;

		this.radius = 6;

		for (Location loc : LocationUtils.getCylinder(center, radius)){

			blocks.add(loc);

			if(!loc.equals(this.center)){

				loc.getBlock().setType(Material.STAINED_CLAY);
				loc.getBlock().setData((byte)0);

			}else{

				loc.getBlock().setType(Material.STAINED_GLASS);
				loc.getBlock().setData((byte)0);

			}

		}

	}

	public void setClaimingTeam(Team team){
		this.claimingTeam = team;
	}

	public void addToProgress(Team team){
		this.claimingTeam = team;

		boolean unclaim = false;

		if(this.ownerTeam != null && this.ownerTeam == team){
			if(this.progress < radius){
				this.progress = this.progress + 1;
				if(this.progress == radius){
					this.ownerTeam = team;
				} else {
					playCaptureEffect();
				}
			}
		} else {
			if(this.ownerTeam != null){
				if(this.progress > 0){
					unclaim = true;
					this.progress = this.progress -1;
					playCaptureEffect();
				} else {

//					F.broadcast("Domination", ownerTeam.color + C.bold + ownerTeam.name + C.white + " lost control of " + C.yellow + this.getDisplayName());

					this.ownerTeam = null;

					this.center.getBlock().setType(Material.STAINED_GLASS);
					this.center.getBlock().setData((byte)0);
				}
			} else {
				this.progress = this.progress + 1;
				playCaptureEffect();
			}
		}

		for(Location l : this.blocks){
			if(l.distance(this.center) <= this.progress){
				if(!l.equals(this.center) || (this.claimingTeam == null && this.ownerTeam == null)){
					l.getBlock().setType(Material.STAINED_CLAY);
				}
				if (this.claimingTeam != null) {
					if(unclaim) {
						if(!l.equals(this.center)){
							l.getBlock().setData((byte) this.ownerTeam.getBlockData());
						}
					} else {
						if(!l.equals(this.center)){
							l.getBlock().setData((byte) this.claimingTeam.getBlockData());
						}
					}

				}
			}else{
				if(!l.equals(this.center)){
					l.getBlock().setType(Material.STAINED_CLAY);
					l.getBlock().setData((byte)0);
				}
			}
		}
	}

	public void playCaptureEffect() {
		center.getWorld().playEffect(center, Effect.STEP_SOUND, Material.EMERALD_BLOCK);
//		center.getWorld().playSound(center, Sound.FIREWORK_BLAST2, 1, 1);
	}

	public int getProgress(){
		return this.progress;
	}

	public ArrayList<Location> getBlocks(){
		return this.blocks;
	}

	public Location getCenter(){
		return this.center;
	}

	public Team getCurrentClaimers(){
		return this.claimingTeam;
	}

	public Team getCurrentClaimedTeam(){
		return this.ownerTeam;
	}

	public boolean isClaimed(){
		if(this.ownerTeam == null){
			return false;
		}else{
			return true;
		}
	}

	public String getName() {
		return name;
	}

	public void setOwnerTeam(Team ownerTeam) {
		this.ownerTeam = ownerTeam;
	}

	public Domination getDomination() {
		return domination;
	}

	public Team getOwnerTeam() {
		return ownerTeam;
	}

	public Team getClaimingTeam() {
		return claimingTeam;
	}
}