package common.command.commands;

import common.command.Command;
import common.util.communication.C;
import common.util.communication.F;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 10/19/15.
 */
public class DonateCommand extends Command {

    public DonateCommand() {
        super("donate", Arrays.asList("buy"));
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
//        ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(player);

        player.sendMessage("");
        F.message(player, "Donate", "Purchase ranks at " + C.aqua + C.underline + "store.mcwar.us");
        player.sendMessage("");
    }
}
