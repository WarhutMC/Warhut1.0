package common.punish.commands;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import common.command.Command;
import common.punish.PunishManager;
import common.punish.Punishment;
import common.util.communication.C;
import common.util.communication.F;
import common.util.communication.S;
import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by luke on 10/24/15.
 */
public class PunishCommand extends Command {
    PunishManager punishManager;

    public PunishCommand(PunishManager punishManager) {
        super("punish", Arrays.asList("p"));
        super.setPermission("mcnet.mod");

        this.punishManager = punishManager;
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        if (args == null || args.size() == 0) {
            listCommands(player);
            return;
        }

        if (args.get(0).equalsIgnoreCase("ban")) {
            if (args.size() >= 4) {
                String uuid = this.buildTarget(args);
                if (uuid != null) {
                    buildAndExecutePunishment(player, args, uuid, Punishment.PunishType.BAN);
                }
            } else {
                F.message(player, "Punish", "/p ban <player> <time> <reason>");
                S.playSound(player, Sound.VILLAGER_NO);
            }
        }

        else if (args.get(0).equalsIgnoreCase("mute")) {
            if (args.size() >= 4) {
                String uuid = this.buildTarget(args);
                if (uuid != null) {
                    buildAndExecutePunishment(player, args, uuid, Punishment.PunishType.MUTE);
                }
            } else {
                F.message(player, "Punish", "/p mute <player> <time> <reason>");
                S.playSound(player, Sound.VILLAGER_NO);
            }
        }

        else if (args.get(0).equalsIgnoreCase("kick")) {
            if (args.size() >= 3) {
                String uuid = this.buildTarget(args);
                if (uuid != null) {
                    buildAndExecutePunishment(player, args, uuid, Punishment.PunishType.KICK);
                }
            } else {
                F.message(player, "Punish", "/p kick <player> <reason>");
                S.playSound(player, Sound.VILLAGER_NO);
            }
        }

        else if (args.get(0).equalsIgnoreCase("unban")) {
            String uuid = this.buildTarget(args);
            if (uuid != null) {
                punishManager.issueRevert(player, args, uuid, Punishment.PunishType.BAN, args.get(1));
            } else {
                F.message(player, "Punish", "/p unban <player>");
                S.playSound(player, Sound.VILLAGER_NO);
            }
        }

        else if (args.get(0).equalsIgnoreCase("unmute")) {
            String uuid = this.buildTarget(args);
            if (uuid != null) {
                punishManager.issueRevert(player, args, uuid, Punishment.PunishType.MUTE, args.get(1));
            } else {
                F.message(player, "Punish", "/p unmute <player>");
                S.playSound(player, Sound.VILLAGER_NO);
            }
        }

        else if (args.get(0).equalsIgnoreCase("active")) {
            player.sendMessage(C.divider);
            player.sendMessage(C.tab + C.gold + C.underline + "Active Punishments");
            player.sendMessage("");

            Date now = new Date();
            int i = 1;

            if (punishManager.punishments.isEmpty()) {
                player.sendMessage(C.tab + C.green + "Currently no active punishments on this server :)");
            } else {

                for (Punishment p : punishManager.punishments) {
                    if (p.expires.after(now)) {
                        Player target = Bukkit.getServer().getPlayer(UUID.fromString(p.uuid));
                        if (target != null) {
                            player.sendMessage(C.tab + C.gray + i + ". " + C.yellow + target.getName() + C.gray + " - "
                                    + C.red + p.punishType.display + C.gray + " for " + C.white + p.reason + C.gray + " by "
                                    + C.green + p.staff);

                            i++;
                        }
                    }
                }
            }
            player.sendMessage("");
            player.sendMessage(C.divider);

        } else {
            listCommands(player);
        }
    }

    public void buildAndExecutePunishment(Player staff, final ArrayList<String> args, String uuid, Punishment.PunishType punishType) {
        //p ban jimmy 1h spamming

        String lengthString = "";
        int minutes = 0;
        if (punishType != Punishment.PunishType.KICK) {
            lengthString = args.get(2);
            if (lengthString.contains("m")) {
                lengthString = lengthString.replace("m", "");
                minutes = Integer.parseInt(lengthString);
            } else if (lengthString.contains("h")) {
                lengthString = lengthString.replace("h", "");
                minutes = Integer.parseInt(lengthString) * 60;
            } else { //days
                lengthString = lengthString.replace("d", "");
                minutes = Integer.parseInt(lengthString) * 60 * 24;
            }
        }

        String reason = "";
        for (int i = 3; i < args.size(); i++) {
            if (i == args.size() - 1) {
                reason += args.get(i);
            } else {
                reason += args.get(i) + " ";
            }
        }

        Date now = new Date();
        Date expires = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(expires);
        c.add(Calendar.MINUTE, minutes);
        expires = c.getTime();

        final Punishment punishment = new Punishment(uuid, "", punishType, reason, staff.getName(), now, expires, minutes, false);

        if (punishment.punishType == Punishment.PunishType.BAN || punishment.punishType == Punishment.PunishType.IPBAN
                || punishment.punishType == Punishment.PunishType.KICK) {
            Player target = Bukkit.getServer().getPlayer(UUID.fromString(punishment.uuid));
            if (target != null) {
                target.kickPlayer(punishment.reason);
            }
        }

        else if (punishment.punishType == Punishment.PunishType.MUTE) {

        }

        Main.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                punishManager.issuePunishment(punishment, args.get(1));
            }
        });
    }

    public String buildTarget(ArrayList<String> args) {
        //p ban jimmy 1h spamming
        String targetName = args.get(1);
        Player target = Bukkit.getServer().getPlayer(targetName);

        if (target != null) {
            return target.getUniqueId().toString();
        } else {
            //database lookup
            DBObject query = new BasicDBObject("name", args.get(1));
            DBObject doc = punishManager.getPlayersCollection().findOne(query);

            return (String) doc.get("uuid");
        }
    }

    public void listCommands(Player player) {
        player.sendMessage(C.divider);
        player.sendMessage("");

        player.sendMessage(C.tab + C.gray + "/punish ban <player> <time> <reason>");
        player.sendMessage(C.tab + C.gray + "/punish unban <player> <time> <reason>");

        player.sendMessage("");

        player.sendMessage(C.tab + C.gray + "/punish kick <player>");

        player.sendMessage("");

        player.sendMessage(C.tab + C.gray + "/punish mute <player>");
        player.sendMessage(C.tab + C.gray + "/punish unmute <player>");

        player.sendMessage("");

        player.sendMessage(C.tab + C.gray + "/punish active");

        player.sendMessage("");
        player.sendMessage(C.tab + C.gray + "time examples: " + C.yellow + "1m" + C.gray + " | " + C.yellow + "1h" + C.gray + " | " + C.yellow + "1d");
        player.sendMessage(C.divider);
    }
}
