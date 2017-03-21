package common.util.event;

import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Luke on 2/6/15.
 */
public class EventUtils {
	public static String getCause(EntityDamageEvent.DamageCause d) {
		if (d == EntityDamageEvent.DamageCause.VOID) {
			return "void";
		} else if (d == EntityDamageEvent.DamageCause.PROJECTILE) {
			return "projectile";
		} else if (d == EntityDamageEvent.DamageCause.DROWNING) {
			return "water";
		} else if (d == EntityDamageEvent.DamageCause.FIRE) {
			return "fire";
		} else if (d == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
			return "slain";
		} else if (d == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
			return "explosion";
		} else {
			return "null";
		}
	}

	public static boolean isItemClickWithDisplayName(PlayerInteractEvent event) {
		if(event.getAction() == Action.LEFT_CLICK_AIR
				|| event.getAction() == Action.LEFT_CLICK_BLOCK
				|| event.getAction() == Action.RIGHT_CLICK_AIR
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem() != null) {
					if (event.getItem().getItemMeta() != null) {
						if (event.getItem().getItemMeta().getDisplayName() != null) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static String getDisplayName(ItemStack itemStack) {
		if (itemStack != null) {
			if (itemStack.getItemMeta() != null) {
				if (itemStack.getItemMeta().getDisplayName() != null) {
					return itemStack.getItemMeta().getDisplayName();
				}
			}
		}
		return null;
	}
}
