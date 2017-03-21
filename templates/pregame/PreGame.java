package cyclegame.templates.pregame;

import common.damage.DamageInfo;
import common.damage.DamageUtils;
import cyclegame.players.ProPlayer;
import cyclegame.players.Rank;
import cyclegame.players.event.AsyncProPlayerJoinEvent;
import common.util.ActionBar;
import common.util.communication.C;
import common.util.communication.F;
import common.util.communication.S;
import common.util.scoreboard.SimpleScoreboard;
import cyclegame.GameAPI;
import cyclegame.rotation.GameMap;
import cyclegame.rotation.Match;
import cyclegame.templates.game.Game;
import main.Main;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;

/**
 * Created by luke on 12/8/15.
 */
public abstract class PreGame implements Listener {
    private Game game;
    private GameMap map;
    private World world;
    private Location spawn;

    //Scoreboard
    public int scoreboardRunnableID;

    //Countdown
    public static int countdownStartingTime = 35;
    public static int minimumPlayers = 2;
    public int countdownRunnableID;
    public int countdownTime = countdownStartingTime;
    public boolean countdownPaused = false;
    public long lastNotEnoughPlayersMessage = 0;

    public ArrayList<String> builders = new ArrayList<>();

    public PreGame(Game game) {
        this.game = game;
        this.map = game.map;
    }

    protected void setupPreGame() {
        this.world = map.getWorld();
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setTime(6000);

        if (map.getLocations().containsKey("spectate_spawn")) {
            this.spawn = map.getLocation("spectate_spawn");
        } else {
            //so current maps don't break
            this.spawn = map.getLocation("blue-spawn");
        }
    }

    public void startPreGame() {
        this.setupPreGame();

        if (!Bukkit.getServer().getOnlinePlayers().isEmpty()) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                this.setup(player);
            }
        }

        this.scoreboardRunnableID = this.updateScoreboards();
        this.countdownRunnableID = this.cycleCountdown();

        Main.registerListener(this);
    }

    public void setup(Player player) {
        game.getSpectateManager().addSpectator(player);
    }

    /*
    * ************************************************
    *
    * Countdown
    *
    * ************************************************
    */

    public int cycleCountdown() {
        return GameAPI.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                int playersOnline = Bukkit.getServer().getOnlinePlayers().size();
                if (!countdownPaused && playersOnline >= minimumPlayers) {
                    if(countdownTime > 0) {
                        if (countdownTime % 10 == 0 && countdownTime <= 60) {
                            Bukkit.broadcastMessage(C.green + "Starting in " + C.red + countdownTime + " seconds");
                        } else if (countdownTime % 60 == 0) {
                            Bukkit.broadcastMessage(C.green + "Starting in " + C.red + (countdownTime / 60) + " minutes");
                        }

                        if (countdownTime <= 5) {
                            Bukkit.broadcastMessage(C.green + "Starting in " + C.red + countdownTime + " seconds");
                            S.plingAll();
                        }

                        /* =============== */

                        ActionBar actionBar = new ActionBar(C.white + C.bold + "Game starting in " + C.red + countdownTime + " seconds");
                        actionBar.sendToServer();

                        /* =============== */

                        countdownTime -= 1;

                    } else {
                        GameAPI.getScheduler().cancelTask(countdownRunnableID);
                        GameAPI.getMatch().startGame();
                    }

                } else {
                    if (playersOnline < minimumPlayers && !countdownPaused && !Bukkit.getServer().getOnlinePlayers().isEmpty()) {
                        if (((System.currentTimeMillis() - lastNotEnoughPlayersMessage) / 1000) >= 50) {
                            lastNotEnoughPlayersMessage = System.currentTimeMillis();

                            F.broadcast("Not enough players! Need a total of " + C.yellow + minimumPlayers + C.gray + " for the game to start.");
                        }
                    }
                }
            }
        }, 20L, 20L);
    }

    /*
    * ************************************************
    *
    * Scoreboard
    *
    * ************************************************
    */

    public void updateScoreboard(ProPlayer proPlayer) {

        SimpleScoreboard board = proPlayer.getScoreboard();

//        for (Rank rank : Rank.values()) {
//            if (board.getScoreboard().getTeam(rank.toString()) == null) {
//                org.bukkit.scoreboard.Team t = board.getScoreboard().registerNewTeam(rank.toString());
//                t.setPrefix(rank.getTag());
//            }
//        }
//
//        for (ProPlayer other : GameAPI.getPlayerHandler().proPlayers) {
//            org.bukkit.scoreboard.Team t = board.getScoreboard().getTeam(other.getRank().toString());
//            t.addPlayer(other.player);
//        }

        board.add("  ", 4);

        board.add(C.green + "Map: " + C.white + game.map.getName().split(" - ")[0], 3);
        board.add(C.green + "Game: " + C.white + game.gameSettings.gameName, 2);

        board.add(" ", 1);


        board.send(proPlayer.player);
        board.update();
    }

    public int updateScoreboards() {

        for (ProPlayer proPlayer : GameAPI.getPlayerHandler().getProPlayers()) {
            proPlayer.generateNewScoreboard();
        }

        return GameAPI.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (ProPlayer proPlayer : GameAPI.getPlayerHandler().proPlayers) {
                    if (proPlayer.initialized) {
                        updateScoreboard(proPlayer);
                    }
                }
            }
        }, 20L, 20L);
    }

     /*
    * ************************************************
    *
    * Events
    *
    * ************************************************
    */

    @EventHandler
    public void onLobbyChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;

        ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(event.getPlayer());

        if (proPlayer.getRank().has(null, Rank.MOD, false)) {
            event.setFormat(
                    "<" + proPlayer.getRank().getTag() + C.aqua + proPlayer.player.getName() + C.white + ">" + " %2$s"
            );
        } else {
            event.setFormat(
                    "<" + proPlayer.getRank().getTag() + C.aqua + proPlayer.player.getName() + C.white + ">" + " %2$s"
            );
        }

    }

    @EventHandler
    public void onCrystalHit(EntityDamageEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_CRYSTAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPregameDamage(EntityDamageEvent event) {
        DamageInfo damageInfo = DamageUtils.getDamageInfo(event);

        if (damageInfo.getHurtPlayer() == null) {
            event.setDamage(0);
            return;
        }

        if (damageInfo.getHurtEntity().getType() == EntityType.ENDER_CRYSTAL) {
            event.setCancelled(true);
        }

        if (damageInfo.getDamagerPlayer() != null) {
            if (!this.builders.contains(damageInfo.getDamagerPlayer().getName())) {
                event.setCancelled(true);
            } else {
                event.setDamage(0); //let builders knock other players around
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLobbyPlace(BlockPlaceEvent event) {
        if (!this.builders.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLobbyBreak(BlockBreakEvent event) {
        if (!this.builders.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLobbyPlayerInteract(PlayerInteractEvent event) {
        if (!this.builders.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (!this.builders.contains(event.getPlayer().getName())) {
            event.setCancelled(true);

            Main.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    event.getPlayer().updateInventory();
                }
            }, 1);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        event.setLeaveMessage("");
    }

    @EventHandler
    public void onLobbyJoin(final PlayerJoinEvent event) {
        GameAPI.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (GameAPI.getMatch().matchState == Match.MatchState.PREGAME) {
                    setup(event.getPlayer());
                }
            }
        }, 0L);
    }

    @EventHandler
    public void onLobbyLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onAsyncLobbyJoin(AsyncProPlayerJoinEvent event) {
        if (event.isNewPlayer) {
            Bukkit.getServer().broadcastMessage(C.yellow + "[+] " + C.white + event.proPlayer.getFormattedName() + C.purple + " (new)");
        } else {
            Bukkit.getServer().broadcastMessage(C.yellow + "[+] " + C.white + event.proPlayer.getFormattedName());
        }
    }


    /*
    * ************************************************
    *
    * GETTERS + SETTERS
    *
    * ************************************************
    */

    public GameMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    public Location getSpawn() {
        return spawn;
    }

    public int getScoreboardRunnableID() {
        return scoreboardRunnableID;
    }

    public static int getCountdownStartingTime() {
        return countdownStartingTime;
    }

    public static int getMinimumPlayers() {
        return minimumPlayers;
    }

    public int getCountdownRunnableID() {
        return countdownRunnableID;
    }

    public int getCountdownTime() {
        return countdownTime;
    }

    public boolean isCountdownPaused() {
        return countdownPaused;
    }

    public long getLastNotEnoughPlayersMessage() {
        return lastNotEnoughPlayersMessage;
    }

    public ArrayList<String> getBuilders() {
        return builders;
    }

    public void unload() {
        Main.getScheduler().cancelTask(this.scoreboardRunnableID);
        Main.getScheduler().cancelTask(countdownRunnableID);

        Main.unloadListener(this);

        this.extraUnload();
    }

    public abstract void extraUnload();
}

