package cyclegame.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import common.command.Command;
import common.util.communication.C;
import common.util.communication.F;
import common.util.inventory.InventoryFactory;
import common.util.itemstack.ItemFactory;
import cyclegame.templates.game.spectate.SpectateManager;
import main.Main;
import net.md_5.bungee.api.ChatColor;

/*
 * Created by Gamer on 12/14/15
 */
public class TeleportGUICommand extends Command {

	public TeleportGUICommand() {
		super("tpg");
	}

	@Override
	public void call(Player player, ArrayList<String> args) {
		if (!SpectateManager.isSpectator(player)) {
			F.warning(player, "Only spectators can teleport.");
			return;
		}
		if (args.size() == 0) {
			ArrayList<ItemStack> contents = new ArrayList<ItemStack>();
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				ItemStack teleport = ItemFactory.createItem(Material.SKULL, C.blue + "Teleport to " + C.yellow + p.getName());
				contents.add(teleport);
			}
			
			player.openInventory(InventoryFactory.createInv(C.aqua + "Teleport (Spectator)", contents));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void teleportGUIClick(InventoryClickEvent e) {
		if (e.getClickedInventory().getTitle() == null || !(e.getClickedInventory().getTitle().contains("Teleport")) || e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
		e.setCancelled(true);
		Player player = (Player) e.getWhoClicked();
		String playerName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).replace("Teleport to ", "");
		
		if (Bukkit.getPlayer(playerName) == null) {
			player.closeInventory();
			F.warning(player, "That player is no longer online.");
			return;
		}
		
		player.closeInventory();
		player.teleport(Bukkit.getPlayer(playerName));
	}

}
