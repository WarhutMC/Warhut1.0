package common.bungee;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Luke on 11/24/14.
 */
public class Bungee {
    public static void sendToServer(Plugin plugin, Player player, String name) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(name); // Send back to lobby
        } catch (IOException e) {
            // Can never happen
        }
        player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }
}
