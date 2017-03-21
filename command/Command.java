package common.command;

import common.util.communication.C;
import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luke on 10/17/15.
 */


//Source tree push test

public abstract class Command implements Listener {
    public String name;
    public String permission = null;
    public List<String> aliases;
    public boolean allowConsole = false;

    public Command(String name) {
        this.name = name;
        this.aliases = new ArrayList<>();

        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public Command(String name, List<String> aliases) {
        this.name = name;
        this.aliases = aliases;

        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public void onConsole(ServerCommandEvent event) {
        if (this.allowConsole) {
            ArrayList<String> args = new ArrayList<String>();

            //Define args
            for (String s : event.getCommand().split(" ")) {
                args.add(s);
            }

            //Remove command from args
            String cmd = args.get(0);
            args.remove(0);

            if (cmd.equalsIgnoreCase("/" + name)) {
                event.setCancelled(true);
                call(null, args);

                return;
            } else if (!this.aliases.isEmpty()) {
                for (String alias : this.aliases) {
                    if (cmd.equalsIgnoreCase("/" + alias)) {
                        event.setCancelled(true);

                        call(null, args);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        ArrayList<String> args = new ArrayList<String>();

        //Define args
        for (String s : event.getMessage().split(" ")) {
            args.add(s);
        }

        //Remove command from args
        String cmd = args.get(0);
        args.remove(0);

        if (cmd.equalsIgnoreCase("/" + name)) {
            event.setCancelled(true);

            if (this.permission != null) {
                if (!player.hasPermission(this.permission)) {
                    player.sendMessage(C.red + "You do not have access to this command.");
                    return;
                }
            }

            call(player, args);
            return;
        } else if (!this.aliases.isEmpty()) {
            for (String alias : this.aliases) {
                if (cmd.equalsIgnoreCase("/" + alias)) {
                    event.setCancelled(true);

                    if (this.permission != null) {
                        if (!player.hasPermission(this.permission)) {
                            player.sendMessage(C.red + "You do not have access to this command.");
                            return;
                        }
                    }

                    call(player, args);
                    return;
                }
            }
        }
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public abstract void call(Player player, ArrayList<String> args);
}
