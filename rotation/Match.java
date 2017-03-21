package cyclegame.rotation;

import common.DBSyncer;
import common.Manager;
import common.kit.Kit;
import cyclegame.players.ProPlayer;
import cyclegame.games.domination.Domination;
import cyclegame.games.dtm.Dtm;
import cyclegame.games.dtm.WarzonePreGame;
import cyclegame.templates.event.GameStartEvent;
import cyclegame.templates.pregame.PreGame;
import main.Main;
import cyclegame.GameAPI;
import cyclegame.games.dtm.WarzoneKitRepo;
import cyclegame.templates.game.Game;
import org.bukkit.Bukkit;

import java.util.HashMap;

/**
 * Created by luke on 10/17/15.
 */
public class Match {
    public GameMap map;

//    public Lobby lobby;
    public PreGame preGame;
    public Game game;

    public MatchState matchState;

    public HashMap<String, Manager> customManagers = new HashMap<>();

    public DBSyncer dbSyncer;

    public enum MatchState {
        PREGAME,
        GAME
    }

    public Match(GameMap map) {
        this.map = map;
        map.load();

        this.map.setKitRepo(this.createKitRepo(map));

        if (map.game.toLowerCase().contains("dtm") || map.game.toLowerCase().contains("warzone") || map.game.toLowerCase().contains("domination")) {
            GameAPI.getPlayerHandler().setCurrentGameCollection("warzone");
            this.dbSyncer = new DefaultDBSyncer(GameAPI.getInstance().getDb().getCollection("warzone"));
        }

        else {
            GameAPI.getPlayerHandler().setCurrentGameCollection(map.game.toLowerCase());
            this.dbSyncer = new DefaultDBSyncer(GameAPI.getInstance().getDb().getCollection(map.game.toLowerCase()));
        }

        for (ProPlayer proPlayer : GameAPI.getPlayerHandler().getProPlayers()) {
            boolean found = false;
            for (Kit kit : map.kitRepo.kits) {
                if (proPlayer.kit.getName().equals(kit.getName())) {
                    proPlayer.setKit(kit);
                    found = true;
                    break;
                }
            }
            if (!found) {
                proPlayer.setKit(map.getKitRepo().defaultKit);
            }

            this.dbSyncer.sync(proPlayer);
        }
    }

    public void startPreGame() {
        this.matchState = MatchState.PREGAME;

        this.game = createGame(map);
        this.game.setupTeams();

        this.preGame = createPreGame(game);
        preGame.startPreGame();
    }

    public void startGame() {
        this.matchState = MatchState.GAME;

        Main.registerListener(this.game);
        this.game.setupGame();

        Bukkit.getServer().getPluginManager().callEvent(new GameStartEvent());

        this.preGame.unload();
        this.preGame = null;
    }



//    public void initializePreGame() {
//        this.matchState = MatchState.PREGAME;
//
//        this.preGame = createPreGame(map);
//        this.preGame.startPreGame();
//    }
//
//    public void initializeGame() {
//        this.matchState = MatchState.GAME;
//        this.game = this.createGame(map);
//
//        //unload lobby
//        File lobbyWorldFolder = this.preGame.getWorld().getWorldFolder();
//        Bukkit.getServer().unloadWorld(this.preGame.getWorld(), false);
//        try {
//            FileUtils.deleteDirectory(lobbyWorldFolder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        this.preGame.unload();
//        this.preGame = null;
//    }

    public KitRepo createKitRepo(GameMap map) {
        switch (map.game.toLowerCase()) {

            case "warzone": {
                return new WarzoneKitRepo();
            }

            case "dtm": {
                return new WarzoneKitRepo();
            }

            case "domination": {
                return new WarzoneKitRepo();
            }
        }
        return null;
    }

    public PreGame createPreGame(Game game) {
        switch (map.game.toLowerCase()) {

            case "warzone": {
                return new WarzonePreGame(game);
            }

            case "dtm": {
                return new WarzonePreGame(game);
            }

            case "domination": {
                return new WarzonePreGame(game);
            }
        }
        return null;
    }

    public Game createGame(GameMap map) {
        switch (map.game.toLowerCase()) {

            case "warzone": {
                return new Dtm(map);
            }

            case "dtm": {
                return new Dtm(map);
            }
            case "domination": {
                return new Domination(map);
            }
        }
        return null;
    }

    public void unload() {

        this.dbSyncer.unload();

        for (Manager manager : this.customManagers.values()) {
            manager.unload();
        }

        if (this.preGame != null) {
            this.preGame.unload();
        }

        if (this.game != null) {
            this.game.unload();
        }

        this.map.kitRepo.defaultKit.unload();
        for (Kit kit : this.map.kitRepo.kits) {
            kit.unload();
        }

        GameAPI.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (ProPlayer proPlayer : GameAPI.getPlayerHandler().proPlayers) {
                    GameAPI.getPlayerHandler().uploadEndOfGameStats(proPlayer);
                }
            }
        });
    }

    public HashMap<String, Manager> getCustomManagers() {
        return customManagers;
    }

    public GameMap getMap() {
        return map;
    }

    public PreGame getPreGame() {
        return preGame;
    }

    public Game getGame() {
        return game;
    }

    public MatchState getMatchState() {
        return matchState;
    }

    public DBSyncer getDbSyncer() {
        return dbSyncer;
    }
}
