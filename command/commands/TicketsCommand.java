package common.command.commands;

import common.command.Command;
import cyclegame.players.ProPlayer;
import common.util.communication.C;
import common.util.communication.F;
import cyclegame.GameAPI;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 10/19/15.
 */
public class TicketsCommand extends Command {

    public TicketsCommand() {
        super("tickets", Arrays.asList("ticket"));
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(player);

        F.message(player, "Play4Cash", "You have " + C.aqua + proPlayer.tickets + " tickets" + C.gray + ".");
        F.message(player, "Play4Cash", "Gain tickets by " + C.green + "winning games" + C.gray + ".");
        F.message(player, "Play4Cash", "Tickets are to win " + C.green + "cash prizes" + C.gray + ".");
        F.message(player, "Play4Cash", "Every Saturday, we will draw " + C.green + "3 winners" + C.gray + ".");
        F.message(player, "Play4Cash", "Each winner has the choice of " + C.yellow + "$5 paypal" + C.gray + " or " + C.yellow + "$10 buycraft credit" + C.gray + ".");
        F.message(player, "Play4Cash", "The more tickets you have, the better chance at winning.");
    }
}
