package common.punish;

import java.util.Date;

/**
 * Created by luke on 10/24/15.
 */
public class Punishment {
    public String uuid;
    public String ip;
    public PunishType punishType;
    public String reason;
    public String staff;
    public Date issued;
    public Date expires;
    public int length; //minutes
    public boolean reverted;

    public enum PunishType {
        IPBAN("IP-Banned", "Un-IP-Banned"),
        BAN("Banned", "Un-Banned"),
        MUTE("Muted", "Un-Muted"),
        KICK("Kicked", "");

        public String display;
        public String unDisplay;

        PunishType(String display, String unDisplay) {
            this.display = display;
            this.unDisplay = unDisplay;
        }
    }

    public Punishment(String uuid, String ip, PunishType punishType, String reason, String staff, Date issued, Date expires, int length, boolean reverted) {
        this.uuid = uuid;
        this.ip = ip;
        this.punishType = punishType;
        this.reason = reason;
        this.staff = staff;
        this.issued = issued;
        this.expires = expires;
        this.length = length;
        this.reverted = reverted;
    }
}
