package common.command.commands;

import common.command.Command;
import common.util.communication.C;
import common.util.communication.F;
import cyclegame.GameAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 10/19/15.
 */
public class RemoveNearCommand extends Command {

    public RemoveNearCommand() {
        super("removenear", Arrays.asList("rn"));
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        super.setPermission("mcnet.mod");

        if (args != null && args.size() == 1) {
            int radius = Integer.valueOf(args.get(0));
            int removeCount = 0;

            for (Entity entity : player.getWorld().getEntities()) {

                if(entity.getType() == EntityType.PLAYER) {
                    continue;
                }

                if (entity.getLocation().distance(player.getLocation()) <= radius) {
                    entity.remove();
                    removeCount++;
                }
            }

            F.message(player, "Entity", "Removed " + C.red + removeCount + C.gray + " entities.");

        } else {
            showCommands(player);
        }
    }

    public void showCommands(Player player) {
        F.message(player, "Entity", "/removenear <radius>");
    }
}
