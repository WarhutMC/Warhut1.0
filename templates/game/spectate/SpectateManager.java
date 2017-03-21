package cyclegame.templates.game.spectate;

import common.Manager;
import common.damage.DamageInfo;
import common.damage.DamageUtils;
import common.util.PlayerUtils;
import common.util.communication.C;
import common.util.communication.S;
import common.util.itemstack.ItemFactory;
import common.util.itemstack.ItemUtils;
import common.util.scoreboard.SimpleScoreboard;
import cyclegame.GameAPI;
import cyclegame.players.ProPlayer;
import cyclegame.templates.event.GameStartEvent;
import cyclegame.templates.game.Game;
import cyclegame.templates.game.Team;
import main.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by luke on 12/13/15.
 */
public class SpectateManager implements Manager, Listener {
    private Game game;
    private Team spectators;
    private Location spawn;

    public static ItemStack joinItemStack;
    public static ItemStack kitMenuItemStack;

    public int updateScoreboardsRunnableId;

    public SpectateManager(Game game) {
        this.game = game;
        this.spectators = game.spectators;

        if (game.map.getLocations().containsKey("spectate-spawn")) {
            this.spawn = game.map.getLocation("spectate-spawn");
        } else {
            //so current maps don't break
            this.spawn = game.map.getLocation("blue-spawn");
        }

        joinItemStack = ItemFactory.createItem(Material.NAME_TAG, C.green + "/join");
        kitMenuItemStack = ItemFactory.createItem(Material.CHEST, C.green + "/kit");

        this.updateScoreboardsRunnableId = updateScoreboards();

        Main.registerListener(this);
    }

    public int updateScoreboards() {
        return Main.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (ProPlayer proPlayer : GameAPI.getPlayerHandler().getProPlayers()) {
                    SimpleScoreboard board = proPlayer.getScoreboard();

                    org.bukkit.scoreboard.Team spectateScoreboardTeam = board.getScoreboard().getTeam("spectators");
                    for (Player player : spectators.players) {
                        if (!spectateScoreboardTeam.getPlayers().contains(player)) {
                            spectateScoreboardTeam.addPlayer(player);
                        }
                    }
                }
            }
        }, 0L, 20L);
    }

    public void addSpectator(Player player) {
        if (spectators.players.contains(player)) {
            return;
        }

        Team existingTeam = game.getTeam(player);
        if (existingTeam != null) {
            game.getTeam(player).players.remove(player);
        }

        this.spectators.add(GameAPI.getPlayerHandler().getProPlayer(player));

        PlayerUtils.resetPlayer(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);

        player.teleport(this.spawn);
        player.getInventory().setItem(1, joinItemStack);
        player.getInventory().setItem(3, kitMenuItemStack);

        S.playSound(player, Sound.ENDERMAN_TELEPORT);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 30, 10));
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 30, 4));
        player.sendMessage(C.yellow + "You are now a " + C.aqua + "spectator");



        GameAPI.getInstance().getGhostManager().addGhost(player);
    }

    public void removeSpectator(Player player) {
        if (this.spectators.players.contains(player)) {
            this.spectators.remove(player);
            GameAPI.getInstance().getGhostManager().removeGhost(player);
        }
    }

    public static boolean isSpectator(Player player) {
        return GameAPI.getMatch().getGame().spectators.players.contains(player);
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {

            //open kit menu
            if (ItemUtils.compareItemStacks(event.getItem(), this.kitMenuItemStack)) {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerCommandPreprocessEvent(event.getPlayer(), "/kit"));
                event.setCancelled(true);
                return;
            }

            //join game
            if (ItemUtils.compareItemStacks(event.getItem(), this.joinItemStack)) {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerCommandPreprocessEvent(event.getPlayer(), "/join"));
                event.setCancelled(true);
                return;
            }
        }

        //spectators blocking block placing
//        if(event.getClickedBlock() != null) {
//            Location blockloc = event.getClickedBlock().getLocation();
//            for (Player p : blockloc.getWorld().getPlayers()) {
//                if (!isSpectator(p))
//                    continue;
//                if (p.getLocation().distanceSquared(blockloc) <= 2.0) {
//                    p.teleport(p.getLocation().add(0, 5, 0));
//                    event.setCancelled(true);
//                    F.warning(p, "Don't get in the way of players!");
//                }
//
//
//            }
//        }
    }

//    @EventHandler
//    public void onPlayerInteractAtPlayer(PlayerInteractEntityEvent event) {
//        if (event.getRightClicked() instanceof Player) {
//            Player clicked = (Player) event.getRightClicked();
//
//            if (!isSpectator(clicked)) return;
//
//            clicked.teleport(clicked.getLocation().add(0, 5, 0));
//            event.setCancelled(true);
//            F.warning(clicked, "Don't get in the way of players!");
//        }
//    }

    @EventHandler
    public void onBlockBlock(BlockCanBuildEvent event) {
        Block b = event.getBlock();
        Location blockloc = b.getLocation();
        for (Player p : b.getWorld().getPlayers()) {
            if (!isSpectator(p))
                continue;
            if (p.getLocation().distanceSquared(blockloc) <= 2.0)
                event.setBuildable(true);
        }
    }

//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onArrowHit(EntityDamageByEntityEvent event) {
//        DamageInfo damageInfo = DamageUtils.getDamageInfo(event);
//        if (damageInfo.getProjectile() != null && damageInfo.getHurtPlayer() != null) {
//            F.debug("Detected arrow hit");
//            if (SpectateManager.isSpectator(damageInfo.getHurtPlayer())) {
//                F.debug("Arrow target was a spectator, cancelling...");
//                event.setCancelled(true);
//                return;
//            }
//
//            F.debug("Arrow target is player and NOT spectator");
//        }
//    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerArrowBlock(EntityDamageByEntityEvent e) {
        Entity entityDamager = e.getDamager();
        Entity entityDamaged = e.getEntity();
        if (entityDamager instanceof Arrow) {
            Arrow arrow = (Arrow) entityDamager;
            if (entityDamaged instanceof Player
                    && arrow.getShooter() instanceof Player) {
                Player damaged = (Player) entityDamaged;
                if (isSpectator(damaged)) {
                    final Location original = damaged.getLocation();

                    Arrow nextArrow = damaged.launchProjectile(Arrow.class);
                    nextArrow.teleport(arrow.getLocation());

                    damaged.setAllowFlight(true);
                    damaged.setFlying(true);
                    damaged.teleport(damaged.getLocation().add(0, 5, 0));

                    nextArrow.setVelocity(arrow.getVelocity());
                    nextArrow.setBounce(false);
                    nextArrow.setShooter(arrow.getShooter());
                    nextArrow.setFireTicks(arrow.getFireTicks());
                    nextArrow.setCritical(arrow.isCritical());
                    nextArrow.setCustomName(arrow.getCustomName());
                    nextArrow.setKnockbackStrength(arrow
                            .getKnockbackStrength());

                    arrow.remove();
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        DamageInfo damageInfo = DamageUtils.getDamageInfo(event);

        if (damageInfo.getHurtPlayer() != null && SpectateManager.isSpectator(damageInfo.getHurtPlayer())) {
            event.setCancelled(true);
            return;
        }

        if (damageInfo.getDamagerPlayer() != null && SpectateManager.isSpectator(damageInfo.getDamagerPlayer())) {
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void unload() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            removeSpectator(player);
        }
        Main.unloadListener(this);
        Main.getScheduler().cancelTask(this.updateScoreboardsRunnableId);
    }
}
