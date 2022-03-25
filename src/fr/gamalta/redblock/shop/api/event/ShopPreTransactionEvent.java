package fr.gamalta.redblock.shop.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.gamalta.redblock.shop.ShopManager;
import fr.gamalta.redblock.shop.item.ShopItem;

public class ShopPreTransactionEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private ShopManager.Action action;
	private ShopItem shopItem;
	private Player player;
	private int amount;
	private double price;
	private boolean silent;

	public ShopPreTransactionEvent(ShopManager.Action action, ShopItem shopItem, Player player, int amount, double price, boolean silent) {

		this.action = action;
		this.shopItem = shopItem;
		this.player = player;
		this.amount = amount;
		this.price = price;
		this.silent = silent;
	}

	@Override
	public boolean isCancelled() {

		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {

		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {

		return handlers;
	}

	public static HandlerList getHandlerList() {

		return handlers;
	}

	public ShopItem getShopItem() {

		return shopItem;
	}

	public ShopManager.Action getAction() {

		return action;
	}

	public Player getPlayer() {

		return player;
	}

	public int getAmount() {

		return amount;
	}

	public void setAmount(int amount) {

		this.amount = amount;
	}

	public double getPrice() {

		return price;
	}

	public void setPrice(double price) {

		this.price = price;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}
}