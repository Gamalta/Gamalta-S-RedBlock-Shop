package fr.gamalta.redblock.shop.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.gamalta.redblock.shop.Shop;
import fr.gamalta.redblock.shop.utils.Buyer;
import fr.gamalta.redblock.shop.utils.Utils;

public class onInventoryCloseEvent implements Listener {

	private Shop main;
	private Utils utils;

	public onInventoryCloseEvent(Shop main) {

		this.main = main;
		utils = new Utils();

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent event) {

		Player player = (Player) event.getPlayer();
		Buyer buyer = main.buyers.get(player);

		if (buyer == null) {
			main.buyers.put(player, new Buyer(player));
		}
		
		if (buyer.hasOpenMenu() && !buyer.isSwitchingGui()) {

			utils.updateInventory(player, buyer);
			buyer.setOpenMenu(null);
		}

	}
}
