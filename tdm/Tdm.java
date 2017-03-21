package cyclegame.games.tdm;

import java.util.HashMap;

import org.bukkit.ChatColor;

import common.util.communication.C;
import cyclegame.rotation.GameMap;
import cyclegame.templates.game.Game;
import cyclegame.templates.game.Team;

/*
 * Created by Gamer 12/13/15
 */
public class Tdm extends Game {
	public Team blueTeam, redTeam, greenTeam, yellowTeam;
	
	private HashMap<Team, Integer> kills = new HashMap<>();

	public Tdm(GameMap gameMap) {
		super(gameMap);

		super.gameSettings.gameName = "TDM";
		super.gameSettings.setupMessage = C.white + "Reac";
	}

	@Override
	public void setupTeams() {
		this.blueTeam = super.addTeam(new Team("Blue", ChatColor.BLUE));
		this.redTeam = super.addTeam(new Team("Red", ChatColor.RED));
		this.greenTeam = super.addTeam(new Team("Green", ChatColor.GREEN));
		this.yellowTeam = super.addTeam(new Team("Yellow", ChatColor.YELLOW));
		
		this.blueTeam.addSpawn(map.getLocation("blue-spawn"));
		this.redTeam.addSpawn(map.getLocation("red-spawn"));
		this.greenTeam.addSpawn(map.getLocation("green-spawn"));
		this.yellowTeam.addSpawn(map.getLocation("yellow-spawn"));
	}

	@Override
	public void setupGame() {
		
		super.startGame();
	}

	@Override
	public void extraUnload() {

	}

}
