package common.broadcast;

import com.mongodb.*;
import cyclegame.players.ProPlayer;
import common.util.communication.C;
import main.Main;
import org.bukkit.ChatColor;
import cyclegame.GameAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by luke on 10/26/15.
 */
public class BroadcastManager {
    public DBCollection broadcastsCollection;

    public HashMap<String, String> broadcasts = new HashMap<>();
    public List keys;
    public int interval = 90; //seconds

    public int index = 0;
    public int runnableID = -1;

    public BroadcastManager() {
        this.broadcastsCollection = GameAPI.getInstance().getDb().getCollection("broadcasts");
        this.updateBroadcasts();

        new AutoBroadcastCommand(this);
    }

    public void updateBroadcasts() {
        this.index = 0;
        this.broadcasts.clear();
        DBCursor cursor = this.broadcastsCollection.find();
        while (cursor.hasNext()) {
            DBObject doc = cursor.next();

            this.broadcasts.put((String) doc.get("id"), (String) doc.get("message"));
        }

        this.keys = new ArrayList<>(broadcasts.keySet());

        this.startBroadcast();
    }

    public void startBroadcast() {
        if (this.runnableID != -1) {
            GameAPI.getScheduler().cancelTask(this.runnableID);
        }

        this.runnableID = GameAPI.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                String message = broadcasts.get(keys.get(index));
                message = ChatColor.translateAlternateColorCodes('&', message);

                for (ProPlayer proPlayer : GameAPI.getPlayerHandler().getProPlayers()) {
                    String s = message.replace("%player%", proPlayer.player.getName());

                    proPlayer.player.sendMessage("");
                    proPlayer.player.sendMessage(C.gray + "[" + C.daqua + C.bold + "TIP" + C.gray + "] " + s);
                    proPlayer.player.sendMessage("");
                }

                index++;
                if (index >= keys.size()) {
                    index = 0;
                }
            }
        }, this.interval * 20, this.interval * 20);
    }
}
