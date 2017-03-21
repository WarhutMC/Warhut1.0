package common.util.communication;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by luke on 10/18/15.
 */
public class S {
    public static void pling(Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
    }

    public static void plingAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
        }
    }

    public static void xpSoundAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
        }
    }

    public static void xpSound(Player player) {
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
    }

    public static void plingHigh(Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
    }

    public static void kill(Player player) {
        player.playSound(player.getLocation(), Sound.IRONGOLEM_HIT, 10, 1);
    }

    public static void arrowHit(Player player) {
        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 10, 1);
    }

    public static void click(Player player) {
        player.playSound(player.getLocation(), Sound.CLICK, 1, 2);
    }

    public static void pop(Player player) {
        player.playSound(player.getEyeLocation(), Sound.ITEM_PICKUP, 10, 1);
    }

    public static void popAll() {
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.playSound(player.getEyeLocation(), Sound.ITEM_PICKUP, 10, 1);
        }
    }

    public static void clickAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.CLICK, 1, 2);
        }
    }

    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 10, 1);
    }

    public static void playSound(Location location, Sound sound) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.playSound(location, sound, 10, 1);
        }
    }

    public static void playSound(Sound sound) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, 10, 1);
        }
    }
}
