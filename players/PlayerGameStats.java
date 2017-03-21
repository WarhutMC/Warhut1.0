package cyclegame.players;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luke on 10/19/15.
 */
public class PlayerGameStats {
    public List<String> kits = new ArrayList<>();
    public int kills;
    public int deaths;
    public int matches;

    public PlayerGameStats() {
    }

    public void clear() {
        this.kits = new ArrayList<>();
        this.kills = 0;
        this.deaths = 0;
        this.matches = 0;
    }
}
