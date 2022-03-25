package fr.gamalta.redblock.shop.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.gamalta.redblock.shop.Shop;
import fr.gamalta.redblock.shop.utils.Buyer;

public class onPlayerJoinEvent implements Listener {

	private Shop main;

	public onPlayerJoinEvent(Shop main) {
		this.main = main;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();
		main.buyers.put(player, new Buyer(player));
	}
}
