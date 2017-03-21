package common.message;

import common.command.Command;
import cyclegame.players.ProPlayer;
import common.util.communication.C;
import common.util.communication.F;
import common.util.communication.S;
import cyclegame.GameAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 10/24/15.
 */
public class ReplyCommand extends Command {
    MessageManager messageManager;

    public ReplyCommand(MessageManager messageManager) {
        super("r", Arrays.asList("reply"));
        this.messageManager = messageManager;
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        if (!GameAPI.getInstance().getPunishManager().isMuted(player, true)) {
            if (args != null && args.size() >= 1) {
                ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(player);

                if (messageManager.lastMessaged.containsKey(player.getName())) {
                    String targetName = messageManager.lastMessaged.get(player.getName());
                    Player target = Bukkit.getServer().getPlayer(targetName);
                    ProPlayer targetProPlayer = GameAPI.getPlayerHandler().getProPlayer(target);

                    if (target != null) {

                        String msg = joinMessage(args);

                        target.sendMessage(C.purple + "From " + proPlayer.getFormattedName() + C.purple + "> " + C.yellow + msg);
                        target.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 10, 1);

                        player.sendMessage(C.purple + "To " + targetProPlayer.getFormattedName() + C.purple + "> " + C.yellow + msg);
//                player.playSound(player.getCenter(), Sound.CHICKEN_EGG_POP, 10, 1);

                        messageManager.lastMessaged.put(player.getName(), target.getName());
                        messageManager.lastMessaged.put(target.getName(), player.getName());

                    } else {
                        F.message(player, "Message", C.red + targetName + C.gray + " is no longer online.");
                        S.playSound(player, Sound.VILLAGER_NO);
                    }
                } else {
                    F.message(player, "Message", "You have nobody to reply to!");
                    S.playSound(player, Sound.VILLAGER_NO);
                }
            } else {
                F.message(player, "Message", "/r <msg>");
                S.playSound(player, Sound.VILLAGER_NO);
            }
        }

    }

    public String joinMessage(ArrayList<String> args) {
        String msg = "";

        for (int i = 0; i < args.size(); i++) {
            msg += args.get(i) + " ";
        }

        return msg;
    }
}
