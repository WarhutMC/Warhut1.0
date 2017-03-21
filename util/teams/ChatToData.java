package common.util.teams;

import org.bukkit.ChatColor;

import common.util.communication.C;
import org.bukkit.DyeColor;

public class ChatToData {

	public static byte ChatColorToData(ChatColor color){
		if(color.equals(ChatColor.WHITE)){
			return (byte)0;
		}else if(color.equals(ChatColor.GOLD)){
			return (byte)1;
		}else if(color.equals(ChatColor.AQUA)){
			return (byte)3;
		}else if(color.equals(ChatColor.YELLOW)){
			return (byte)4;
		}else if(color.equals(ChatColor.GREEN)){
			return (byte)5;
		}else if(color.equals(ChatColor.LIGHT_PURPLE)){
			return (byte)6;
		}else if(color.equals(ChatColor.DARK_GRAY)){
			return (byte)7;
		}else if(color.equals(ChatColor.GRAY)){
			return (byte)8;
		}else if(color.equals(ChatColor.DARK_AQUA)){
			return (byte)9;
		}else if(color.equals(ChatColor.DARK_PURPLE)){
			return (byte)10;
		}else if(color.equals(ChatColor.BLUE)){
			return (byte) 11;
		}else if(color.equals(ChatColor.DARK_BLUE)){
			return (byte)11;
		}else if(color.equals(ChatColor.DARK_GREEN)){
			return (byte)13;
		}else if(color.equals(ChatColor.DARK_RED)){
			return (byte) 14;
		}else if(color.equals(ChatColor.RED)){
			return (byte)14;
		}else if(color.equals(ChatColor.BLACK)){
			return (byte)15;
		}else{
			return (byte)0;
		}
	}
	
}
