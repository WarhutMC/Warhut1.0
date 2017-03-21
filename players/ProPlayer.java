package cyclegame.players;

import common.punish.Punishment;
import common.util.communication.C;
import common.util.communication.F;
import common.util.communication.S;
import common.util.permission.PermissionUtils;
import common.util.scoreboard.SimpleScoreboard;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import cyclegame.GameAPI;
import common.kit.Kit;
import org.bukkit.scoreboard.NameTagVisibility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by luke on 10/17/15.
 *
 */
public class ProPlayer {
    public Player player;
    private Rank rank;

    public UUID lastDamagedBy = null;
    private Date lastHitDate = new Date(System.currentTimeMillis() - 10000);

    public SimpleScoreboard scoreboard;

    public boolean initialized = false;

    public ArrayList<Punishment> punishments;

    public boolean radio = true;

    public Kit kit; //assigned to default kit on join

    private Object customStats;

    public int coins = 0;
    public int tickets = 0;
    public int xp = 0;

    public PlayerGameStats gameStats = new PlayerGameStats();

    public List<String> ips = new ArrayList<>();
    public String firstJoined;

    public ProPlayer(Player player) {
        this.player = player;
        if (GameAPI.getServerMode() == GameAPI.ServerMode.GAME) {
            this.kit = GameAPI.getMatch().map.kitRepo.defaultKit;
        }

        this.generateNewScoreboard();

        //todo: database calls for coins, tickets, owned items (kits)
    }

    public SimpleScoreboard generateNewScoreboard() {
        this.scoreboard = new SimpleScoreboard(C.yellow + C.bold + "MCWAR.US");
        org.bukkit.scoreboard.Team t = scoreboard.getScoreboard().registerNewTeam("spectators");
        t.setDisplayName("spectators");
        t.setPrefix(C.aqua);
        t.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);

        this.scoreboard.send(player);
        return scoreboard;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void addTickets(int tickets) {
        this.tickets += tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }

    public boolean canPurchase(int price) {
        return this.coins >= price;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    public void setFirstJoined(String firstJoined) {
        this.firstJoined = firstJoined;
    }

    public void setRank(Rank rank) {
        this.rank = rank;

        if (rank.has(null, Rank.MOD, false)) {
            PermissionUtils.addPerm(player, "nocheat.notify");
            PermissionUtils.addPerm(player, "bukkit.command.teleport");
            PermissionUtils.addPerm(player, "minecraft.command.tp");
            PermissionUtils.addPerm(player, "mcnet.mod");
        }

        if (rank.has(null, Rank.ADMIN, false)) {
            PermissionUtils.addPerm(player, "mcnet.admin");
        }
    }

    public String getFormattedName() {
        return this.rank.getTag() + this.player.getName() + " " + C.gray;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public Player getPlayer() {
        return player;
    }

    public Rank getRank() {
        return rank;
    }

    public SimpleScoreboard getScoreboard() {
        return scoreboard;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Kit getKit() {
        return kit;
    }

    public int getCoins() {
        return coins;
    }

    public int getTickets() {
        return tickets;
    }

    public PlayerGameStats getGameStats() {
        return gameStats;
    }

    public List<String> getIps() {
        return ips;
    }

    public String getFirstJoined() {
        return firstJoined;
    }

    public UUID getLastDamagedBy() {
        return lastDamagedBy;
    }

    public void setLastDamagedBy(UUID lastDamagedBy) {
        this.lastDamagedBy = lastDamagedBy;
    }

    public ArrayList<Punishment> getPunishments() {
        return punishments;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public void setPunishments(ArrayList<Punishment> punishments) {
        this.punishments = punishments;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void addXp(int xp) {
        int oldLevel = getLevel();
        this.xp += xp;
        int newLevel = this.getLevel();

        if (newLevel > oldLevel) {
            F.message(player, "Experience", "You are now " + C.yellow + "level " + newLevel);
            S.playSound(player, Sound.LEVEL_UP);
        }
    }

    public void setScoreboard(SimpleScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void setGameStats(PlayerGameStats gameStats) {
        this.gameStats = gameStats;
    }

    public int getLevel() {
        return (this.xp / 100) + 1;
    }

    public Object getCustomStats() {
        return customStats;
    }

    public void setCustomStats(Object customStats) {
        this.customStats = customStats;
    }

    public boolean isRadio() {
        return radio;
    }

    public void setRadio(boolean radio) {
        this.radio = radio;
    }

    public Date getLastHitDate() {
        return lastHitDate;
    }

    public void setLastHitDate(Date lastHitDate) {
        this.lastHitDate = lastHitDate;
    }
}
