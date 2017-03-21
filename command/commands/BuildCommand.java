package common.command.commands;

import common.bungee.Bungee;
import common.command.Command;
import common.util.communication.F;
import main.Main;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by luke on 10/19/15.
 */
public class BuildCommand extends Command {

    public BuildCommand() {
        super("build");
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        F.message(player, "Portal", "Connecting to Build Server...");
        Bungee.sendToServer(Main.getInstance(), player, "Build");
    }
}
