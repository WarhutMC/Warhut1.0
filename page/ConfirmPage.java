package common.page;

import common.util.itemstack.ItemFactory;
import common.util.communication.C;
import common.util.communication.S;
import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by luke on 10/19/15.
 */
public class ConfirmPage implements Listener {
    Runnable yes;
    Runnable no;
    Player player;

    Inventory inventory;

    public ConfirmPage(Player player, Runnable yes, Runnable no, ItemStack topic, String yesMessage, String noMessage) {
        this.player = player;
        this.yes = yes;
        this.no = no;


        this.inventory = Bukkit.getServer().createInventory(null, 54, C.underline + player.getName() + " Confirmation");

        this.inventory.setItem(4, topic);

        this.inventory.setItem(19, ItemFactory.createItem(Material.EMERALD_BLOCK, C.green + C.bold + yesMessage));
        this.inventory.setItem(20, ItemFactory.createItem(Material.EMERALD_BLOCK, C.green + C.bold + yesMessage));
        this.inventory.setItem(21, ItemFactory.createItem(Material.EMERALD_BLOCK, C.green + C.bold + yesMessage));
        this.inventory.setItem(28, ItemFactory.createItem(Material.EMERALD_BLOCK, C.green + C.bold + yesMessage));
        this.inventory.setItem(29, ItemFactory.createItem(Material.EMERALD_BLOCK, C.green + C.bold + yesMessage));
        this.inventory.setItem(30, ItemFactory.createItem(Material.EMERALD_BLOCK, C.green + C.bold + yesMessage));
        this.inventory.setItem(37, ItemFactory.createItem(Material.EMERALD_BLOCK, C.green + C.bold + yesMessage));
        this.inventory.setItem(38, ItemFactory.createItem(Material.EMERALD_BLOCK, C.green + C.bold + yesMessage));
        this.inventory.setItem(39, ItemFactory.createItem(Material.EMERALD_BLOCK, C.green + C.bold + yesMessage));


        this.inventory.setItem(23, ItemFactory.createItem(Material.REDSTONE_BLOCK, C.red + C.bold + noMessage));
        this.inventory.setItem(24, ItemFactory.createItem(Material.REDSTONE_BLOCK, C.red + C.bold + noMessage));
        this.inventory.setItem(25, ItemFactory.createItem(Material.REDSTONE_BLOCK, C.red + C.bold + noMessage));
        this.inventory.setItem(32, ItemFactory.createItem(Material.REDSTONE_BLOCK, C.red + C.bold + noMessage));
        this.inventory.setItem(33, ItemFactory.createItem(Material.REDSTONE_BLOCK, C.red + C.bold + noMessage));
        this.inventory.setItem(34, ItemFactory.createItem(Material.REDSTONE_BLOCK, C.red + C.bold + noMessage));
        this.inventory.setItem(41, ItemFactory.createItem(Material.REDSTONE_BLOCK, C.red + C.bold + noMessage));
        this.inventory.setItem(42, ItemFactory.createItem(Material.REDSTONE_BLOCK, C.red + C.bold + noMessage));
        this.inventory.setItem(43, ItemFactory.createItem(Material.REDSTONE_BLOCK, C.red + C.bold + noMessage));

        if(player != null) {
            S.playSound(player, Sound.ENDERDRAGON_WINGS);
            player.openInventory(this.inventory);
        }

        Main.registerListener(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getName() == this.inventory.getName()) {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();

            if (event.getCurrentItem().getType() == Material.EMERALD_BLOCK) {
                p.closeInventory();

                this.yes.run();
            } else if (event.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
                p.closeInventory();
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 10, 1);

                this.no.run();
            }
        }
    }


    /*
    * ************************************************
    *
    * Cleanup
    *
    * ************************************************
    */

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(event.getInventory().getName() == this.inventory.getName()) {
            this.unload();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer() == this.player) {
            this.unload();
        }
    }

    @EventHandler
    public void onQuit(PlayerKickEvent event) {
        if (event.getPlayer() == this.player) {
            this.unload();
        }
    }

    public void unload() {
        this.player = null;
        this.inventory.clear();
        Main.unloadListener(this);
        this.no.run();
    }
}
