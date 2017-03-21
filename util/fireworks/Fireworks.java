package common.util.fireworks;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fireworks {
    private static final List<FireworkEffect.Type> types = new ArrayList<FireworkEffect.Type>();

    static {
        for (FireworkEffect.Type effect : FireworkEffect.Type.values())
            types.add(effect);
    }

    public static void spawnColoredFirework(Location location, FireworkEffect.Type type, int R, int G, int B) {
        final Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();

        meta.addEffects(FireworkEffect.builder().with(type).withColor(Color.fromRGB(R, G, B)).withTrail().build());

        firework.setFireworkMeta(meta);

        firework.detonate();
    }
    
    public static void spawnRandomFirework(Location location, int power) {
        final Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
		meta.setPower(2);

        meta.addEffects(FireworkEffect.builder().with(types.get(new Random().nextInt(types.size()))).withColor(getRandomBGRColour()).withTrail().build());

        firework.setFireworkMeta(meta);
    }

    public static void spawnRandomFirework(Location location) {
        spawnRandomFirework(location, 2);
    }

    private static Color getRandomBGRColour() {
        return Color.fromBGR(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));
    }
}