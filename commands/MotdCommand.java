package cyclegame.commands;

import common.command.Command;
import common.util.communication.C;
import cyclegame.GameAPI;
import host.user.Rank;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by luke on 12/9/15.
 */
public class MotdCommand extends Command {
    public MotdCommand() {
        super("motd");
        super.setPermission("mcnet.admin");
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        GameAPI.getInstance().loadMotd();
        player.sendMessage(C.green + "Reloaded MOTD");
    }
}
