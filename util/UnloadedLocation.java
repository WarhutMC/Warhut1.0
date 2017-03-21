package common.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Created by luke on 10/17/15.
 */
public class UnloadedLocation {
    public double x;
    public double y;
    public double z;

    public Location toLocation() {
        return new Location(Bukkit.getServer().getWorlds().get(0), x, y, z);
    }
}
