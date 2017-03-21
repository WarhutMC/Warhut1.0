package cyclegame.games.ctf;

import java.util.ArrayList;
import java.util.HashMap;

import common.damage.DamageInfo;
import common.damage.DamageUtils;
import cyclegame.players.ProPlayer;
import common.util.communication.C;
import common.util.communication.F;
import common.util.npc.Hologram;
import common.util.scoreboard.SimpleScoreboard;
import cyclegame.games.ctf.flags.Flag;
import cyclegame.games.ctf.flags.FlagManager;
import cyclegame.games.dtm.replenish.ReplenishManager;
import cyclegame.rotation.GameMap;
import cyclegame.templates.game.Game;
import cyclegame.templates.game.Team;
import cyclegame.GameAPI;
import main.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Created by luke on 10/17/15.
 */
public class Ctf extends Game {
    public Team blueTeam;
    public Team redTeam;

    public int scoreboardRunnableID;

    public ReplenishManager replenishManager;
    public FlagManager flagManager;

    public static int spawnBlockPlaceRadius = 10;
    public static int zoneBlockPlaceRadius = 8;

    public static int pointsToWin = 3;
    
    public static int captureRadius = 2;
    
    public ArrayList<Hologram> holograms = new ArrayList<Hologram>();

    public HashMap<Team, Integer> points = new HashMap<Team, Integer>();
    
    public Ctf(GameMap map) {
        super(map);

        super.gameSettings.gameName = "CTF";
        super.gameSettings.setupMessage = C.white + "Capture the enemy " + C.yellow + C.bold + "FLAG" + C.white + " to obtain points! " +
                "The first team to " + C.yellow + pointsToWin + C.white + " points will " + C.green + C.bold + "Win the GAME" + C.white + "!";
    }

    @Override
    public void setupTeams() {
        this.blueTeam = super.addTeam(new Team("Blue", ChatColor.BLUE));
        this.redTeam = super.addTeam(new Team("Red", ChatColor.RED));

        this.blueTeam.addSpawn(map.getLocation("blue-spawn"));
        this.redTeam.addSpawn(map.getLocation("red-spawn"));
    }

    @Override
    public void setupGame() {
        this.flagManager = new FlagManager(this);
        this.scoreboardRunnableID = this.updateScoreboards();

        this.replenishManager = new ReplenishManager(40L);

        super.startGame();
    }

    public void updateScoreboard(ProPlayer proPlayer) {
        SimpleScoreboard board = proPlayer.getScoreboard();

        for (Team team : super.teams) {
            if (board.getScoreboard().getTeam(team.name) == null) {
                org.bukkit.scoreboard.Team t =  board.getScoreboard().registerNewTeam(team.name);
                t.setPrefix(team.color.toString());
            }

            for (Player player : team.players) {
                org.bukkit.scoreboard.Team t = board.getScoreboard().getTeam(team.name);
                t.addPlayer(player);
            }
        }


        board.add(C.blue + C.bold + "BLUE TEAM", 13);
        board.add(C.white + flagManager.points.get(blueTeam) + " ", 12);

        board.add(" ", 11);

        board.add(C.red + C.bold + "RED TEAM", 10);
        board.add(C.white + flagManager.points.get(redTeam) + "  ", 9);

        board.add("  ", 8);

        int i = 7;
        for (Flag flag : flagManager.getFlags()) {
            if (flag.getTeam() != null) {
                board.add(flag.getTeam().color + flag.getTeam().name + ": " + flag.getStatus(), i);
            }
            i--;
        }

        board.add("   ", 4);

        board.add(C.white + "Kills: " + C.green + super.getMatchKills(proPlayer.player), 3);

        board.add(" ", 2);
        board.add(C.strike + "------------", 1);

        board.send(proPlayer.player);
        board.update();
    }

    public int updateScoreboards() {

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

    public boolean isAllowModify(Location location) {
        if (this.getSpawnZone(location) != null || flagManager.isInsideCapturePoint(location)) {
            return false;
        }
        return true;
    }

    public Team getSpawnZone(Location location) {
        for (Team team : super.teams) {
            if (team.getSpawnLocation().distance(location) <= spawnBlockPlaceRadius) {
                return team;
            }
        }
        return null;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!isAllowModify(event.getBlockPlaced().getLocation())) {
            event.setCancelled(true);
            F.warning(event.getPlayer(), "You cannot modify this region!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isAllowModify(event.getBlock().getLocation())) {
            event.setCancelled(true);
            F.warning(event.getPlayer(), "You cannot modify this region!");
            return;
        }

        if (event.getBlock().getType() != Material.WOOD) {
            event.getBlock().getDrops().clear();
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (event.getItem().getItemStack().getType() != Material.WOOD) {
            event.setCancelled(true);
            event.getItem().remove();
        }
    }

    @EventHandler
    public void onCustomDamage(EntityDamageEvent event) {
        DamageInfo damageInfo = DamageUtils.getDamageInfo(event);

        if (damageInfo.getHurtPlayer() != null) {
            Team team = this.getSpawnZone(damageInfo.getHurtPlayer().getLocation());
            if (team != null) {
                if (team.players.contains(damageInfo.getHurtPlayer())) {
                    event.setCancelled(true);

                    if (damageInfo.getDamagerPlayer() != null && team.onSameTeam(damageInfo.getDamagerPlayer(), damageInfo.getHurtPlayer())) {
                        if (!super.getTeam(damageInfo.getDamagerPlayer()).onSameTeam(damageInfo.getDamagerPlayer(), damageInfo.getHurtPlayer())) {
                            F.warning(damageInfo.getDamagerPlayer(), "You cannot heart players inside their spawn!");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void extraUnload() {
        this.replenishManager.unload();
        GameAPI.getScheduler().cancelTask(this.scoreboardRunnableID);
        this.flagManager.unload();
        
        for(Hologram hologram : holograms){
        	hologram.unload();
        }
    }
}