package cyclegame.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import cyclegame.rotation.Match;
import cyclegame.templates.game.spectate.SpectateManager;
import main.Main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import cyclegame.GameAPI;

import common.command.Command;
import common.damage.CustomDeathEvent;
import common.kit.Kit;
import common.page.ConfirmPage;
import cyclegame.players.PlayerHandler;
import cyclegame.players.ProPlayer;
import common.util.communication.C;
import common.util.communication.F;
import common.util.communication.S;
import common.util.itemstack.ItemFactory;
import common.util.particles.ParticleEffect;

public class KitCommand extends Command {

	HashMap<Player, Kit> players = new HashMap<>();
	
    public KitCommand() {
        super("kit", Arrays.asList("kits"));
    }

    @Override
    public void call(Player player, ArrayList<String> args) {
		openKitGui(player);
    }	
    
    public void openKitGui(Player player){
    	ProPlayer prop = GameAPI.getPlayerHandler().getProPlayer(player);
    	
    	Inventory gui = Bukkit.getServer().createInventory(player, 54,C.underline + "Kit Menu");
    	ArrayList<Kit> notOwned = new ArrayList<Kit>();


		int invSlot = 1;
    	for (Kit kit : GameAPI.getMatch().map.kitRepo.kits) {
        	if(!prop.gameStats.kits.contains(PlayerHandler.formatKitNameForDatabase(kit)) && kit.price != 0){
        		notOwned.add(kit);
    		} else {
				if(invSlot == 8 || invSlot == 17 || invSlot == 26) {
					invSlot += 2;
				}
    			gui.setItem(invSlot, ItemFactory.createItem(kit.icon, C.green +C.underline+ kit.name, Arrays.asList("", C.gray + "Cost: " + C.aqua + "$" + kit.getCost(), "", C.gray + kit.description)));
				invSlot++;
    		}
    	}

//		ItemStack borderItem = ItemFactory.createItem(Material.STAINED_GLASS_PANE, "");
//		borderItem.setDurability((short) 15);
//		for (int i = 27; i < 36; i++) {
//			gui.setItem(i, borderItem);
//		}

    	for(int i = 0; i < notOwned.size(); i++){
    		Kit kit = notOwned.get(i);
			gui.setItem(54 - (i+1), ItemFactory.createItem(kit.icon, C.red +C.underline+ kit.name, Arrays.asList("", C.gray + "Cost: " + C.aqua + "$" + kit.getCost(), "", C.gray + kit.description)));
    	}

		player.openInventory(gui);
		S.playSound(player, Sound.ENDERDRAGON_WINGS);
	}
    
    @EventHandler
    public void onClick(InventoryClickEvent e){
    	
    	if(e.getInventory().getTitle().equals(C.underline + "Kit Menu")){
			e.setCancelled(true);

    		if(e.getWhoClicked() instanceof Player){
            ProPlayer proPlayer = GameAPI.getPlayerHandler().getProPlayer((Player)e.getWhoClicked());

    		if(!e.getCurrentItem().equals(null) && e.getCurrentItem().getItemMeta() != null){
    			for(final Kit kit: GameAPI.getMatch().map.kitRepo.kits){
					if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(C.green +C.underline+ kit.name)){
						if(kit.price == 0 || proPlayer.gameStats.kits.contains(PlayerHandler.formatKitNameForDatabase(kit))) {

							if (GameAPI.getMatch().getMatchState() == Match.MatchState.GAME && !SpectateManager.isSpectator((Player) e.getWhoClicked())) {
								this.players.put((Player) e.getWhoClicked(), kit);
								e.getWhoClicked().sendMessage("You have selected " + C.yellow + kit.name + C.white + " for your next life.");
							} else {
								proPlayer.setKit(kit);
								e.getWhoClicked().sendMessage("You have selected " + C.yellow + kit.name + C.white + ".");
							}


							e.getWhoClicked().closeInventory();
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.NOTE_PLING, 1, 1);
							return;
						}
					}else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(C.red +C.underline+ kit.name)){
						if(kit.price != 0 && !proPlayer.gameStats.kits.contains(PlayerHandler.formatKitNameForDatabase(kit))) {
							
							final ProPlayer prop = GameAPI.getPlayerHandler().getProPlayer((Player)e.getWhoClicked());
							
							if(prop.coins >= kit.price){
								
								new ConfirmPage(
			                            (Player) e.getWhoClicked(),
			                            new Runnable() {
			                                @Override
			                                public void run() {
			    								purchase(prop, kit);
			                                }
			                            },
			                            new Runnable() {
			                                @Override
			                                public void run() {

			                                }
			                            },
			                            ItemFactory.createItem(Material.IRON_SWORD,
			                                    C.green + "Purchase " + C.yellow + C.bold + kit.name.toUpperCase() + C.green + " for " + C.gold + C.bold + kit.price + " COINS"
			                            ),
			                            "Yes, purchase this kit!",
			                            "No, I don't want this kit!"

			                    );
							} else {
								F.warning(proPlayer.player, "Not enough coins!");
							}
							
						}
					}
    			}

				if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(C.aqua + GameAPI.getMatch().map.kitRepo.defaultKit.name)){
					if (GameAPI.getMatch().getMatchState() == Match.MatchState.GAME && !SpectateManager.isSpectator((Player) e.getWhoClicked())) {
						this.players.put((Player) e.getWhoClicked(), GameAPI.getMatch().map.kitRepo.defaultKit);
						e.getWhoClicked().sendMessage("You have selected " + C.yellow + GameAPI.getMatch().map.kitRepo.defaultKit.name + C.white + " for your next life.");
					} else {
						proPlayer.setKit(GameAPI.getMatch().map.kitRepo.defaultKit);
						e.getWhoClicked().sendMessage("You have selected " + C.yellow + GameAPI.getMatch().map.kitRepo.defaultKit.name + C.white + ".");
					}
					e.getWhoClicked().closeInventory();
					((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.NOTE_PLING, 1, 1);
			}


    		}
    	}
    	}
    }
    
    public void purchase(final ProPlayer proPlayer, Kit kit) {
        proPlayer.coins -= kit.price;
        GameAPI.getPlayerHandler().incrementValue(GameAPI.getPlayerHandler().playersCollection, proPlayer, "coins", -kit.price);

        proPlayer.gameStats.kits.add(PlayerHandler.formatKitNameForDatabase(kit));
        GameAPI.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                GameAPI.getPlayerHandler().uploadKitsToCurrentGame(proPlayer);
            }
        });

        Player player = proPlayer.player;

		this.players.put(player, kit);

//        F.message(player, "You purchased " + C.yellow + C.bold + kit.name.toUpperCase() + C.gray + " for " + C.gold + C.bold + this.kit.price + " COINS");
        F.addCoinsMessage(proPlayer, -kit.price, "Purchased " + kit.name);

        S.playSound(player, Sound.LEVEL_UP);
        ParticleEffect.REDSTONE.display(.2f, .1f, .2f, 1f, 50, proPlayer.getPlayer().getEyeLocation(), Arrays.asList(player));

    }
    
    @EventHandler
    public void onDeath(CustomDeathEvent e) {

		if (this.players.containsKey(e.getDeadPlayer())) {
			Kit kit = this.players.get(e.getDeadPlayer());
			if (kit != null) {
				GameAPI.getPlayerHandler().getProPlayer(e.getDeadPlayer()).setKit(kit);
			}
		}
	}

}
