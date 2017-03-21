package common.command.commands;

import common.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 10/25/15.
 */
public class VanillaAdminCommands extends Command {
    public VanillaAdminCommands() {
        super("stop", Arrays.asList("restart"));
        super.setPermission("mcnet.admin");
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        Bukkit.getServer().shutdown();
    }
}
