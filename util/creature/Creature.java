package common.util.creature;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;

/**
 * Created by Luke on 11/24/14.
 */
public class Creature {
    public static LivingEntity spawnCreature(EntityType entityType, Location location) {
        LivingEntity mob = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
        return mob;
    }

    public static LivingEntity spawnCreature(EntityType entityType, Location location, String name) {
        LivingEntity mob = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
        mob.setCustomName(name);
        mob.setCustomNameVisible(true);
        return mob;
    }

    public static LivingEntity spawnAdultHorse(Horse.Style style, Location location, String name) {
        Horse mob = (Horse) location.getWorld().spawnEntity(location, EntityType.HORSE);
        mob.setStyle(style);
        mob.setAdult();
        mob.setCustomName(name);
        mob.setCustomNameVisible(true);
        return mob;
    }

    public static LivingEntity spawnNetherSkelleton(Location location) {
        Skeleton mob = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
        mob.setSkeletonType(Skeleton.SkeletonType.WITHER);
        return mob;
    }

    public static LivingEntity spawnNetherSkelleton(Location location, String name) {
        Skeleton mob = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
        mob.setSkeletonType(Skeleton.SkeletonType.WITHER);
        mob.setCustomName(name);
        mob.setCustomNameVisible(true);
        return mob;
    }

    public static void loadChunk(Location location) {
        location.getChunk().load();
    }
}
