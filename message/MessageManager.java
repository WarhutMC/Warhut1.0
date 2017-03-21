package common.message;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by luke on 10/24/15.
 */
public class MessageManager {
    public HashMap<String, String> lastMessaged = new HashMap<>();

    public MessageManager() {
        new MessageCommand(this);
        new ReplyCommand(this);
    }

    public static String joinMessage(ArrayList<String> args) {
        String msg = "";

        for (int i = 1; i < args.size(); i++) {
            msg += args.get(i) + " ";
        }

        return msg;
    }
}
