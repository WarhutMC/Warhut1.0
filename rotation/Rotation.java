package cyclegame.rotation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import common.util.communication.F;
import common.util.json.JsonUtil;
import cyclegame.commands.MapsCommand;
import cyclegame.commands.StatsCommand;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import cyclegame.GameAPI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by luke on 10/17/15.
 */
public class Rotation {
    public JsonObject rotationFile;

    public ArrayList<GameMap> activeMaps = new ArrayList<>();
    public int mapIndex = -1;

    public int nextMatchIndex = -1;

    public Match match;
//    public int matchCount = 1; //used for world folders

    public Rotation() {
        this.loadRotation();

        //game related commands
        new StatsCommand();
        new MapsCommand();
    }

    public void loadRotation() {
        this.rotationFile = JsonUtil.convertFileToJSON("rotation.json");
        this.activeMaps.clear();

        Gson gson = new Gson();
        MapJsonInfo[] arr = gson.fromJson(this.rotationFile.get("maps"), MapJsonInfo[].class);

        for (MapJsonInfo info : arr) {
            info.name.replace(" ", "-");
            info.name = info.name.toLowerCase();
            F.log("Found Map: " + info.name);

            File mapFolder = new File("/home/mc/maps/" + info.name);
            if(mapFolder.exists()) {
                JsonObject mapJson = JsonUtil.convertFileToJSON(mapFolder + "/map.json");

                this.activeMaps.add(new GameMap(mapFolder, mapJson));
            } else {
                F.log("Couldn't find map " + info.name + " that was specified in rotation.json");
            }
        }
    }

    public void cycle() {
        int previousCount = GameAPI.getInstance().matchCount;
        int previousIndex = mapIndex;

        GameAPI.getInstance().matchCount++;

        if (nextMatchIndex != -1) {
            mapIndex = nextMatchIndex;
            nextMatchIndex = -1;
        } else {
            mapIndex++;
        }

        if(mapIndex > activeMaps.size() - 1) {
            mapIndex = 0;
        }

        GameMap map = activeMaps.get(mapIndex);

        if(this.match != null) {
            this.match.unload();
        }

        this.match = new Match(map);
        this.match.startPreGame();

        if(mapIndex > 0) { //There was a previous game
            Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(Integer.toString(previousCount)), false);
            try {
                FileUtils.deleteDirectory(new File("matches/" + Integer.toString(previousCount)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class MapJsonInfo {
        String name;
    }

}
