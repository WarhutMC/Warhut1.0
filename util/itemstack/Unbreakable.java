package common.util.itemstack;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class Unbreakable {

	public static void setUnbreakable(ItemStack stack){
		
		ItemMeta meta = stack.getItemMeta();
		meta.spigot().setUnbreakable(true);
		stack.setItemMeta(meta);
		
	}
	
}
