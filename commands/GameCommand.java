package cyclegame.commands;

import common.command.Command;
import cyclegame.players.ProPlayer;
import common.util.communication.C;
import common.util.communication.F;
import cyclegame.GameAPI;
import cyclegame.rotation.GameMap;
import cyclegame.rotation.Match;
import cyclegame.templates.game.Game;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 10/17/15.
 */
public class GameCommand extends Command {

    public GameCommand() {
        super("game", Arrays.asList("g"));
        super.setPermission("mcnet.mod");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            if (event.getRightClicked() != null && event.getRightClicked() instanceof Player) {
                Player clicked = (Player) event.getRightClicked();
                ProPlayer clickedProPlayer = GameAPI.getPlayerHandler().getProPlayer(clicked);

                F.message(event.getPlayer(), "Spy", "Clicked " + C.yellow + clickedProPlayer.getFormattedName());
            }
        }
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
        ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(player);

        if(args.size() == 0) {
            player.sendMessage(C.divider);
            player.sendMessage("");

            player.sendMessage(C.tab + C.white + "/game start");
            player.sendMessage(C.tab + C.white + "/game end");
            player.sendMessage(C.tab + C.white + "/game cycle");
            player.sendMessage(C.tab + C.white + "/game setnext <map>");

            player.sendMessage("");
            player.sendMessage(C.divider);
        }

        else if (args.size() == 1) {

            /* /game start */
            if (args.get(0).equalsIgnoreCase("start")) {
                if (GameAPI.getMatch().matchState == Match.MatchState.PREGAME) {
                    GameAPI.getRotation().match.startGame();
                } else {
                    F.warning(player, "GAME already started!");
                }
            }

            /* /game cycle */
            else if (args.get(0).equalsIgnoreCase("cycle")) {
                GameAPI.getRotation().cycle();
            }

//            /* /game spy */
//            else if (args.get(0).equalsIgnoreCase("spy")) {
//                if (player.getGameMode() == GameMode.SPECTATOR) {
//                    player.setGameMode(GameMode.SURVIVAL);
//                    F.message(player, "GAME", "Spy mode " + C.red + "disabled" + C.gray + ".");
//                } else {
//                    player.setGameMode(GameMode.SPECTATOR);
//                    F.message(player, "GAME", "Spy mode " + C.green + "enabled" + C.gray + ".");
//                }
//            }

            /* /game end */
            else if (args.get(0).equalsIgnoreCase("end")) {
                if (GameAPI.getMatch().matchState == Match.MatchState.GAME) {
                    if (GameAPI.getMatch().game.gameState == Game.GameState.PLAYING) {
                        GameAPI.getMatch().game.endGame(GameAPI.getMatch().game.teams.get(0));
                    } else {
                        F.warning(player, "The GAME has already ended!");
                    }
                } else {
                    F.warning(player, "No GAME is currently running!");
                }
            }
        }

        else if (args.size() > 1) {
            if (args.get(0).equalsIgnoreCase("setnext") || args.get(0).equalsIgnoreCase("sn")) {
                if (args.size() >= 2) {
                    String mapName = args.get(1);
                    for (String s : args) {
                        if (args.indexOf(s) > 1) {
                            mapName += " " + s;
                        }
                    }

                    GameMap next = getMap(mapName);

                    if (next != null) {
                        GameAPI.getRotation().nextMatchIndex = GameAPI.getRotation().activeMaps.indexOf(next);

                        if (GameAPI.getMatch().matchState == Match.MatchState.PREGAME) {
                            GameAPI.getRotation().cycle();
                        }

                        F.broadcast("Staff", proPlayer.getFormattedName() + C.gray + "set the next map to " + C.yellow + next.name + C.gray + ".");
                    } else {
                        F.warning(player, "Couldn't find map " + C.red + mapName);
                    }
                } else {
                    F.message(player, "Command", "/game setnext <map>");
                }
            }

            else if (args.size() == 2) {
                if (args.get(0).equalsIgnoreCase("load") && args.get(1).equalsIgnoreCase("rotation")) {
                    GameAPI.getRotation().loadRotation();
                    F.message(player, "Admin", "Loaded Rotation");
                }
            }
        }
    }

    private GameMap getMap(String input) {
        input = input.toLowerCase().replaceAll(" ", "");
        GameMap result = null;
        for (GameMap loadedMap : GameAPI.getRotation().activeMaps) {
            if (loadedMap.name.equalsIgnoreCase(input.toLowerCase())) {
                result = loadedMap;
            }
        }
        if (result == null) {
            for (GameMap loadedMap : GameAPI.getRotation().activeMaps) {
                if (loadedMap.name.toLowerCase().startsWith(input.toLowerCase())) {
                    result = loadedMap;
                }
            }
        }
        return result;
    }
}
