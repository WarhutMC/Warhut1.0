package common.punish.commands;

import common.command.Command;
import common.util.communication.C;
import common.util.communication.F;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 10/24/15.
 */
public class DisableVanillaPunishCommand extends Command {
    public DisableVanillaPunishCommand() {
        super("ban", Arrays.asList("pardon", "kick"));
        super.setPermission("mcnet.mod");
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        F.message(player, "Punish", "Please use the " + C.yellow + "/punish" + C.gray + " command.");
    }
}
