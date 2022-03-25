package fr.gamalta.redblock.shop.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.gamalta.redblock.shop.Shop;

public class onPlayerQuitEvent implements Listener {

	private Shop main;

	public onPlayerQuitEvent(Shop main) {
		this.main = main;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		Player player = event.getPlayer();
		main.buyers.remove(player);
	}
}
