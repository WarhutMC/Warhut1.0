package common.broadcast;

import common.command.Command;
import common.util.communication.C;
import common.util.communication.F;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 10/26/15.
 */
public class AutoBroadcastCommand extends Command {
    BroadcastManager broadcastManager;

    public AutoBroadcastCommand(BroadcastManager broadcastManager) {
        super("autob", Arrays.asList("autobroadcast"));
        this.broadcastManager = broadcastManager;
    }

    @Override
    public void call(final Player player, ArrayList<String> args) {
        if (args == null || args.size() == 0) {
            this.listCommands(player);
            return;
        }

        if (args.get(0).equalsIgnoreCase("list")) {
            player.sendMessage("");

            for (String key : broadcastManager.broadcasts.keySet()) {
                String value = broadcastManager.broadcasts.get(key);

                player.sendMessage(" " + C.yellow + key + C.gray + ". " + value);

                player.sendMessage("");
            }
        }

        else if (args.get(0).equalsIgnoreCase("reload")) {
            broadcastManager.updateBroadcasts();
            F.message(player, "Auto Broadcast", "Updated the broadcast list.");
        }
    }

    public void listCommands(Player player) {
        player.sendMessage(C.divider);
        player.sendMessage("");

        player.sendMessage(C.tab + C.gray + "/autob list");
        player.sendMessage(C.tab + C.gray + "/autob reload");

        player.sendMessage("");
        player.sendMessage(C.divider);
    }
}
