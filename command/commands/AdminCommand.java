package common.command.commands;

import common.command.Command;
import cyclegame.players.ProPlayer;
import cyclegame.players.Rank;
import common.util.communication.C;
import common.util.communication.F;
import common.util.communication.S;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import cyclegame.GameAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by luke on 10/19/15.
 */
public class AdminCommand extends Command {

	public HashMap<Player, Integer> editing = new HashMap<Player, Integer>();
	
    public AdminCommand() {
        super("admin", Arrays.asList("a"));
        super.setPermission("mcnet.admin");

//        super.setRank(Rank.ADMIN);
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        if(args == null || args.size() == 0) {
            F.message(player, "/admin setrank <player> <rank>");
            F.message(player, "/admin addcoins <player> <amount>");
            F.message(player, "/admin addskulls <player> <amount>");
            return;
        }

        ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(player);

        if (args.size() >= 1) {
        	
            if (args.get(0).equalsIgnoreCase("setrank")) {
                if (args.size() == 3) {
                    String pName = args.get(1);

                    Rank rank;
                    if (args.get(2).equalsIgnoreCase("regular")) {
                        rank = Rank.regular;
                    } else {
                        rank = Rank.valueOf(args.get(2).toUpperCase());
                    }

                    GameAPI.getPlayerHandler().updateDatabaseValue(GameAPI.getPlayerHandler().playersCollection, pName, "rank", rank.toString());

                    Player target = Bukkit.getServer().getPlayer(pName);
                    if (target != null) {
                        ProPlayer targetProPlayer = GameAPI.getPlayerHandler().getProPlayer(target);

                        targetProPlayer.setRank(rank);
                        if(rank == Rank.regular) {
                            F.message(target, "Rank", "Your rank was set to " + C.white + "REGULAR");
                        } else {
                            F.message(target, "Rank", "Your rank was set to " + rank.getTag());
                        }
                        S.playSound(target, Sound.LEVEL_UP);
                    }

                    if(rank == Rank.regular) {
                        F.message(player, "Admin", C.yellow + pName + C.gray + " was set to rank " + C.white + "REGULAR");
                    } else {
                        F.message(player, "Admin", C.yellow + pName + C.gray + " was set to rank " + rank.getTag());
                    }
                } else {
                    F.warning(player, "/admin setrank <player> <rank>");
                }
            }

            else if (args.get(0).equalsIgnoreCase("addcoins")) {
                if (args.size() == 3) {
                    String pName = args.get(1);
                    int amount = Integer.parseInt(args.get(2));

                    GameAPI.getPlayerHandler().incrementValue(GameAPI.getPlayerHandler().playersCollection, pName, "coins", amount);

                    Player target = Bukkit.getServer().getPlayer(pName);
                    if (target != null) {
                        ProPlayer targetProPlayer = GameAPI.getPlayerHandler().getProPlayer(target);
                        targetProPlayer.addCoins(amount);

                        if(amount >= 0) {
                            F.message(target, "Coins", "You were given " + C.green + amount + " coins");
                            S.playSound(target, Sound.LEVEL_UP);
                        } else {
                            F.message(target, "Coins", "You were deducted " + C.red + amount + " coins");
                            S.playSound(target, Sound.VILLAGER_HIT);
                        }
                    }

                    if(amount >= 0) {
                        F.message(player, "Admin", C.yellow + pName + C.gray + " was given " + C.green + amount + " coins");
                    } else {
                        F.message(player, "Admin", C.yellow + pName + C.gray + " was deducted " + C.red + amount + " coins");
                    }

                } else {
                    F.warning(player, "/admin addcoins <player> <amount>");
                }
            }
        }
    }
}
