package common.util;

import java.util.ArrayList;

import main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import common.util.communication.C;

public class Poll {

	public static Poll activePoll;
	
	String name;
	String question;
	int rewardTickets;
	boolean allowMultiplier;
	
	ArrayList<String> answers = new ArrayList<String>();
	
	ArrayList<Player> alreadyPolled = new ArrayList<Player>();
	
	public Poll(String name, String question, int rewardTickets){
		
		this.name = name;
		this.question = question;
		this.rewardTickets = rewardTickets;
		
		//Allow multiplier will allow reward multiplication for ranks.
		this.allowMultiplier = false;
	}
	
	public void sendToPlayer(Player player){
		if(!alreadyPolled.contains(player)){
			player.sendMessage(C.gold +""+ C.bold + "POLL:");
			player.sendMessage("");
			player.sendMessage(C.gold + this.question);
			player.sendMessage(C.gold + "Reward: " + C.aqua + this.rewardTickets + " tickets");
			for(int i = 0; i < answers.size(); i++){
			
			TextComponent message = new TextComponent(" " + (i+1) + ": " + this.answers.get(i));
			message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND,
					
					//Command to run when clicked:
					"answerpoll " + this.name + (i+1) 
					
					) );
			
			message.setColor(ChatColor.GRAY);
			
			message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Select option " + (i+1)).create() ) );
						
			player.sendMessage(message+"");
			
			}
		}
	}
	
	public void sendToServer(){
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			if(!alreadyPolled.contains(player)){
				player.sendMessage(C.gold +""+ C.bold + "POLL:");
				player.sendMessage("");
				player.sendMessage(C.gold + this.question);
				player.sendMessage(C.gold + "Reward: " + C.red + this.rewardTickets);
				for(int i = 0; i < answers.size(); i++){
				
				TextComponent message = new TextComponent(" " + (i+1) + ": " + this.answers.get(i));
				message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND,
						
						//Command to run when clicked:
						"answerpoll " + (i+1) 
						
						) );
				
				message.setColor(ChatColor.GRAY);
				
				message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Select option " + (i+1)).create() ) );
							
				player.sendMessage(message+"");
				
				}
			}
		}
	}
	
	
	/*
	 * Setters
	 */
	
	public void publish(){
		
		if(activePoll.equals(null)){
		
		//TODO save all data to database
		activePoll = this;
		sendToServer();
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable(){

			@Override
			public void run() {
				
				activePoll = null;
				
			}
			
			//TODO Change this to 20*60*60 (1 hour)
			//when done testing (currently 1 minute)
			
		}, 20*60 /*(1 hour)*/);
		
		}
	}
	
	public void addAnswer(String answer){
		answers.add(answer);
	}
	
	public void setMultipler(boolean allowed){
		this.allowMultiplier = allowed;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setQuestion(String question){
		this.question = question;
	}
	
	public void setRewardTickets(int tickets){
		this.rewardTickets = tickets;
	}
	
	/*
	 * Getters
	 */
	
	public ArrayList<String> getAnswers(){
		return this.answers;
	}
	
	public ArrayList<Player> getPolledPlayers(){
		return this.alreadyPolled;
	}
	
	public String getName(){
		return this.name;
	}
	
	public boolean getMultiplier(){
		return this.allowMultiplier;
	}
	
	public String getQuestion(){
		return this.question;
	}
	
	public int getRewardTickets(){
		return this.rewardTickets;
	}
	
}
