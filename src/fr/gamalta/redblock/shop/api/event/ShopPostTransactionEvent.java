package fr.gamalta.redblock.shop.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.gamalta.redblock.shop.api.TransactionResult;

public class ShopPostTransactionEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	private TransactionResult transactionResult;

	public ShopPostTransactionEvent(TransactionResult transactionResult) {

		this.transactionResult = transactionResult;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public TransactionResult getResult() {

		return transactionResult;
	}
}
