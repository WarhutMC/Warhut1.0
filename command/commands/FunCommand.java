package common.command.commands;

import common.command.Command;
import cyclegame.players.ProPlayer;
import common.util.communication.C;
import common.util.communication.F;
import cyclegame.GameAPI;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by luke on 10/19/15.
 */
public class FunCommand extends Command {

	public HashMap<Player, Integer> editing = new HashMap<Player, Integer>();

    public FunCommand() {
        super("fun", Arrays.asList("f"));
        super.setPermission("mcnet.mod");

//        super.setRank(Rank.MOD);
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        if(args == null || args.size() == 0) {
            F.message(player, "/f summon <EntityType>");
            return;
        }

        ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(player);

        if (args.get(0).equalsIgnoreCase("summon")) {
            if (args.size() >= 2) {

                EntityType entityType = EntityType.valueOf(args.get(1).toUpperCase());
                if (entityType != null) {
                    int amount = 1;
                    if (args.size() == 3) {
                        amount = Integer.valueOf(args.get(2));
                    }

                    for (int i = 0; i < amount; i++) {
                        player.getWorld().spawnEntity(player.getLocation(), entityType);
                    }

                    F.message(player, "Fun", "Summoned " + C.red + entityType.toString());

                } else {
                    F.warning(player, "Couldn't find entity " + C.red + args.get(1));
                }
            }
        }
    }
}
