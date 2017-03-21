package common.util.npc;

import common.damage.DamageInfo;
import common.damage.DamageUtils;
import common.util.creature.Creature;
import common.util.particles.ParticleEffect;
import main.Main;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by luke on 10/18/15.
 */
public abstract class NPC implements Listener {
    public LivingEntity body;

    public Slime slime;
    public ArmorStand armorStand;

    public ArmorStand lowArmorStand;
    public Slime lowSlime;

    public NPC() {

    }

    public void build(EntityType entityType, Location location, String name, boolean ai) {
        location.getChunk().load();
        this.body = Creature.spawnCreature(entityType, location);
        this.body.setRemoveWhenFarAway(false);

        //Nametag Magic
        this.armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        if(!name.equalsIgnoreCase("")) {
            armorStand.setCustomName(name);
            armorStand.setCustomNameVisible(true);
        }
        armorStand.setVisible(false);
        armorStand.setRemoveWhenFarAway(false);

        //Lower Nametag
        this.slime = (Slime) location.getWorld().spawnEntity(location, EntityType.SLIME);
        slime.setRemoveWhenFarAway(false);
        slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10));
        slime.setSize(-3);

        //Put it all together
        slime.setPassenger(armorStand);
        this.body.setPassenger(slime);

//        MobUtils.noAI(this.slime);
//        MobUtils.noAI(this.armorStand);
        if(!ai) {
            MobUtils.noAI(this.body);
        }

        Main.registerListener(this);
    }

    public void build(EntityType entityType, Location location, String name, String lowName, boolean ai) {
        location.getChunk().load();
        this.body = Creature.spawnCreature(entityType, location);
        this.body.setRemoveWhenFarAway(false);

        //Nametag Magic
        this.armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        if(!name.equalsIgnoreCase("")) {
            armorStand.setCustomName(name);
            armorStand.setCustomNameVisible(true);
        }
        armorStand.setVisible(false);
        armorStand.setRemoveWhenFarAway(false);

        //Lower Nametag
        this.slime = (Slime) location.getWorld().spawnEntity(location, EntityType.SLIME);
        slime.setRemoveWhenFarAway(false);
        slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10));
        slime.setSize(-3);

        //Put it all together
        slime.setPassenger(armorStand);
        this.body.setPassenger(slime);

//        MobUtils.noAI(this.slime);
//        MobUtils.noAI(this.armorStand);
        if(!ai) {
            MobUtils.noAI(this.body);
        }

        /* =================== */

        //Nametag Magic
        this.lowArmorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        lowArmorStand.setCustomName(lowName);
        lowArmorStand.setCustomNameVisible(true);
        lowArmorStand.setVisible(false);
        lowArmorStand.setRemoveWhenFarAway(false);

        //Lower Nametag
        this.lowSlime = (Slime) location.getWorld().spawnEntity(location, EntityType.SLIME);
        lowSlime.setRemoveWhenFarAway(false);
        lowSlime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10));
        lowSlime.setSize(-5);

        //Put it all together
        lowSlime.setPassenger(lowArmorStand);
        armorStand.setPassenger(lowSlime);

        Main.registerListener(this);
    }



    public abstract void onClick(Player player);

    @EventHandler
    public void interact(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() == this.slime || (this.lowSlime != null && event.getRightClicked() == this.lowSlime) || (this.lowArmorStand != null && event.getRightClicked() == this.lowArmorStand)) {

            Vector toEntity = this.slime.getLocation().toVector().subtract(event.getPlayer().getLocation().toVector());
            Vector direction = event.getPlayer().getLocation().getDirection();
            double dot = toEntity.normalize().dot(direction);

            if (dot > .2) {
                this.onClick(event.getPlayer());
                event.setCancelled(true);
            }

        } else if (event.getRightClicked() == this.body) {
            this.onClick(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        DamageInfo damageInfo = DamageUtils.getDamageInfo(event);

        if(damageInfo.getHurtEntity() == this.body || damageInfo.getHurtEntity() == this.slime || (this.lowSlime != null && damageInfo.getHurtEntity() == this.lowSlime) || (this.lowArmorStand != null && damageInfo.getHurtEntity() == this.lowArmorStand)) {
            event.setCancelled(true);
            if(this.body.getEquipment().getHelmet() != null) {
//                this.body.getEquipment().getHelmet().setDurability((short) 20);
                this.body.getEquipment().setHelmet(new ItemStack(this.body.getEquipment().getHelmet().getType()));
            }
            if (damageInfo.getDamagerPlayer() != null) {
                this.onClick(damageInfo.getDamagerPlayer());
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getEntity() == this.body) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() == this.body || event.getEntity() == this.slime || (this.lowSlime != null && event.getEntity() == this.lowSlime) || (this.lowArmorStand != null && event.getEntity() == this.lowArmorStand)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMount(VehicleEnterEvent event) {
        if (event.getVehicle() == this.body) {
            event.setCancelled(true);
        }
    }

    /* Same as unload(), but with fancy effects */
    public void delete() {
        Location center = this.body.getEyeLocation().add(0, -.5, 0);
        ParticleEffect.SMOKE_LARGE.display(.5f, .5f, .5f, 0.05f, 40, center, 20);
        ParticleEffect.LAVA.display(.5f, .5f, .5f, 0.05f, 40, center, 20);
        ParticleEffect.CRIT.display(.5f, .5f, .5f, 0.05f, 40, center, 20);

        unload();
    }

    public void unload() {
        this.extraUnload();
        this.body.remove();

        if (this.slime != null) {
            this.slime.remove();
        }

        if (this.armorStand != null) {
            this.armorStand.remove();
        }

        if (this.lowSlime != null) {
            this.lowSlime.remove();
        }

        if (this.lowArmorStand != null) {
            this.lowArmorStand.remove();
        }

        HandlerList.unregisterAll(this);
    }

    public void clearItems() {
        if (this.body.getType() == EntityType.PIG_ZOMBIE) {

        }
    }

    public abstract void extraUnload();
}
