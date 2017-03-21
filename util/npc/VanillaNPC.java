package common.util.npc;

import common.util.LocationUtils;
import cyclegame.GameAPI;
import main.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by luke on 11/7/15.
 */
public class VanillaNPC extends NPC {
    public Location spawn;
    public int runnableID;

    public VanillaNPC(EntityType entityType, Location location) {
        super.build(entityType, location, "", true);
        this.spawn = location;
        this.runnableID = fallCheck();
    }

    public int fallCheck() {
        return GameAPI.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (body.getLocation().getY() <= 0) {
                    body.teleport(spawn);
                }
            }
        }, 20 * 5, 20 * 5);
    }

    @Override
    public void onClick(Player player) {

    }

    @Override
    public void extraUnload() {
        GameAPI.getScheduler().cancelTask(this.runnableID);
    }

    public static ArrayList<VanillaNPC> spawnGroup(EntityType entityType, Location center, int radius, int amount) {
        ArrayList<VanillaNPC> list = new ArrayList<>();
        for (Location location : LocationUtils.getCircle(center, radius, amount)) {
            list.add(new VanillaNPC(entityType, location));
        }

        return list;
    }
}
