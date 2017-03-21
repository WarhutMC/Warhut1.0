package common.message;

import common.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 10/24/15.
 */
public class MeCommand extends Command {
    public MeCommand() {
        super("me", Arrays.asList("minecraft:me"));
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        player.sendMessage("lol");
    }
}
