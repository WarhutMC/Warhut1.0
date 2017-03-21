package cyclegame.commands;

import common.command.Command;
import cyclegame.players.ProPlayer;
import cyclegame.players.Rank;
import common.util.communication.C;
import common.util.communication.F;
import cyclegame.templates.game.Team;
import cyclegame.GameAPI;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 10/19/15.
 */
public class TeamChatCommand extends Command {

    public TeamChatCommand() {
        super("t", Arrays.asList("tc", "team"));
    }

    @Override
    public void call(Player player, ArrayList<String> args) {

        if (args != null && args.size() > 0) {
            if (GameAPI.getMatch().game != null) {
                if (!GameAPI.getInstance().getPunishManager().isMuted(player, true)) {
                    ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(player);
                    Team team = GameAPI.getMatch().game.getTeam(player);

                    String msg = "";
                    for (String s : args) {
                        msg += s + " ";
                    }

                    for (Player teamMember : team.players) {
                        if (proPlayer.getRank().has(null, Rank.MOD, false)) {
                            teamMember.sendMessage(
                                    team.color + "[Team] " + proPlayer.getRank().getTag() + team.color + proPlayer.player.getName() + C.white + " " + msg
                            );
                        } else {
                            teamMember.sendMessage(
                                    team.color + "[Team] " + proPlayer.getRank().getTag() + team.color + proPlayer.player.getName() + C.gray + " " +  msg
                            );
                        }
                    }

                }
            }
        } else {
            F.warning(player, "/t [message]");
        }

    }
}
