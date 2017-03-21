package cyclegame.templates.game;

import common.damage.CustomDeathEvent;
import common.damage.CustomRespawnEvent;
import common.damage.DamageInfo;
import common.damage.DamageUtils;
import common.kit.Kit;
import cyclegame.commands.JoinCommand;
import cyclegame.commands.SpectateCommand;
import cyclegame.players.Rank;
import cyclegame.players.event.AsyncProPlayerJoinEvent;
import common.util.PlayerUtils;
import common.util.communication.C;
import common.util.communication.F;
import common.util.communication.S;
import common.util.fireworks.Fireworks;
import cyclegame.commands.TeamChatCommand;
import cyclegame.GameAPI;
import cyclegame.rotation.GameMap;
import cyclegame.templates.event.PlayerJoinGameEvent;
import cyclegame.templates.game.spectate.SpectateManager;
import main.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import cyclegame.players.ProPlayer;

import java.util.*;

/**
 * Created by luke on 10/16/15.
 */
public abstract class Game implements Listener {
    public GameSettings gameSettings;
    public World world;
    public GameMap map;

    public TeamChatCommand teamChatCommand;
 
    public HashMap<UUID, Integer> killsCounter = new HashMap<>();
    public HashMap<UUID, Integer> deathsCounter = new HashMap<>();
 
    //Countdown
    public static int countdownStartTime = 13;
    public int countdownTime = countdownStartTime;
    public int countdownRunnableID = -1;
 
    public GameState gameState;
    public List<Team> teams = new ArrayList<>();
    public Team spectators;

    private SpectateManager spectateManager;

    //commands
    private JoinCommand joinCommand;
    private SpectateCommand spectateCommand;
 
    public Game(GameMap gameMap) {
        this.world = gameMap.getWorld();
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setGameRuleValue("doMobSpawning", "false");

        this.gameSettings = new GameSettings();
        this.map = gameMap;

        this.spectators = new Team("Spectators", ChatColor.AQUA);
        this.spectateManager = new SpectateManager(this);

        this.teamChatCommand = new TeamChatCommand();

        this.joinCommand = new JoinCommand(this);
        this.spectateCommand = new SpectateCommand(this);
    }

    public abstract void setupTeams();

    public abstract void setupGame();
 
    public int endGameCountdown() {
        return GameAPI.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
 
                if (countdownTime > 0) {
                    if (countdownTime <= 5 || countdownTime % 10 == 0) {
                        Bukkit.broadcastMessage(C.green + "Cycling in " + C.red + countdownTime + " seconds");
                        S.plingAll();
                    }
                } else {
                    GameAPI.getScheduler().cancelTask(countdownRunnableID);
                    GameAPI.getRotation().cycle();
                }
 
                /* ========= */
                countdownTime -= 1;
            }
        }, 20L, 20L);
    }
 
    public void startGame() {
        this.gameState = GameState.STARTING;
        this.enableKits();

        this.gameState = GameState.PLAYING;

        Bukkit.broadcastMessage(C.daqua + "##########################");
        Bukkit.broadcastMessage(C.daqua + "#");
        Bukkit.broadcastMessage(C.daqua + "#   " + C.green + "The Match has started!");
        Bukkit.broadcastMessage(C.daqua + "#");
        Bukkit.broadcastMessage(C.daqua + "##########################");
    }

    public void joinGame(ProPlayer proPlayer) {
        proPlayer.setLastDamagedBy(null);
        assignToTeam(proPlayer);
        prepare(proPlayer);
        Main.getInstance().getServer().getPluginManager().callEvent(new PlayerJoinGameEvent(proPlayer));
    }
 
    public void enableKits() {
        this.map.kitRepo.defaultKit.enable();
 
        for (Kit kit : this.map.kitRepo.kits) {
            kit.enable();
        }
    }
 
    public void prepare(ProPlayer proPlayer) {
        Player player = proPlayer.player;
        PlayerUtils.resetPlayer(player);
        player.setGameMode(GameMode.SURVIVAL);
 
        Team team = this.getTeam(player);
 
        player.teleport(this.getTeam(player).getSpawnLocation());
        proPlayer.kit.apply(player, team.color);

        if (this.gameState == GameState.ENDED) {
            player.sendMessage(C.divider);
            player.sendMessage("");
            player.sendMessage(C.white + C.bold + "THE GAME HAS ALREADY ENDED, " + C.gold + C.bold + "A NEW GAME IS STARTING SOON!");
            player.sendMessage("");
            player.sendMessage(C.divider);
            this.prepareEndGame(proPlayer);
        } else {
            if (this.gameSettings.setupMessage != "") {
                player.sendMessage(C.divider);
                player.sendMessage("");
                player.sendMessage(C.tab + C.gray + "Playing " + C.yellow + this.map.name + C.gray + " by " + C.yellow + C.purple + map.builder);
                player.sendMessage("");
                player.sendMessage(C.tab + C.gray + this.gameSettings.setupMessage);
                player.sendMessage("");
                player.sendMessage(C.divider);
            }
        }
    }
 
    public void assignToTeam(ProPlayer proPlayer) {
        this.spectateManager.removeSpectator(proPlayer.player);
        this.getSmallestTeam().add(proPlayer);
    }
 
 
    /*
    * ************************************************
    *
    * Team Related
    *
    * ************************************************
    */
 
    @EventHandler
    public void onGameAsyncJoin(AsyncProPlayerJoinEvent event) {
        if (event.isNewPlayer) {
            Bukkit.getServer().broadcastMessage(C.yellow + "[+] " + C.white + event.proPlayer.getFormattedName() + C.purple + " (new)");
        } else {
            Bukkit.getServer().broadcastMessage(C.yellow + "[+] " + C.white + event.proPlayer.getFormattedName());
        }
    }
 
    @EventHandler(priority = EventPriority.HIGH)
    public void onGameJoin(PlayerJoinEvent event) {
        ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(event.getPlayer());
//        this.assignToTeam(proPlayer);
//        this.prepare(proPlayer);

        spectateManager.addSpectator(event.getPlayer());
    }
 
    @EventHandler
    public void onGameCustomRespawn(CustomRespawnEvent event) {
        PlayerUtils.resetPlayer(event.getPlayer());
 
        Team team = this.getTeam(event.getPlayer());
        event.setSpawn(team.getSpawnLocation());
 
        ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(event.getPlayer());
        proPlayer.kit.apply(proPlayer.player, team.color);
 
        proPlayer.player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 8));
        S.playSound(event.getPlayer().getLocation(), Sound.IRONGOLEM_HIT);
    }
 
    @EventHandler
    public void onGameCustomDeath(CustomDeathEvent event) {
        if (event.getDeadPlayer() != null) {
            final ProPlayer deadPlayer = GameAPI.getPlayerHandler().getProPlayer(event.getDeadPlayer());
            Team deadTeam = this.getTeam(deadPlayer.player);

            GameAPI.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    GameAPI.getPlayerHandler().incrementValue(GameAPI.getMatch().getDbSyncer().getDbCollection(), deadPlayer, "deaths", 1);
                }
            });
            deadPlayer.gameStats.deaths += 1;
 
            if (this.deathsCounter.containsKey(deadPlayer.player.getUniqueId())) {
                this.deathsCounter.put(deadPlayer.player.getUniqueId(), this.deathsCounter.get(deadPlayer.player.getUniqueId()) + 1);
            } else {
                this.deathsCounter.put(deadPlayer.player.getUniqueId(), 1);
            }
 
            String msg;
 
            if (event.getKillerPlayer() != null) {
                final ProPlayer killerPlayer = GameAPI.getPlayerHandler().getProPlayer(event.getKillerPlayer());
                Team killerTeam = this.getTeam(killerPlayer.player);

                GameAPI.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        //Coins
                        int coinsAdded = (int) (gameSettings.coinsPerKill * killerPlayer.getRank().getCoinScale());
                        if (coinsAdded != 0) {
                            GameAPI.getPlayerHandler().incrementValue(GameAPI.getPlayerHandler().playersCollection, killerPlayer, "coins", coinsAdded);
                            killerPlayer.addCoins(coinsAdded);
                            F.addCoinsMessage(killerPlayer, coinsAdded, "Killed " + deadPlayer.player.getName());
                        }

                        if (gameSettings.xpPerKill != 0) {
                            GameAPI.getPlayerHandler().incrementValue(GameAPI.getPlayerHandler().playersCollection, killerPlayer, "xp", gameSettings.xpPerKill);
                        }

                        //Kills
                        GameAPI.getPlayerHandler().incrementValue(GameAPI.getMatch().getDbSyncer().getDbCollection(), killerPlayer, "kills", 1);
                    }
                });
                killerPlayer.gameStats.kills += 1;
 
                if (this.killsCounter.containsKey(killerPlayer.player.getUniqueId())) {
                    this.killsCounter.put(killerPlayer.player.getUniqueId(), this.killsCounter.get(killerPlayer.player.getUniqueId()) + 1);
                } else {
                    this.killsCounter.put(killerPlayer.player.getUniqueId(), 1);
                }
 
                msg = deadTeam.color + deadPlayer.player.getName() + C.gray + " was killed by " + killerTeam.color + killerPlayer.player.getName();
                killerPlayer.player.sendMessage(msg);
//                F.message(killerPlayer.player, C.red + "Death", msg);
            } else {
                msg = deadTeam.color + deadPlayer.player.getName() + C.gray + " was killed by " + C.green + "wilderness";
            }

            deadPlayer.player.sendMessage(msg);
//            F.message(deadPlayer.player, C.red + "Death", msg);
        }
    }

    @EventHandler
    public void onGameDamageEvent(EntityDamageEvent event) {
        DamageInfo damageInfo = DamageUtils.getDamageInfo(event);

        if (!this.gameSettings.damage) {
            event.setCancelled(true);
            return;
        }

        if (damageInfo.getDamagerPlayer() != null && damageInfo.getHurtPlayer() != null) {
            if (SpectateManager.isSpectator(damageInfo.getDamagerPlayer()) || SpectateManager.isSpectator(damageInfo.getHurtPlayer())) {
                return;
            }

            if (this.getTeam(damageInfo.getDamagerPlayer()).onSameTeam(damageInfo.getDamagerPlayer(), damageInfo.getHurtPlayer())) {
                event.setCancelled(true);
            } else {
                damageInfo.getHurtEntity().getWorld().playSound(damageInfo.getHurtEntity().getLocation(), Sound.BLAZE_HIT, .5f, 1f);
            }
        }
    }
 
    @EventHandler
    public void onGameChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;
 
        ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(event.getPlayer());

        if (proPlayer.getRank().has(null, Rank.MOD, false)) {
            event.setFormat(
                    "<" + proPlayer.getRank().getTag() + this.getTeam(event.getPlayer()).color + proPlayer.player.getName() + C.white + "> %2$s"
            );
        } else {
            event.setFormat(
                    "<" + proPlayer.getRank().getTag() + this.getTeam(event.getPlayer()).color + proPlayer.player.getName() + C.white + "> %2$s"
            );
        }
    }
 
    public Team getSmallestTeam() {
        Team smallest = this.teams.get(0);
 
        for (Team team : this.teams) {
            if (team.players.size() < smallest.players.size()) {
                smallest = team;
            }
        }
 
        return smallest;
    }
 
    public Team addTeam(Team team) {
        this.teams.add(team);
        return team;
    }
 
    public Team getTeam(Player player) {
        for (Team team : this.teams) {
            if (team.players.contains(player)) {
                return team;
            }
        }
        if (spectators.players.contains(player)) {
            return spectators;
        }
        return null;
    }
 
    /*w
    * ************************************************
    *
    * GAME Settings Events
    *
    * ************************************************
    */

    @EventHandler
    public void onGameHungerChange(FoodLevelChangeEvent event) {
        if (!gameSettings.hunger) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameNaturalMobSpawning(CreatureSpawnEvent event) {
        if (!gameSettings.naturalMobSpawning) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameMoveInventory(InventoryMoveItemEvent event) {
        if (!gameSettings.moveInventory) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameMoveInventory(InventoryClickEvent event) {
        if (!gameSettings.moveInventory) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameMoveInventory(PlayerDropItemEvent event) {
        if (!gameSettings.moveInventory) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameMoveInventory(InventoryDragEvent event) {
        if (!gameSettings.moveInventory) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameRain(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameBlockPlace(BlockPlaceEvent event) {
        if (!gameSettings.build) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameBlockBreak(BlockBreakEvent event) {
        if (!gameSettings.build) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameHealthRegen(EntityRegainHealthEvent event) {
        if (!gameSettings.healthRegen) {
            event.setCancelled(true);
        }
    }
 
 
    /*
    * ************************************************
    *
    * Misc
    *
    * ************************************************
    */
 
    public int getMatchKills(Player player) {
        if (this.killsCounter.containsKey(player.getUniqueId())) {
            return this.killsCounter.get(player.getUniqueId());
        } else {
            return 0;
        }
    }
 
    public int getMatchDeaths(Player player) {
        if (this.deathsCounter.containsKey(player.getUniqueId())) {
            return this.deathsCounter.get(player.getUniqueId());
        } else {
            return 0;
        }
    }
 
    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameQuitEvent(PlayerQuitEvent event) {
        event.setQuitMessage("");
        F.log("players online: " + Bukkit.getServer().getOnlinePlayers().size());
        if (Bukkit.getServer().getOnlinePlayers().size() <= 1) {
            GameAPI.getRotation().cycle();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameKickEvent(PlayerKickEvent event) {
        event.setLeaveMessage("");
        if (Bukkit.getServer().getOnlinePlayers().size() <= 1) {
            GameAPI.getRotation().cycle();
        }
    }
 
    @EventHandler
    public void onGameLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onGameBlockTeamLastHit(EntityDamageByEntityEvent event) {
        if(event.isCancelled()) return;

        DamageInfo damageInfo = DamageUtils.getDamageInfo(event);
        if (damageInfo.getHurtPlayer() != null && damageInfo.getDamagerPlayer() != null) {
            if (SpectateManager.isSpectator(damageInfo.getDamagerPlayer()) || SpectateManager.isSpectator(damageInfo.getHurtPlayer())) {
                return;
            }

            if (!getTeam(damageInfo.getHurtPlayer()).onSameTeam(damageInfo.getHurtPlayer(), damageInfo.getDamagerPlayer())) {
                ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer(damageInfo.getHurtPlayer());
                proPlayer.setLastDamagedBy(damageInfo.getDamagerPlayer().getUniqueId());
            }
        }
    }

    public void prepareEndGame(ProPlayer proPlayer) {
        proPlayer.player.setAllowFlight(true);
        proPlayer.player.setFlying(true);
    }
 
    public void endGame(final Team winners) {
        this.gameState = GameState.ENDED;
        this.gameSettings.damage = false;

        Bukkit.broadcastMessage(C.daqua + "##########################");
        Bukkit.broadcastMessage(C.daqua + "#");
        Bukkit.broadcastMessage(C.daqua + "#   " + winners.getColor() + C.bold + winners.getName() + C.white + " won the game!");
        Bukkit.broadcastMessage(C.daqua + "#");
        Bukkit.broadcastMessage(C.daqua + "##########################");
 
        for (ProPlayer proPlayer : GameAPI.getPlayerHandler().proPlayers) {
            Fireworks.spawnRandomFirework(proPlayer.player.getLocation());
            prepareEndGame(proPlayer);
        }

        //Database related rewards
        GameAPI.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                for(ProPlayer proPlayer : GameAPI.getPlayerHandler().proPlayers) {
                    if (winners.players.contains(proPlayer.player)) {
                        int coinsAdded = (int) (gameSettings.coinsPerWin * proPlayer.getRank().getCoinScale());
                        GameAPI.getPlayerHandler().incrementValue(GameAPI.getPlayerHandler().playersCollection, proPlayer, "coins", coinsAdded);
                        proPlayer.addCoins(coinsAdded);
                        F.addCoinsMessage(proPlayer, coinsAdded, "Win a game");
                        GameAPI.getPlayerHandler().incrementValue(GameAPI.getPlayerHandler().playersCollection, proPlayer, "xp", gameSettings.xpPerWin);

                        //tickets
                        int tickets = 1 * proPlayer.getRank().getTicketScale();
                        if (tickets > 0) {
                            GameAPI.getPlayerHandler().incrementValue(GameAPI.getPlayerHandler().playersCollection, proPlayer, "tickets", tickets);
                            proPlayer.addTickets(tickets);
                            F.message(proPlayer.player, C.aqua + " +" + tickets + " tickets " + C.gray + "for" + C.green + " winning " + C.gray + "the game.");
                        }


                    } else {
                        int coinsAdded = (int) (gameSettings.coinsPerLoss * proPlayer.getRank().getCoinScale());
                        GameAPI.getPlayerHandler().incrementValue(GameAPI.getPlayerHandler().playersCollection, proPlayer, "coins", coinsAdded);
                        proPlayer.addCoins(coinsAdded);
                        F.addCoinsMessage(proPlayer, coinsAdded, "Finish a game");
                        GameAPI.getPlayerHandler().incrementValue(GameAPI.getPlayerHandler().playersCollection, proPlayer, "xp", gameSettings.xpPerLoss);
                    }
                }
            }
        });
 
        this.countdownRunnableID = this.endGameCountdown();
    }

 
    public void broadcastDeath(ProPlayer killer, ProPlayer dead) {
        if (killer != null) {
 
        } else {
 
        }
    }
 
    public enum GameState {
        STARTING,
        PLAYING,
        ENDED;
    }

    public class GameSettings {
        public boolean hunger = false;
        public boolean teamDamage = false;
        public boolean fallDamage = false;
        public boolean naturalMobSpawning = false;
        public boolean damage = true;
        public boolean moveInventory = false;
        public boolean deathMessages = true;
        public boolean build = true;
        public boolean healthRegen = true;

        public Sound damageSound = Sound.BLAZE_HIT;

        public double respawnTime = 0;
        public int coinsPerKill = 1;
        public int coinsPerWin = 10;
        public int coinsPerLoss = 5;
        public int xpPerKill = 3;
        public int xpPerWin = 10;
        public int xpPerLoss = 5;

        public String gameName = "Prohost";
        public String setupMessage = "";
    }

    public SpectateManager getSpectateManager() {
        return spectateManager;
    }

        /*
    * ************************************************
    *
    * Unload
    *
    * ************************************************
    */

    public void unload() {
        Main.unloadListener(this);
        Main.unloadListener(this.joinCommand);
        Main.unloadListener(this.spectateCommand);

        if (this.countdownRunnableID != -1) {
            GameAPI.getScheduler().cancelTask(this.countdownRunnableID);
        }

        for (Team team : this.teams) {
            team.unload();
        }

        this.spectateManager.unload();

        Main.unloadListener(this.teamChatCommand);

        this.extraUnload();
    }

    public abstract void extraUnload();
}