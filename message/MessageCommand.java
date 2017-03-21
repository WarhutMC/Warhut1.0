package common.message;

import common.command.Command;
import cyclegame.players.ProPlayer;
import common.util.communication.C;
import common.util.communication.F;
import common.util.communication.S;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import cyclegame.GameAPI;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 10/24/15.
 */
public class MessageCommand extends Command {
    MessageManager messageManager;

    public MessageCommand(MessageManager messageManager) {
        super("msg", Arrays.asList("message", "tell", "pm", "w", "whisper", "minecraft:tell"));
        this.messageManager = messageManager;
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        if (!GameAPI.getInstance().getPunishManager().isMuted(player, true)) {
            if (args != null && args.size() >= 2) {
                ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(player);

                Player target = Bukkit.getServer().getPlayer(args.get(0));
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
                    F.message(player, "Message", "Couldn't find " + C.red + args.get(0));
                    S.playSound(player, Sound.VILLAGER_NO);
                }

            } else {
                F.message(player, "Message", "/msg <player> <message>");
                S.playSound(player, Sound.VILLAGER_NO);
            }
        }

    }

    public String joinMessage(ArrayList<String> args) {
        String msg = "";

        for (int i = 1; i < args.size(); i++) {
            msg += args.get(i) + " ";
        }

        return msg;
    }
}
