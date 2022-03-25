package fr.gamalta.redblock.shop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.gamalta.lib.message.Message;
import fr.gamalta.redblock.shop.Shop;
import fr.gamalta.redblock.shop.utils.Buyer;

public class shopCmd implements CommandExecutor {
	
	private Shop main;
	
	public shopCmd(Shop main) {
		
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		
		if (commandSender instanceof Player) {
			Player player = (Player) commandSender;
			
			if (!main.buyers.containsKey(player)) {
				main.buyers.put(player, new Buyer(player));
			}
			
			if (!main.bannedWorlds.contains(player.getWorld().getName())) {
				
				if (player.hasPermission(main.settingsCFG.getString("Permission.Reload")) && args.length > 0) {
					
					if (args[0].equalsIgnoreCase("Reload")) {
						if (main.buyers != null) {
							
							for (Buyer buyer : main.buyers.values()) {
								
								if (buyer.hasOpenMenu()) {
									buyer.getPlayer().closeInventory();
								}
							}
						}
						
						if (main.fileHandler != null) {
							
							main.fileHandler.close();
							main.getLogger().removeHandler(main.fileHandler);
						}
						
						main.initButtons();
						main.initShops();
						main.initLogger();
						main.settingsCFG.load();
						main.messagesCFG.load();
						
						player.spigot().sendMessage(new Message(main.messagesCFG, "Reload").setPlaceHolderPlayer(player).create());
						
					} else {
						
						player.spigot().sendMessage(new Message(main.messagesCFG, "Usage").setPlaceHolderPlayer(player).create());
						
					}
				} else {
					
					main.getShopManager().openMainMenu(player);
				}
				
			} else {
				
				player.spigot().sendMessage(new Message(main.messagesCFG, "BannedWorlds").setPlaceHolderPlayer(player).create());
				
			}
		}
		
		return true;
	}
}