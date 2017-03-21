package common.command.commands;

import common.command.Command;
import common.util.communication.C;
import cyclegame.GameAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.ArrayList;

/**
 * Created by luke on 12/12/15.
 */
public class GMuteCommand extends Command {
    public boolean muted = false;

    public GMuteCommand() {
        super("gmute");
        super.setPermission("mcnet.admin");
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        muted = !muted;

        if (muted) {
            Bukkit.broadcastMessage(C.aqua + C.bold + "!! " + C.yellow + "Global mute" + C.green + " enabled");
        } else {
            Bukkit.broadcastMessage(C.aqua + C.bold + "!! " + C.yellow + "Global mute" + C.red + " disabled");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (muted) {

            if (!event.getPlayer().hasPermission("mcnet.mod")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(C.red + "Chat is currently disabled.");
            }
        }
    }
}
