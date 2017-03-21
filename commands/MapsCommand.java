package cyclegame.commands;

import common.command.Command;
import common.util.communication.C;
import cyclegame.rotation.GameMap;
import org.bukkit.entity.Player;
import cyclegame.GameAPI;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 10/19/15.
 */
public class MapsCommand extends Command {

    public MapsCommand() {
        super("maps", Arrays.asList("map", "rotation", "rot"));
    }

    @Override
    public void call(Player player, ArrayList<String> args) {


        player.sendMessage("");
        for (int i = 0; i < GameAPI.getRotation().activeMaps.size(); i++) {
            GameMap map = GameAPI.getRotation().activeMaps.get(i);

            if (GameAPI.getRotation().mapIndex == i) {
                player.sendMessage(C.tab + C.gray + (i + 1) + ". " + C.yellow + map.name);
            } else {
                player.sendMessage(C.tab + C.gray + (i + 1) + ". " + C.white + map.name);
            }
        }
        player.sendMessage("");
    }
}
