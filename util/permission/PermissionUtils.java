package common.util.permission;

import main.Main;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

/**
 * Created by luke on 11/13/15.
 */
public class PermissionUtils {
    public static void addPerm(Player player, String perm) {
        PermissionAttachment pa = player.addAttachment(Main.getInstance());
        pa.setPermission(perm, true);
        Main.getInstance().getPerms().put(player.getUniqueId(), pa);
    }
}
