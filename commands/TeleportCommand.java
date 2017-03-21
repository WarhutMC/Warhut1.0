package cyclegame.commands;

import common.command.Command;
import common.util.communication.C;
import common.util.communication.F;
import common.util.communication.S;
import cyclegame.templates.game.spectate.SpectateManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 12/14/15.
 */
public class TeleportCommand extends Command {

    public TeleportCommand() {
        super("tp", Arrays.asList("teleport"));
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        if (!SpectateManager.isSpectator(player)) {
            F.warning(player, "Only " + C.aqua + "spectators" + C.red + " can teleport.");
            return;
        }

        if (args == null || args.size() != 1) {
            player.sendMessage(C.red + "/tp (player)");
            return;
        }

        Player to = Bukkit.getServer().getPlayer(args.get(0));
        if (to == null) {
            player.sendMessage(C.red + "Unable to find player named " + C.yellow + to.getName());
            return;
        }

        player.teleport(to);
        player.sendMessage(C.daqua + "Teleported to " + C.yellow + to.getName());
        S.playSound(player, Sound.ENDERMAN_TELEPORT);
    }
}
