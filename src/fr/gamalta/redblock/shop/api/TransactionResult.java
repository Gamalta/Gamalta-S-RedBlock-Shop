package fr.gamalta.redblock.shop.api;

import org.bukkit.entity.Player;

import fr.gamalta.redblock.shop.ShopManager;
import fr.gamalta.redblock.shop.item.ShopItem;

public class TransactionResult {

	private final ShopManager.Action action;
	private final TransactionResultType transactionResultType;
	private final ShopItem shopItem;
	private final Player player;
	private final int amount;
	private final double price;
	private final boolean silent;

	public TransactionResult(ShopManager.Action action, TransactionResultType transactionResultType, ShopItem shopItem, Player player, int amount, double price, boolean silent) {
		this.action = action;
		this.transactionResultType = transactionResultType;
		this.shopItem = shopItem;
		this.player = player;
		this.amount = amount;
		this.price = price;
		this.silent = silent;
	}

	public ShopManager.Action getAction() {
		return action;
	}

	public TransactionResultType getTransactionResultType() {
		return transactionResultType;
	}

	public ShopItem getShopItem() {
		return shopItem;
	}

	public Player getPlayer() {
		return player;
	}

	public int getAmount() {
		return amount;
	}

	public double getPrice() {
		return price;
	}

	public boolean getSilent() {
		return silent;
	}
}
