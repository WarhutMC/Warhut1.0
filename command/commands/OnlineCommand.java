package common.command.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import cyclegame.GameAPI;

import common.command.Command;
import cyclegame.players.ProPlayer;
import cyclegame.players.Rank;
import common.util.communication.C;
import common.util.communication.F;

public class OnlineCommand extends Command {

    public OnlineCommand() {
        super("online", Arrays.asList("list", "who", "c"));
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        
    	int admins = 0;
    	int mods = 0;
    	int broadcasters = 0;
    	int donors = 0;
    	int defaults = 0;
    	
        for(ProPlayer p : GameAPI.getPlayerHandler().getProPlayers()){
        	if(p.getRank().equals(Rank.ADMIN)){
        		admins = admins + 1;
        	}else
        	if(p.getRank().equals(Rank.MOD)){
        		mods = mods  +1;
        	}else
        	if(p.getRank().equals(Rank.TWITCH) || p.getRank().equals(Rank.YOUTUBE)){
        		broadcasters = broadcasters + 1;
        	}else
        	if(p.getRank().equals(Rank.PRO) || p.getRank().equals(Rank.PRO2) || p.getRank().equals(Rank.PRO3)){
        		donors = donors + 1;
        	}else{
        		defaults = defaults + 1;
        	}
        }

        F.message(player, C.aqua + "Online Players " + C.gray + "(" + C.yellow + Bukkit.getServer().getOnlinePlayers().size() + C.gray + "/" + C.yellow + Bukkit.getServer().getMaxPlayers() + C.gray + ")");
        F.message(player, Rank.ADMIN.getChatColor() + "  Admin: " + C.gray + admins);
        F.message(player, Rank.MOD.getChatColor() + "  Mod: " + C.gray + mods);
        F.message(player, Rank.YOUTUBE.getChatColor() + "  Broadcaster: " + C.gray + broadcasters);
        F.message(player, Rank.PRO3.getChatColor() + "  Donator: " + C.gray + donors);
        F.message(player, Rank.regular.getChatColor() + "  Default: " + C.gray + defaults);



    }
	
}
