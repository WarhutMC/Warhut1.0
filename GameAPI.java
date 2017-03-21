package cyclegame;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.gson.JsonObject;
import com.mongodb.*;
import common.broadcast.BroadcastManager;
import common.cheat.CheatManager;
import common.command.commands.*;
import common.damage.DamageHandler;
import common.ghost.GhostManager;
import common.message.MeCommand;
import common.message.MessageManager;
import common.noteblock.api.NoteBlockPlayerMain;
import common.punish.PunishManager;
import common.update.UpdateManager;
import common.util.communication.C;
import common.util.communication.F;
import common.util.json.JsonUtil;
import common.vanish.EntityHider;
import cyclegame.rotation.Match;
import cyclegame.rotation.Rotation;
import cyclegame.templates.game.spectate.SpectateManager;
import fr.mrsheepsheep.tinthealth.THAPI;
import fr.mrsheepsheep.tinthealth.TintHealth;
import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitScheduler;
import cyclegame.commands.*;
import cyclegame.players.PlayerHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by luke on 10/16/15.
 *
 */
public class GameAPI implements Listener {
    public int matchCount = 0;
    private static GameAPI instance;
    private static PlayerHandler playerHandler;
    private static CheatManager cheatManager;

    private static ServerMode serverMode;
    private static Rotation rotation;

    private DamageHandler damageHandler;
    private PunishManager punishManager;
    private UpdateManager updateManager;
    private EntityHider entityHider;
    private GhostManager ghostManager;

    private String motd;
    
    private ArrayList<Player> possibleAfks = new ArrayList<>();
    
    private THAPI tintHealth;

    /* Perms */
    public final HashMap<UUID, PermissionAttachment> perms = new HashMap<UUID, PermissionAttachment>();

    /* Database */
    public MongoClient mongo;
    public DB db;

    public void onEnable() {
        instance = this;

        this.setupDatabase();

        NoteBlockPlayerMain noteBlockPlayerMain = new NoteBlockPlayerMain();
        noteBlockPlayerMain.onEnable();

        //global managers
        playerHandler = new PlayerHandler();
        damageHandler = new DamageHandler();
        cheatManager = new CheatManager();
        punishManager = new PunishManager(this.db, playerHandler.getPlayersCollection());
        entityHider = new EntityHider(Main.getInstance(), EntityHider.Policy.BLACKLIST);
        ghostManager = new GhostManager(Main.getInstance());
        new MessageManager();

        //global commands
        new GameCommand();
        new AdminCommand();
        new CoinsCommand();
        new TicketsCommand();
        new OnlineCommand();
        new VanillaAdminCommands();
        new MeCommand();
        new DonateCommand();
        new BuildCommand();
        new PluginCommand();
        new RemoveNearCommand();
        new FunCommand();
        new LoginCommand();
        new KitCommand();
        new MotdCommand();
        new GMuteCommand();
        new TeleportCommand();
//        new TeleportCommand();

        //global packet editing
        this.disableSound();

//        JsonObject json = JsonUtil.convertFileToJSON("settings.json");
        serverMode = ServerMode.GAME;

//        if (json != null) {
//            if (json.has("mode")) {
//                if (json.get("mode").getAsString().equalsIgnoreCase("hub")) {
//                    serverMode = ServerMode.HUB;
//                }
//            }
//        }

        this.loadMotd();


        if (serverMode == ServerMode.GAME) {
            new BroadcastManager();
            rotation = new Rotation();
            if(rotation.activeMaps.size() > 0) {
                rotation.cycle();
            }
        }

        TintHealth th = (TintHealth) Main.getInstance().getServer().getPluginManager().getPlugin("TintHealth");
	    this.tintHealth = th.getAPI();

        updateManager = new UpdateManager();
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
        
//        initiateAfkRemover();
    }

    public void loadMotd() {
        JsonObject settingsJson = JsonUtil.convertFileToJSON("settings.json");
        if (settingsJson.get("motd") != null) {
            this.motd = settingsJson.get("motd").getAsString();
            this.motd = ChatColor.translateAlternateColorCodes('&', this.motd);
        } else {
            this.motd = C.red + " Dank";
        }
    }

    public void onDisable() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.kickPlayer("Server Restarting");
        }
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        event.setMotd(this.motd);
        event.setMaxPlayers(0);
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent event){
    	if(this.possibleAfks.contains(event.getPlayer())){
    		this.possibleAfks.remove(event.getPlayer());
    	}
    }

    public void setupDatabase() {
        this.mongo = new MongoClient(new MongoClientURI("mongodb://root:removed-password@mcwar.us/mc"));

        this.db = mongo.getDB("mc");
        this.db.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
    }

    public static GameAPI getInstance() {
        return instance;
    }

    public static Main getMain() {
        return Main.getInstance();
    }

    public static Rotation getRotation() {
        return rotation;
    }

    public static Match getMatch() {
        return GameAPI.getRotation().match;
    }

    public static PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    public static CheatManager getCheatManager() {
        return cheatManager;
    }

    public static BukkitScheduler getScheduler() {
        return Bukkit.getServer().getScheduler();
    }
    
    public void initiateAfkRemover(){
    	Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {

            @Override
            public void run() {
                if (getServerMode().equals(ServerMode.GAME)) {
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        if (possibleAfks.contains(player) && !SpectateManager.isSpectator(player)) {
                            F.warning(player, "You left the match for being AFK.");
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerCommandPreprocessEvent(player, "/leave"));

                            possibleAfks.remove(player);
                        } else {
                            possibleAfks.add(player);
                        }
                    }
                }
            }

        }, 90 * 20, 90 * 20);
    }

    public void disableSound() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
            new PacketAdapter(Main.getInstance(), PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    // You can also get the location of the sound effect -
                    // see PacketWrapper's WrapperPlayServerNamedSoundEffect
                    String soundName = event.getPacket().getStrings().read(0);


                    if (soundName.contains("mob.zombie")
                                    || soundName.contains("mob.cow.hurt")
                                    || soundName.contains("mob.sheep.say")
                                    || soundName.contains("mob.pig.say")
                                    || soundName.contains("mob.skeleton")
                    ) {
                        event.setCancelled(true);
                    }
                }
            }
        );
    }

    public int getMatchCount() {
        return matchCount;
    }

    public MongoClient getMongo() {
        return mongo;
    }

    public DB getDb() {
        return db;
    }

    public PunishManager getPunishManager() {
        return punishManager;
    }


    public static ServerMode getServerMode() {
        return serverMode;
    }

    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    public enum ServerMode {
        GAME,
        HUB;
    }

    public THAPI getTintHealth() {
        return tintHealth;
    }

    public HashMap<UUID, PermissionAttachment> getPerms() {
        return perms;
    }

    public GhostManager getGhostManager() {
        return ghostManager;
    }

    public ArrayList<Player> getPossibleAfks() {
        return possibleAfks;
    }

    public DamageHandler getDamageHandler() {
        return damageHandler;
    }

    public EntityHider getEntityHider() {
        return entityHider;
    }

    public String getMotd() {
        return motd;
    }
}
