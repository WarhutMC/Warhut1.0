package cyclegame.players;

import common.util.communication.C;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by luke on 10/18/15.
 */
public enum Rank {
    ADMIN("ADMIN", ChatColor.YELLOW, 4, 0),
    MOD("MOD", ChatColor.DARK_GREEN, 4, 0),
    YOUTUBE("YOUTUBE", ChatColor.LIGHT_PURPLE, 4, 4),
    TWITCH("TWITCH", ChatColor.LIGHT_PURPLE, 4, 4),
    HEART("❤", ChatColor.RED, 4, 4),
    PRO3("$$$", ChatColor.GOLD, 4, 4),
    PRO2("$$", ChatColor.GREEN, 3, 3),
    PRO("$",ChatColor.AQUA, 2, 2),
    regular("", ChatColor.WHITE, 1, 1);

    public String name;
    private ChatColor tagColor;
    public double coinScale;
    public int ticketScale;

    private Rank(String name, ChatColor tagColor, double coinScale, int ticketScale) {
        this.name = name;
        this.tagColor = tagColor;
        this.coinScale = coinScale;
        this.ticketScale = ticketScale;
    }

    public boolean has(Player player, Rank rank, boolean inform) {
        if (compareTo(rank) <= 0)
            return true;

        if (inform)
            player.sendMessage(C.blue + "Permissions > " + C.gray + "This requires Permission Rank " + rank.getTag());

        return false;
    }

    public String getChatColor() {
        return C.white;
    }

    public String getTag() {
        if(!this.name.equalsIgnoreCase("")) {
            return this.tagColor + this.name.toUpperCase() + this.tagColor + " ";
        }

        return C.white + "";
    }

    public ChatColor getTagColor() {
        return tagColor;
    }

    public static Rank getRank(String s) {
        for (Rank rank : values()) {
            if (rank.name.equalsIgnoreCase(s)) {
                return rank;
            }
        }
        return Rank.regular;
    }

    public double getCoinScale() {
        return coinScale;
    }

    public int getTicketScale() {
        return ticketScale;
    }
}
