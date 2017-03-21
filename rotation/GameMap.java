package cyclegame.rotation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import common.util.Comp;
import common.util.communication.F;
import common.util.LocationUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import cyclegame.GameAPI;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by luke on 10/16/15.
 */
public class GameMap {
    public String name;
    public String game;
    public String builder = "Prohost";
    public World world = null;

    public HashMap<String, Location> locations = new HashMap<>();
    public HashMap<String, Integer> variables = new HashMap<>();

    public KitRepo kitRepo;

    public File folder;
    private JsonObject jsonObject;

    public GameMap (File folder, JsonObject jsonObject) {
        this.folder = folder;
        this.jsonObject = jsonObject;

        this.game = jsonObject.get("game").getAsString();
        this.name = jsonObject.get("name").getAsString();

        if(jsonObject.has("builder")) {
            this.builder = jsonObject.get("builder").getAsString();
        }
    }

    public void setKitRepo(KitRepo kitRepo) {
        Collections.sort(kitRepo.kits, new Comp());
        this.kitRepo = kitRepo;
    }

    public void buildLocations(World world) {
        JsonArray array = this.jsonObject.getAsJsonArray("locations");

        Iterator<JsonElement> it = array.iterator();
        while (it.hasNext()) {
            JsonObject json = it.next().getAsJsonObject();

            String locationName = json.get("name").getAsString();

            String coordsString = json.get("coords").getAsString();
            Location location = LocationUtils.convert(world, coordsString);

            if(json.has("yaw")) {
                location.setYaw(json.get("yaw").getAsInt());
            }

            this.locations.put(locationName, location);
        }

        if(this.jsonObject.has("variables")) {
            JsonArray vars = this.jsonObject.getAsJsonArray("variables");

            Iterator<JsonElement> varIt = array.iterator();
            while (varIt.hasNext()) {
                JsonObject json = varIt.next().getAsJsonObject();

                String name = json.get("name").getAsString();
                int value = json.get("value").getAsInt();

                this.variables.put(name, value);
            }
        }
    }

    public World load() {
        try {
            File matchFolder = new File("matches/" + GameAPI.getInstance().matchCount);
            FileUtils.deleteDirectory(matchFolder);
            FileUtils.copyDirectory(folder, matchFolder);

            FileUtils.forceDelete(new File("matches/" + GameAPI.getInstance().matchCount + "/uid.dat"));
            FileUtils.forceDelete(new File("matches/" + GameAPI.getInstance().matchCount + "/session.lock"));

            WorldCreator worldCreator = new WorldCreator("matches/" + GameAPI.getInstance().matchCount).generator(new NullChunkGenerator());
            World world = worldCreator.createWorld();
            world.setAutoSave(false);

            buildLocations(world);
            this.world = world;

            return world;

        } catch (IOException e) {
            e.printStackTrace();
        }

        F.log("ERROR: Unable to load world");
        return null;
    }

    public Location getLocation(String s) {
        return this.locations.get(s);
    }

    public int getVariable(String s) {
        return this.variables.get(s);
    }

    public String getName() {
        return name;
    }

    public String getGame() {
        return game;
    }

    public String getBuilder() {
        return builder;
    }

    public HashMap<String, Location> getLocations() {
        return locations;
    }

    public HashMap<String, Integer> getVariables() {
        return variables;
    }

    public KitRepo getKitRepo() {
        return kitRepo;
    }

    public File getFolder() {
        return folder;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public World getWorld() {
        return world;
    }
}
