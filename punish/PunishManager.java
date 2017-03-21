package common.punish;

import com.mongodb.*;
import common.punish.commands.DisableVanillaPunishCommand;
import common.punish.commands.PunishCommand;
import common.util.communication.C;
import common.util.communication.F;
import common.util.parse.ParseUtils;
import common.util.parse.TimeUtils;
import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by luke on 10/24/15.
 */
public class PunishManager implements Listener {
    public DB db;
    public DBCollection punishCollection;
    public DBCollection playersCollection;
    public ArrayList<Punishment> punishments = new ArrayList<>();

    public PunishManager(DB db, DBCollection playersCollection) {
        this.db = db;
        this.punishCollection = db.getCollection("punish");
        this.playersCollection = playersCollection;

        new PunishCommand(this);
        new DisableVanillaPunishCommand();

        Main.registerListener(this);
    }

    public void issueRevert(Player staff, ArrayList<String> args, String uuid, Punishment.PunishType punishType, String targetName) {
        BasicDBList list = new BasicDBList();
        list.add(new BasicDBObject("uuid", uuid));
        list.add(new BasicDBObject("type", punishType.toString()));

        DBObject query = new BasicDBObject("$and", list);

        DBCursor cursor = this.punishCollection.find(query);
        while (cursor.hasNext()) {
            DBObject doc = cursor.next();

            String punishTypeString = (String) doc.get("type");
            if (punishType.toString().equalsIgnoreCase(punishTypeString)) {
                SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy");
                Date now = new Date();
                Date expires = new Date();
                try {
                    expires = sdf.parse((String) doc.get("expires"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (expires.after(now)) {
                    BasicDBObject updateQuery = new BasicDBObject("uuid", uuid);

                    List<BasicDBObject> updateQueryList = new ArrayList<>();
                    updateQueryList.add(new BasicDBObject("expires", (String) doc.get("expires")));

                    updateQuery.put("$and", updateQueryList);

                    this.punishCollection.update(updateQuery,
                            new BasicDBObject("$set", new BasicDBObject("reverted", true)), false, true);
                }
            }
        }

        ArrayList<Punishment> toRemove = new ArrayList<>();
        for (Punishment p : this.punishments) {
            if (p.uuid.equalsIgnoreCase(uuid)) {
                if (p.punishType == punishType) {
                    Date now = new Date();
                    if (p.expires.after(now)) {
                        toRemove.add(p);
                    }
                }
            }
        }

        for (Punishment p : toRemove) {
            this.punishments.remove(p);
        }
        /* ==================== */

        String staffMsg = C.purple + targetName + C.gray + " was " + C.green + punishType.unDisplay + C.gray + " by "
                + C.yellow + staff.getName() + C.gray + ".";

        String allMsg = C.purple + targetName + C.gray + " was " + C.green + punishType.unDisplay + C.gray + ".";

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage("");
            player.sendMessage(C.red + "[Punish] " + staffMsg);
            player.sendMessage("");
        }
    }

    public void issuePunishment(Punishment punishment, String targetName) {

        DBObject doc = new BasicDBObject("uuid", punishment.uuid);
        doc.put("ip", punishment.ip);
        doc.put("type", punishment.punishType.toString());
        doc.put("reason", punishment.reason);
        doc.put("staff", punishment.staff);
        doc.put("length", punishment.length);
        doc.put("issued", punishment.issued.toString());
        doc.put("expires", punishment.expires.toString());
        doc.put("reverted", false);

        this.punishCollection.insert(doc);

        this.punishments.add(punishment);

        /* ==================== */

        String staffMsg = "";
        String allMsg = "";

        if (punishment.punishType == Punishment.PunishType.KICK) {
            staffMsg = C.purple + targetName + C.gray + " was " + C.red + punishment.punishType.display + C.gray + " by "
                    + C.yellow + punishment.staff + C.gray + ".";

            allMsg = C.purple + targetName + C.gray + " was " + C.red + punishment.punishType.display + C.gray + ".";
        } else {
            staffMsg = C.purple + targetName + C.gray + " was " + C.red + punishment.punishType.display + C.gray + " by "
                    + C.yellow + punishment.staff + C.gray + " for " + C.green + TimeUtils.formatToTimeFromMinutes(punishment.length) + C.gray + " with reason " + C.white + punishment.reason + C.gray + ".";

            allMsg = C.purple + targetName + C.gray + " was " + C.red + punishment.punishType.display + C.gray + " for " + C.yellow + TimeUtils.formatToTimeFromMinutes(punishment.length) + C.gray + ".";
        }


        for (Player player : Bukkit.getServer().getOnlinePlayers()) {

            player.sendMessage("");
            player.sendMessage(C.red + "[Punish] " + staffMsg);
            player.sendMessage("");

        }
    }

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        F.log("Prejoin: " + event.getName());
//        PunishmentProfile punishmentProfile = new PunishmentProfile(event.getUniqueId(), ParseUtils.parseIP(event.getAddress().getHostName()));

        DBObject clause1 = new BasicDBObject("uuid", event.getUniqueId().toString());
        DBObject clause2 = new BasicDBObject("ip", ParseUtils.parseIP(event.getAddress().getHostName()));
        BasicDBList or = new BasicDBList();
        or.add(clause1);
        or.add(clause2);
        DBObject query = new BasicDBObject("$or", or);

        DBCursor cursor = this.punishCollection.find(query);
        while (cursor.hasNext()) {
            DBObject doc = cursor.next();

            String uuid = (String) doc.get("uuid");
            String ip = (String) doc.get("ip");
            Punishment.PunishType punishType = Punishment.PunishType.valueOf((String) doc.get("type"));
            String reason = (String) doc.get("reason");
            String staff = (String) doc.get("staff");

            SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy");
            Date issued = new Date();
            Date expires = new Date();
            try {
                issued = sdf.parse((String) doc.get("issued"));
                expires = sdf.parse((String) doc.get("expires"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int length = (int) doc.get("length");
            boolean reverted = (boolean) doc.get("reverted");

            Punishment punishment = new Punishment(uuid, ip, punishType, reason, staff, issued, expires, length, reverted);

            if(!reverted) {
                Date current = new Date();

                if (expires.after(current)) {
                    //punishment still in effect

                    if (punishment.punishType == Punishment.PunishType.BAN || punishment.punishType == Punishment.PunishType.IPBAN) {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, C.gray + "Banned for: " + C.red + punishment.reason + "\n"
                                + C.gray + "Expires: " + C.yellow + expires);
                        return;
                    }
                }
            }
            this.punishments.add(punishment);
        }
    }

    public void removePunishments(UUID uuid) {
        ArrayList<Punishment> toRemove = new ArrayList<>();

        for (Punishment p : this.punishments) {
            if (p.uuid.toString().equalsIgnoreCase(uuid.toString())) {
                toRemove.add(p);
            }
        }

        for (Punishment p : toRemove) {
            this.punishments.remove(p);
        }
    }

    public boolean isMuted(Player player, boolean message) {
        Date now = new Date();
        for (Punishment p : this.punishments) {
            if (p.uuid.equalsIgnoreCase(player.getUniqueId().toString())) {
                if (p.punishType == Punishment.PunishType.MUTE && p.expires.after(now)) {
                    if (message) {
                        F.message(player, "Punish", "You are muted until " + C.yellow + p.expires + C.gray + ".");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (this.isMuted(event.getPlayer(), true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removePunishments(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        removePunishments(event.getPlayer().getUniqueId());
    }

    public DB getDb() {
        return db;
    }

    public DBCollection getPunishCollection() {
        return punishCollection;
    }

    public DBCollection getPlayersCollection() {
        return playersCollection;
    }

    public ArrayList<Punishment> getPunishments() {
        return punishments;
    }
}
