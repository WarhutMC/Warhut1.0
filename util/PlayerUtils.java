package common.util;

import cyclegame.GameAPI;
import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

/**
 * Created by luke on 10/18/15.
 */
public class PlayerUtils {

    /* Will NOT change gamemode */
    public static void resetPlayer(Player player) {
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
        for (PotionEffect effect : player.getActivePotionEffects()) {
            try {
                player.removePotionEffect(effect.getType());
            } catch (NullPointerException e) {
            }
        }
        player.setTotalExperience(0);
        player.setExp(0);
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.1F);
        player.setFireTicks(0);
        player.setFallDistance(0);
        player.setVelocity(new Vector());
        player.setFlying(false);
        player.setAllowFlight(false);

        for (Player player1 : Bukkit.getServer().getOnlinePlayers()) {
            if(!player1.canSee(player)) player1.showPlayer(player);
        }

        if (Main.getInstance().getGameAPI() != null) {
            GameAPI.getInstance().getGhostManager().removeGhost(player);
            GameAPI.getInstance().getTintHealth().removeTint(player);
        }
    }

    public static void heal(Player player, int amount) {
        if (player.getHealth() + amount > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        } else {
            player.setHealth(player.getHealth() + amount);
        }
    }
}
