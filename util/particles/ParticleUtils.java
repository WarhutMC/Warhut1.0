package common.util.particles;

import common.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

/**
 * Created by luke on 7/27/15.
 */
public class ParticleUtils {

    /* Can only be used when radius is less than 1 */
    public static void circle(Location location, ParticleEffect particleEffect, float radius, int amount) {
        for (Location loc : LocationUtils.getCircle(location, radius, amount)) {
            particleEffect.display(0, 0, 0, 0, 1, loc, 80);
        }
    }

}
