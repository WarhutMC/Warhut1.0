package cyclegame.players;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import common.kit.Kit;
import cyclegame.players.event.AsyncProPlayerJoinEvent;
import common.util.parse.ParseUtils;
import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import cyclegame.GameAPI;

import java.util.*;


/**
 * Created by luke on 10/17/15.
 *
 */
public class PlayerHandler implements Listener {

    public DBCollection playersCollection;
    public ArrayList<ProPlayer> proPlayers = new ArrayList<>();

    public DBCollection currentGameCollection;

    public PlayerHandler() {
        this.playersCollection = GameAPI.getInstance().db.getCollection("players");

        Main.registerListener(this);
    }

    public ProPlayer getProPlayer(Player player) {
        for (ProPlayer proPlayer : this.proPlayers) {
            if (proPlayer.player == player) {
                return proPlayer;
            }
        }
        return null;
    }

    /* USE ASYNC */
    public void updateDatabaseValue(DBCollection collection, ProPlayer proPlayer, String key, Object value) {
        collection.update(new BasicDBObject("uuid", proPlayer.player.getUniqueId().toString()),
                new BasicDBObject("$set", new BasicDBObject(key, value)));
    }

    /* USE ASYNC */
    public void updateDatabaseValue(DBCollection collection, String player, String key, Object value) {
        collection.update(new BasicDBObject("name", player),
                new BasicDBObject("$set", new BasicDBObject(key, value)));
    }

    /* USE ASYNC */
    public void incrementValue(DBCollection collection, ProPlayer proPlayer, String key, int amount) {
        collection.update(new BasicDBObject("uuid", proPlayer.player.getUniqueId().toString()),
                new BasicDBObject("$inc", new BasicDBObject(key, amount)));
    }

    /* USE ASYNC */
    public void incrementValue(DBCollection collection, String player, String key, int amount) {
        collection.update(new BasicDBObject("name", player),
                new BasicDBObject("$inc", new BasicDBObject(key, amount)));
    }

    /* USE ASYNC */
    public void incrementValue(DBCollection collection, UUID uuid, String key, int amount) {
        collection.update(new BasicDBObject("uuid", uuid.toString()),
                new BasicDBObject("$inc", new BasicDBObject(key, amount)));
    }

    /* USE ASYNC */
    public void uploadKitsToCurrentGame(ProPlayer proPlayer) {
        this.currentGameCollection.update(new BasicDBObject("uuid", proPlayer.player.getUniqueId().toString()),
                new BasicDBObject("$addToSet", new BasicDBObject("kits", new BasicDBObject("$each", proPlayer.gameStats.kits))));
    }

    /* USE ASYNC */
    public void globalPlayerDatabaseSync(ProPlayer proPlayer) {
        DBObject query = new BasicDBObject("uuid", proPlayer.player.getUniqueId().toString());
        DBObject doc = this.playersCollection.findOne(query);

        boolean isNewPlayer;

        if(doc != null) {
            isNewPlayer = false;

            proPlayer.setRank(Rank.valueOf((String) doc.get("rank")));
            proPlayer.setCoins(((Number) doc.get("coins")).intValue());
            proPlayer.setTickets(((Number) doc.get("tickets")).intValue());

            if (doc.get("xp") != null) {
                proPlayer.setXp(((Number) doc.get("xp")).intValue());
            }

            if (doc.get("radio") != null) {
                proPlayer.setRadio(((Boolean) doc.get("radio")).booleanValue());
            } else {
                updateDatabaseValue(this.playersCollection, proPlayer, "radio", true);
            }

            BasicDBList ips = (BasicDBList) doc.get("ips");
            String ip = ParseUtils.parseIP(proPlayer.player.getAddress());
            if (!ips.contains(ip)) {
                ips.add(ip);
                updateDatabaseValue(this.playersCollection, proPlayer, "ips", ips);
            }
            List<String> localIps = new ArrayList<>();
            for(Object o : ips) {
                localIps.add((String) o);
            }
            proPlayer.setIps(localIps);

            proPlayer.setFirstJoined((String) doc.get("first_joined"));

            updateDatabaseValue(this.playersCollection, proPlayer, "last_online_date", new Date().toString());

            if (!((String) doc.get("name")).equalsIgnoreCase(proPlayer.player.getName())) {
                updateDatabaseValue(this.playersCollection, proPlayer, "name", proPlayer.getPlayer().getName());
            }

            if (doc.get("hostname") == null) {
                updateDatabaseValue(this.playersCollection, proPlayer, "hostname", proPlayer.player.getAddress().getHostName());
            }

        } else {
            isNewPlayer = true;
            DBObject insert = new BasicDBObject("uuid", proPlayer.player.getUniqueId().toString());

            insert.put("name", proPlayer.player.getName());

            insert.put("rank", "regular");
            proPlayer.setRank(Rank.regular);

            insert.put("coins", 0);
            proPlayer.setCoins(0);

            insert.put("tickets", 0);
            proPlayer.setTickets(0);

            insert.put("xp", 0);
            proPlayer.setXp(0);

            insert.put("radio", true);

            List<String> ipList = Arrays.asList(ParseUtils.parseIP(proPlayer.player.getAddress()));
                    insert.put("ips", ipList);
            proPlayer.setIps(ipList);

            insert.put("join_date", new Date().toString());
            proPlayer.setFirstJoined(new Date().toString());

            insert.put("last_online_date", new Date().toString());

            this.playersCollection.insert(insert);
        }

        proPlayer.initialized = true;
        Bukkit.getServer().getPluginManager().callEvent(new AsyncProPlayerJoinEvent(proPlayer, isNewPlayer));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent event){
        final ProPlayer proPlayer = new ProPlayer(event.getPlayer());
        proPlayers.add(proPlayer);

        if (GameAPI.getServerMode() == GameAPI.ServerMode.GAME) {
            GameAPI.getMatch().dbSyncer.preSync(proPlayer);
        }

        GameAPI.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                globalPlayerDatabaseSync(proPlayer);

                if (GameAPI.getServerMode() == GameAPI.ServerMode.GAME) {
                    GameAPI.getMatch().dbSyncer.sync(proPlayer);
                }
            }
        });

        event.setJoinMessage("");
    }
    
    public void setCurrentGameCollection(String name) {
        name = name.replace(" ", "_");
        name = name.toLowerCase();

        this.currentGameCollection = GameAPI.getInstance().db.getCollection(name);
    }

    public void uploadEndOfGameStats(ProPlayer proPlayer) {
        this.incrementValue(this.currentGameCollection, proPlayer, "matches", 1);
        this.uploadKitsToCurrentGame(proPlayer);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        this.proPlayers.remove(this.getProPlayer(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(PlayerKickEvent event) {
        this.proPlayers.remove(this.getProPlayer(event.getPlayer()));
    }

    public static String formatKitNameForDatabase(Kit kit) {
        String s = kit
                .name;
        s = s.replace(" ", "_");
        s = s.toLowerCase();
        return s;
    }

    public DBCollection getPlayersCollection() {
        return playersCollection;
    }

    public ArrayList<ProPlayer> getProPlayers() {
        return proPlayers;
    }

    public DBCollection getCurrentGameCollection() {
        return currentGameCollection;
    }
}
