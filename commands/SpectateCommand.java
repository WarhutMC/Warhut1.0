package cyclegame.commands;

import common.Manager;
import common.command.Command;
import common.util.communication.C;
import common.util.communication.F;
import cyclegame.GameAPI;
import cyclegame.players.ProPlayer;
import cyclegame.templates.game.Game;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by luke on 12/13/15.
 */
public class SpectateCommand extends Command {
    private Game game;

    public SpectateCommand(Game game) {
        super("spectate", Arrays.asList("spec", "leave"));
        this.game = game;
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        if (game.spectators.players.contains(player)) {
            player.sendMessage(C.red + "You are already a " + C.aqua + "spectator" + C.red + ".");
            return;
        }

        ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(player);
        if (new Date().getTime() - proPlayer.getLastHitDate().getTime() < 10000) {
            F.warning(player, C.red + "You are in too much danger to leave the game right now.");
            return;
        }

        game.getSpectateManager().addSpectator(player);
    }
}
