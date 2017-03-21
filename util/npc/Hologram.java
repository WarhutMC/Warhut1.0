package common.util.npc;

import common.Manager;
import common.damage.DamageInfo;
import common.damage.DamageUtils;
import main.Main;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * Created by luke on 11/17/15.
 */
public class Hologram implements Manager, Listener {
    public ArmorStand armorStand;

    public Hologram(String name, Location location) {
        this.armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(name);
        armorStand.setGravity(false);
        armorStand.setRemoveWhenFarAway(false);

        Main.registerListener(this);
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() == this.armorStand) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        DamageInfo damageInfo = DamageUtils.getDamageInfo(event);

        if (damageInfo.getHurtEntity() == this.armorStand) {
            event.setCancelled(true);
        }
    }

    @Override
    public void unload() {
        this.armorStand.remove();
        Main.unloadListener(this);
    }
}
