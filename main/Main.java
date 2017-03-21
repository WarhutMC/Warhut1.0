package main;

import com.google.gson.JsonObject;
import common.util.communication.C;
import common.util.communication.F;
import common.util.json.JsonUtil;
import cyclegame.GameAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by luke on 12/3/15.
 */
public class Main extends JavaPlugin {
    private static Main instance;
    private GameAPI gameAPI;

    private ServerSettings serverSettings;

    /* Perms */
    public final HashMap<UUID, PermissionAttachment> perms = new HashMap<UUID, PermissionAttachment>();

    @Override
    public void onEnable() {
        instance = this;
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        initialize();
    }

    public void initialize() {
        JsonObject json = JsonUtil.convertFileToJSON("settings.json");
        if(json != null) {
            if (json.has("type")) {
                String type = json.get("type").getAsString();

                if (type.equalsIgnoreCase("warzone")) {
                    System.out.println("###################################");
                    System.out.println("   Creating Warzone instance...");
                    System.out.println("###################################");

                    this.serverSettings = new ServerSettings("MCWar", "MCWar.us", "www.mcwar.us");
                    this.gameAPI = new GameAPI();
                    gameAPI.onEnable();
                    return;
                }

//                else if (type.equalsIgnoreCase("mchost")) {
//                    this.serverSettings = new ServerSettings("MCHost", "mchost.co", "www.mchost.co");
//                    new Host();
//                    return;
//                }

            }
        }

        /* failed to load */
        F.log(C.divider);
        F.log("");
        F.log("Failed to load settings.json!");
        F.log("Shutting down server...");
        F.log("");
        F.log(C.divider);
        Bukkit.getServer().shutdown();
    }

    public static BukkitScheduler getScheduler() {
        return Bukkit.getServer().getScheduler();
    }

    public static void registerListener(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, Main.getInstance());
    }

    public static void unloadListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public static Main getInstance() {
        return instance;
    }

    public GameAPI getGameAPI() {
        return gameAPI;
    }

    public ServerSettings getServerSettings() {
        return serverSettings;
    }

    public HashMap<UUID, PermissionAttachment> getPerms() {
        return perms;
    }
}
