package common.update;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import cyclegame.GameAPI;
import main.Main;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;

import java.util.ArrayList;

/**
 * Created by luke on 10/30/15.
 */
public class UpdateManager {
    public ArrayList<ProServer> proServers = new ArrayList<>();
    public DBCollection gserversCollection;

    public UpdateManager() {
        this.gserversCollection = GameAPI.getInstance().getDb().getCollection("gservers");

        downloader();
        uploader();
    }

    public int uploader() {
        return GameAPI.getScheduler().scheduleAsyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                String address = Bukkit.getServer().getIp();
                int port = Bukkit.getServer().getPort();
                int players = Bukkit.getServer().getOnlinePlayers().size();
                int max = Bukkit.getServer().getMaxPlayers();
                double tps = MinecraftServer.getServer().recentTps[0];

                String game;
                String map;
                String id;
                if (GameAPI.getServerMode() == GameAPI.ServerMode.GAME) {
                    game = GameAPI.getRotation().match.map.game;

                    if (game.contains("warzone") || game.contains("dtm") || game.contains("domination") || game.contains("ctf")) {
                        game = "warzone";
                        id = "g" + (port - 12000);
                    }

                    else if (game.contains("infected")) {
                        game = "infected";
                        id = "inf" + (port - 13000);
                    }

                    else {
                        id = "g" + (port - 12000);
                    }

                    map = GameAPI.getRotation().match.map.name;
                } else {
                    game = "hub";
                    map = "hub";
                    id = "h" + (port - 11000);
                }

                DBObject doc = new BasicDBObject("id", id);
                doc.put("address", address);
                doc.put("port", port);
                doc.put("cyclegame", game);
                doc.put("map", map);
                doc.put("players", players);
                doc.put("max", max);
                doc.put("tps", tps);
                doc.put("updated", System.currentTimeMillis());

                DBObject query = new BasicDBObject("id", id);

                if (gserversCollection.findOne(query) != null) {
                    gserversCollection.findAndModify(query, doc);
                } else {
                    gserversCollection.insert(doc);
                }
            }
        }, 20L, 20L);
    }

    public int downloader() {
        return GameAPI.getScheduler().scheduleAsyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                DBCursor cursor = gserversCollection.find();
                ArrayList<ProServer> temp = new ArrayList<ProServer>();

                while (cursor.hasNext()) {
                    DBObject doc = cursor.next();

                    String id = (String) doc.get("id");
                    String address = (String) doc.get("address");
                    int port = (int) doc.get("port");
                    String game = (String) doc.get("cyclegame");
                    String map = (String) doc.get("map");
                    int players = (int) doc.get("players");
                    int max = (int) doc.get("max");
                    double tps = (double) doc.get("tps");
                    long updated = (long) doc.get("updated");

                    ProServer proServer = new ProServer(id, address, port, game, map, players, max, tps, updated);
                    temp.add(proServer);
                }

                proServers.clear();
                for (ProServer proServer : temp) {
                    proServers.add(proServer);
                }
            }
        }, 20L, 20L);
    }

    public class ProServer {
        String id;
        String address;
        int port;
        String game;
        String map;
        int players;
        int max;
        double tps;
        long updated;
        boolean online;

        public ProServer(String id, String address, int port, String game, String map, int players, int max, double tps, long updated) {
            this.id = id;
            this.address = address;
            this.port = port;
            this.game = game;
            this.map = map;
            this.players = players;
            this.max = max;
            this.tps = tps;
            this.updated = updated;
            this.online = System.currentTimeMillis() - updated <= 4000;
        }
        
        /*
         * ProServer "getters"
         */
        
        public String getId(){
        	return this.id;
        }
        
        public String getAddress(){
        	return this.address;
        }
        
        public int getPort(){
        	return this.port;
        }
        
        public String getGame(){
        	return this.game;
        }
        
        public String getMap(){
        	return this.map;
        }
        
        public int getOnlinePlayers(){
        	return this.players;
        }
        
        public int getMaxOnlinePlayers(){
        	return this.max;
        }
        
        public double getTPS(){
        	return this.tps;
        }

        public int getPlayers() {
            return players;
        }

        public int getMax() {
            return max;
        }

        public double getTps() {
            return tps;
        }

        public long getUpdated() {
            return updated;
        }

        public boolean isOnline() {
            return online;
        }
        
        public boolean isFull() {
            if(this.max == this.players){
            	return true;
            }else{
            	return false;
            }
        }
    }

    public ArrayList<ProServer> getProServers() {
        return proServers;
    }

    public DBCollection getGserversCollection() {
        return gserversCollection;
    }
}
