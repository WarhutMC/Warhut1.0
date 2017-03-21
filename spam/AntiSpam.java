package common.spam;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import common.Manager;
import common.util.communication.F;
import main.Main;

/**
 * Created by luke on 12/9/15.
 * ACTUALLY created by Gamer on 12/12/15 ;)
 */
public class AntiSpam implements Manager, Listener {

	public final ConcurrentMap<Player, String> lastMessage = new ConcurrentHashMap<Player, String>();
	public final ConcurrentMap<Player, Long> delay = new ConcurrentHashMap<Player, Long>();
	
	public AntiSpam() {
		Main.getInstance().registerListener(this);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void asyncChatEvent(AsyncPlayerChatEvent e) {
		final Player player = e.getPlayer();
		if (player.hasPermission("mcnet.mod")) {
			return;
		}

		String message = e.getMessage();
		Calendar calendar = Calendar.getInstance();

		if (!delay.containsKey(player)) {
			delay.put(player, calendar.getTimeInMillis());
			return;
		}
		
		if (calendar.getTimeInMillis() < delay.get(player) + 2000) {
			e.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You can talk every 2 seconds.");
			return;
		}

		delay.put(player, calendar.getTimeInMillis());

		/**
		 * Makes a new string to check for advertisement and removes allowed
		 * domains from the advert check
		 */
		String advertCheck = message;
		for (String allow : allowedDomains) {
			advertCheck = advertCheck.replace(allow, "");
		}

		// Checks for domain extensions in the new string
		for (String ext : extensions) {
			if (advertCheck.contains(ext)) {
				e.setCancelled(true);
				F.warning(player, "Do not advertise!");
				return;
			}
		}

		/**
		 * Checks for bad words
		 */
		String alphaNumeric = message.replaceAll("[^\\p{Alpha}\\p{Digit}]+", "");
		for (String swear : banned) {
			if (alphaNumeric.toLowerCase().contains(swear)) {
				e.setCancelled(true);
				F.warning(player, "Do not swear!");
				return;
			}
		}
		
		 String last = null;
		if (lastMessage.containsKey(player))
			last = lastMessage.get(player);
		if (last == null) {
			lastMessage.put(player, "");
			return;
		}
		
		// If they hit up and typed the same message, this'll catch em ;)
		if (message.equals(lastMessage.get(player))) {
			e.setCancelled(true);
			F.warning(player, "Do not spam!");
			lastMessage.put(player, message);
			return;
		}
		
		/**
		String[] mA = message.split("");
		String[] lA = last.split("");

		long flagCount = 0;
		for (int i = 0; i < mA.length; i++) {
			for (int j = 0; j < lA.length; j++) {
				try {
					if (mA[i].equals(lA[j]) || lA[j].equals(mA[i])) {
						flagCount++;
						continue;
					} else if (mA[i].equals(lA[j - 1]) || mA[i].equals(lA[j + 1]) || lA[j].equals(mA[i - 1])
							|| lA[j].equals(mA[i + 1])) {
						flagCount += 0.5;
						continue;
					} else if (mA[i].equals(lA[j - 2]) || mA[i].equals(lA[j + 2]) || lA[j].equals(mA[i - 2])
							|| lA[j].equals(mA[i + 2])) {
						flagCount += 0.2;
						continue;
					}
				} catch (ArrayIndexOutOfBoundsException ex) {
					continue;
				}
			}
		}

		String highestLength = message;
		String lowestLength = last;
		if (last.length() > message.length()) {
			highestLength = last;
			lowestLength = message;
		}

		if (flagCount > highestLength.length()) {
			e.setCancelled(true);
			player.sendMessage(ChatColor.RED + "Do not spam!");
			return;
		}	

		lastMessage.put(player, message);
		*/
	}

	// List of domains to be skipped over during the advertisement check
	public final List<String> allowedDomains = Arrays.asList("mchost.co", "mcwar.us", "youtube.com", "imgur.com",
			"prntscr.com", "google.com", "lmgtfy.com", "pastebin.com");

	// List of extensions to check for advertisement
	public final List<String> extensions = Arrays.asList(".com", ".net", ".us", ".co", ".se", ".fi", ".org", ".gs");

	// List of banned words
	public final List<String> banned = Arrays.asList("fuck", "shit", "cock", "penis", "nigger", "rape", "anal", "anus",
			"bitch", "dick", "porn", "cunt", "pussy", "twat", "whore", "slut", "faggot", "prick", "dildo", "cum",
			"jizz", "sperm", "ass", "blowjob", "cameltoe", "camel-toe", "camel toe", "chink", "chinc", "clit", "choad",
			"chode", "wanker", "dago", "dyke", "faggit", "fag", "fellatio", "gaylord", "gaytard", "gaywad", "gooch",
			"gook", "gringo", "guido", "handjob", "honkey", "queer", "retard");

	@Override
	public void unload() {

	}
}
