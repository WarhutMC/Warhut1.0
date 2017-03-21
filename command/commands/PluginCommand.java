package common.command.commands;

import common.command.Command;
import common.util.communication.C;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 10/19/15.
 */
public class PluginCommand extends Command {

    public PluginCommand() {
        super("pl", Arrays.asList("plugins", "bukkit:pl", "bukkit:plugins", "help", "?", "bukkit:?", "bukkit:help"));
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        player.sendMessage("Plugins (1): " + C.green + "MCNet");
    }
}
