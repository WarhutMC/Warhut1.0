package common.util.itemstack;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.GameProfileSerializer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.List;

/**
 * Created by luke on 10/17/15.
 */
public class ItemFactory {

    public static ItemStack createItem(Material material) {
        ItemStack item = new ItemStack(material);
        return item;
    }

    public static ItemStack createItem(Material material, int amount) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        return item;
    }

    public static ItemStack createItem(Material material, String name) {
        ItemStack item = createItem(material, 1);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createItem(Material material, String name, List<String> lore, int amount) {
        ItemStack item = createItem(material, name);
        item.setAmount(amount);

        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }
    
    public static ItemStack createItem(Material material, String name, List<String> lore, int amount, byte data) {
        ItemStack item = new ItemStack(material, amount, data);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    public static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = createItem(material, name);

        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createPotion(PotionType potionType, int level, String name) {
        Potion potion = new Potion(potionType);
        potion.setLevel(level);

        ItemStack itemStack = potion.toItemStack(1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public static ItemStack getPlayerSkull(String name) {
        ItemStack skull = new ItemStack(Material.SKULL, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(name);
        skull.setItemMeta(meta);
        return skull;
    }
}
