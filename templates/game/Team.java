package cyclegame.templates.game;

import cyclegame.players.ProPlayer;
import main.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import common.util.teams.ChatToData;

import java.util.ArrayList;

/**
 * Created by luke on 10/18/15.
 */
public class Team implements Listener {
    public String name;
    public ChatColor color;

    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Location> spawns = new ArrayList<>();

    public Team(String name, ChatColor color) {
        this.name = name;
        this.color = color;

        Main.registerListener(this);
    }

    public void add(ProPlayer proPlayer) {
        this.players.add(proPlayer.player);
    }

    public void remove(Player player) {
        if(this.players.contains(player)) {
            this.players.remove(player);
        }
    }

    public Team addSpawn(Location location) {
        this.spawns.add(location);
        return this;
    }

    public Team removeSpawn(Location location) {
        if(this.spawns.contains(location)) {
            this.spawns.remove(location);
        }
        return this;
    }

    public boolean onSameTeam(Player p1, Player p2) {
        if(this.players.contains(p1) && this.players.contains(p2)) {
            return true;
        }
        return false;
    }

    public Location getSpawnLocation() {
        return this.spawns.get(0);
    }
    
    public byte getBlockData(){
    	return ChatToData.ChatColorToData(this.color);
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Location> getSpawns() {
        return spawns;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.players.remove(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        this.players.remove(event.getPlayer());
    }

    public void unload() {
        this.players.clear();
        this.spawns.clear();
        Main.unloadListener(this);
    }

}
