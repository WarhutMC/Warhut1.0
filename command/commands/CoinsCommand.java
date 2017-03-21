package common.command.commands;

import common.command.Command;
import cyclegame.players.ProPlayer;
import common.util.communication.C;
import common.util.communication.F;
import org.bukkit.entity.Player;
import cyclegame.GameAPI;

import java.util.ArrayList;

/**
 * Created by luke on 10/19/15.
 */
public class CoinsCommand extends Command {

    public CoinsCommand() {
        super("coins");
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(player);

        F.message(player, "Coins", "You have " + C.gold + proPlayer.coins + " coins" + C.gray + ".");
    }
}
