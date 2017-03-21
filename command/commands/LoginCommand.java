package common.command.commands;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import common.command.Command;
import common.util.communication.C;
import common.util.communication.F;
import cyclegame.GameAPI;
import main.Main;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by luke on 10/19/15.
 */
public class LoginCommand extends Command {
    DBCollection playersCollection;

    public LoginCommand() {
        super("login");
        this.playersCollection = GameAPI.getPlayerHandler().playersCollection;
    }

    @Override
    public void call(final Player player, final ArrayList<String> args) {
        GameAPI.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                DBObject doc = playersCollection.findOne(new BasicDBObject("uuid", player.getUniqueId().toString()));

                if (doc != null) {
                    if (doc.get("password") != null) {

                        if(args == null || args.size() == 0) {
                            F.message(player, "Login", "You already have a forum account setup.");
                            F.message(player, "Login", "If you forgot the password, type " + C.yellow + "/login reset");
                            return;
                        }
                    }

                    String password = generateRandomPassword(5);
                    playersCollection.update(new BasicDBObject("uuid", player.getUniqueId().toString()),
                            new BasicDBObject("$set", new BasicDBObject("password", password)));

                    player.sendMessage(C.divider);
                    player.sendMessage(C.gray + C.tab + "Link: " + C.aqua + "www.mcwar.us/login");
                    player.sendMessage("");
                    player.sendMessage(C.tab + "New Password: " + C.yellow + password);
                    player.sendMessage("");
                    player.sendMessage(C.divider);
                }
            }
        });
    }

    public static String generateRandomPassword(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random rng = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }
}
