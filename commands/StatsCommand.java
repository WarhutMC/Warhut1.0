package cyclegame.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import common.command.Command;
import common.util.communication.C;
import common.util.communication.F;
import common.util.inventory.InventoryFactory;
import common.util.itemstack.ItemFactory;
import cyclegame.GameAPI;
import cyclegame.players.ProPlayer;

/**
 * Created by luke on 10/19/15.
 */
public class StatsCommand extends Command {

    public StatsCommand() {
        super("stats", Arrays.asList("stat", "statistics", "statistic"));
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(player);
        
        if(args.size() == 0){

        player.sendMessage(C.divider);
        player.sendMessage("");
        player.sendMessage(C.tab + C.gray + "Displaying " + C.purple + C.bold + GameAPI.getMatch().map.game.toUpperCase() + C.gray + " lifetime stats for " + C.yellow + player.getName());
        player.sendMessage("");
        player.sendMessage(C.tab + C.gray + "Level: " + C.green + proPlayer.getLevel());
        player.sendMessage(C.tab + C.gray + "Kills: " + C.green + proPlayer.gameStats.kills);
        player.sendMessage(C.tab + C.gray + "Deaths: " + C.green + proPlayer.gameStats.deaths);
        player.sendMessage(C.tab + C.gray + "Games Played: " + C.green + proPlayer.gameStats.matches);
        player.sendMessage(C.tab + C.gray + "Coins: " + C.green + proPlayer.coins);
        player.sendMessage("");
        player.sendMessage(C.divider);

//         player.openInventory(getStats(proPlayer));

        }else{
        	//getting other player's statistics

        	ArrayList<String> foundPlayers = new ArrayList<String>();
        	for(Player p : Bukkit.getServer().getOnlinePlayers()){
        		if(p.getName().toUpperCase().contains(args.get(0).toUpperCase())){
        			foundPlayers.add(p.getName());
        		}
        	}

        	if(!(foundPlayers.size() > 1)){
            	if(!(foundPlayers.size() < 1)){

            		//Displaying found player's statistics
            		ProPlayer pp = GameAPI.getPlayerHandler().getProPlayer(Bukkit.getServer().getPlayer(foundPlayers.get(0)));
            		/*
                    player.sendMessage(C.divider);
                    player.sendMessage("");
                    player.sendMessage(C.tab + C.gray + "Displaying " + C.purple + C.bold + GameAPI.getMatch().map.game.toUpperCase() + C.gray + " lifetime stats for " + C.yellow + foundPlayers.get(0));
                    player.sendMessage("");
                    player.sendMessage(C.tab + C.gray + "Level: " + C.green + pp.getLevel());
                    player.sendMessage(C.tab + C.gray + "Kills: " + C.green + pp.gameStats.kills);
                    player.sendMessage(C.tab + C.gray + "Deaths: " + C.green + pp.gameStats.deaths);
                    player.sendMessage(C.tab + C.gray + "Games Played: " + C.green + pp.gameStats.matches);
                    player.sendMessage(C.tab + C.gray + "Coins: " + C.green + pp.coins);
                    player.sendMessage("");
                    player.sendMessage(C.divider); 
            		 */
            		
                    player.openInventory(getStats(pp));
            	}else{
            		F.message(player, "Stats", "No online players found!");
            	}
        	}else{
        		F.message(player, "Stats", "Multiple online players found " + C.purple + foundPlayers.toString() + C.gray + "!");
        	}
        }

    }
    
    // GUI stats - Gamer
    public Inventory getStats(ProPlayer pp) {
        List<ItemStack> contents = Arrays.asList(
        	ItemFactory.createItem(Material.EXP_BOTTLE, C.blue + "Level", Arrays.asList("", C.aqua + "Level: " + C.white + pp.getLevel(), "")),
        	ItemFactory.createItem(Material.STAINED_GLASS_PANE, C.gray + "-", Arrays.asList(C.gray + "-"), DyeColor.BLACK.getData()),
        	ItemFactory.createItem(Material.DIAMOND_SWORD, C.blue + "Kills", Arrays.asList("", C.aqua + "Kills: " + C.white + pp.gameStats.kills, "")),
        	ItemFactory.createItem(Material.STAINED_GLASS_PANE, C.gray + "-", Arrays.asList(C.gray + "-"), DyeColor.BLACK.getData()),
        	ItemFactory.createItem(Material.BARRIER, C.blue + "Deaths", Arrays.asList("", C.aqua + "Deaths: " + C.white + pp.gameStats.deaths, "")),
        	ItemFactory.createItem(Material.STAINED_GLASS_PANE, C.gray + "-", Arrays.asList(C.gray + "-"), DyeColor.BLACK.getData()),
        	ItemFactory.createItem(Material.WATCH, C.blue + "Games Played", Arrays.asList("", C.aqua + "Games Played: " + C.white + pp.gameStats.matches, "")),
        	ItemFactory.createItem(Material.STAINED_GLASS_PANE, C.gray + "-", Arrays.asList(C.gray + "-"), DyeColor.BLACK.getData()),
        	ItemFactory.createItem(Material.DIAMOND, C.blue + "Coins", Arrays.asList("", C.aqua + "Coins: " + C.white + pp.getCoins()))
        );
        
        return InventoryFactory.createInv(C.gold + C.underline + "Stats", 9, contents);
    }
}
