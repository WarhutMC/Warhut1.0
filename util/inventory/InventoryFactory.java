package common.util.inventory;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import common.util.itemstack.ItemFactory;

/*
 * Created by Gamer 12/14/15
 */
public class InventoryFactory {

	public static Inventory createInv(int size) {
		Inventory inventory = Bukkit.createInventory(null, size);
		return inventory;
	}
	
	public static Inventory createInv(String title, int size) {
		Inventory inventory = Bukkit.createInventory(null, size, title);
		return inventory;
	}

	public static Inventory createInv(List<ItemStack> contents) {
		Inventory inventory = Bukkit.createInventory(null, (int) Math.ceil(contents.size() / 9) * 9);
		int count = 0;
		for (ItemStack item : contents) {
			inventory.setItem(count, item);
			count++;
		}

		return inventory;
	}
	
	public static Inventory createInv(String title, List<ItemStack> contents) {
		Inventory inventory = Bukkit.createInventory(null, (int) Math.ceil(contents.size() / 9) * 9, title);
		int count = 0;
		for (ItemStack item : contents) {
			inventory.setItem(count, item);
			count++;
		}

		return inventory;
	}

	public static Inventory createInv(ItemStack[] contents) {
		Inventory inventory = Bukkit.createInventory(null, (int) Math.ceil(contents.length / 9) * 9);
		int count = 0;
		for (ItemStack item : contents) {
			inventory.setItem(count, item);
			count++;
		}

		return inventory;
	}
	
	public static Inventory createInv(String title, ItemStack[] contents) {
		Inventory inventory = Bukkit.createInventory(null, (int) Math.ceil(contents.length / 9) * 9, title);
		int count = 0;
		for (ItemStack item : contents) {
			inventory.setItem(count, item);
			count++;
		}

		return inventory;
	}

	public static Inventory createInv(int size, List<ItemStack> contents) {
		Inventory inventory = Bukkit.createInventory(null, size);
		int count = 0;
		for (ItemStack item : contents) {
			inventory.setItem(count, item);
			count++;
		}

		return inventory;
	}
	
	public static Inventory createInv(String title, int size, List<ItemStack> contents) {
		Inventory inventory = Bukkit.createInventory(null, size, title);
		int count = 0;
		for (ItemStack item : contents) {
			inventory.setItem(count, item);
			count++;
		}

		return inventory;
	}

	public static Inventory createInv(int size, ItemStack[] contents) {
		Inventory inventory = Bukkit.createInventory(null, size);
		int count = 0;
		for (ItemStack item : contents) {
			inventory.setItem(count, item);
			count++;
		}

		return inventory;
	}
	
	public static Inventory createInv(String title, int size, ItemStack[] contents) {
		Inventory inventory = Bukkit.createInventory(null, size, title);
		int count = 0;
		for (ItemStack item : contents) {
			inventory.setItem(count, item);
			count++;
		}

		return inventory;
	}

}
