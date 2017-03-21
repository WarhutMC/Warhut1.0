package common.util.communication;

import java.util.ArrayList;

import cyclegame.players.ProPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by luke on 10/16/15.
 */
public class F {

    public static void log(String message) {
        System.out.println(C.yellow + C.bold + "LOG " + C.gray + message);
    }

    public static void warning(Player player, String message) {
        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 10, 1);
        player.sendMessage(C.yellow + " ⚠ " + C.red + message);
    }

    public static void debug(String message) {
        Bukkit.getServer().broadcastMessage(C.red + C.bold + "DEBUG " + C.gray + message);
    }

    public static void broadcast(String msg) {
        Bukkit.getServer().broadcastMessage(C.aqua + C.bold + "!! " + C.gray + msg);
    }

    public static void broadcast(String head, String msg) {
        if (msg == null || msg.equals("")) {
            Bukkit.getServer().broadcastMessage("");
        } else {
            Bukkit.getServer().broadcastMessage(C.aqua + C.bold + "!! " + C.gray + msg);
        }
    }

    public static void broadcastExcludePlayer(String head, String msg, Player player) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p != player) {
                if (msg == null || msg.equals("")) {
                    p.sendMessage("");
                } else {
                    p.sendMessage(C.purple + "* " + C.gray + msg);
                }
            }
        }
    }

    public static void broadcastExcludePlayers(String head, String msg, ArrayList<Player> players) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (!players.contains(p)) {
                if (msg == null || msg.equals("")) {
                    p.sendMessage("");
                } else {
                    p.sendMessage(C.purple + head + " - " + C.gray + msg);
                }
            }
        }
    }
    
    public static void message(Player player, String msg) {
        if (msg == null || msg.equals("")) {
            player.sendMessage("");
        } else {
            player.sendMessage(C.purple + "* " + C.gray + msg);
        }
    }

    public static void message(Player player, String head, String msg) {
        if (msg == null || msg.equals("")) {
            player.sendMessage("");
        } else {
            player.sendMessage(C.purple + "* " + C.gray + msg);
        }
    }

    public static void message(Player player, String msg, boolean sound) {
        player.sendMessage(C.purple + "* " + C.gray + msg);
        if(sound) {
            S.pling(player);
        }
    }

    public static void addCoinsMessage(ProPlayer proPlayer, int amount, String reason) {
        if (amount > 0) {
            F.message(proPlayer.player, "Coins", C.green + "+" + amount + " coins " + C.white + " | " + C.purple + reason);
        } else {
            F.message(proPlayer.player, "Coins", C.red + amount + " coins " + C.white + " | " + C.purple + reason);
        }
    }

    public static void addCurrencyMessage(ProPlayer proPlayer, int amount, String currency, String reason) {
        if (amount > 0) {
            F.message(proPlayer.player, currency, C.green + "+" + amount + " " + currency.toString() + " " + C.white + " | " + C.purple + reason);
        } else {
            F.message(proPlayer.player, currency, C.red + amount + " " + currency.toLowerCase() + " " + C.white + " | " + C.purple + reason);
        }
    }
}
