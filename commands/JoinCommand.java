package cyclegame.commands;

import common.command.Command;
import common.util.communication.C;
import common.util.communication.F;
import common.util.communication.S;
import common.util.itemstack.EnchantGlow;
import cyclegame.GameAPI;
import cyclegame.players.ProPlayer;
import cyclegame.rotation.Match;
import cyclegame.templates.event.GameStartEvent;
import cyclegame.templates.game.Game;
import cyclegame.templates.game.Team;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by luke on 12/13/15.
 */
public class JoinCommand extends Command {
    private Game game;
    private ArrayList<Player> pregameJoinList = new ArrayList<>();

    public JoinCommand(Game game) {
        super("join");
        this.game = game;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.pregameJoinList.remove(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerKickEvent event) {
        this.pregameJoinList.remove(event.getPlayer());
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        Collections.shuffle(pregameJoinList);
        for (Player player : pregameJoinList) {
            ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(player);
            game.joinGame(proPlayer);
        }
        pregameJoinList.clear();
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        if (GameAPI.getMatch().getMatchState() == Match.MatchState.PREGAME) {
            if (!pregameJoinList.contains(player)) {
                pregameJoinList.add(player);
            }
            EnchantGlow.addGlow(player.getInventory().getItem(1));
            player.sendMessage(C.green + "You will join the game when the match starts.");
            S.playSound(player, Sound.LEVEL_UP);
            return;
        }

        if (!game.spectators.players.contains(player)) {
            F.warning(player, C.red + "You are already in the game!");
            return;
        }

        Team existingTeam = game.getTeam(player);
        if (existingTeam != null) {
            game.getTeam(player).players.remove(player);
        }

        ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(player);
        game.joinGame(proPlayer);
    }
}
