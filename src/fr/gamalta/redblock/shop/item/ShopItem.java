package fr.gamalta.redblock.shop.item;

import fr.gamalta.lib.item.RedItem;
import fr.gamalta.redblock.shop.utils.type.ItemType;

public class ShopItem {

	private String shopId;
	private ItemType itemType;
	private RedItem redItem;
	private int page;
	private int slot;
	private double buy;
	private double sell;

	public ShopItem(String shopId, ItemType itemType, RedItem redItem, int page, int slot, double buy, double sell) {

		this.shopId = shopId;
		this.itemType = itemType;
		this.redItem = redItem;
		this.page = page;
		this.slot = slot;
		this.buy = buy;
		this.sell = sell;

	}

	public String getShopId() {
		return shopId;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public RedItem getRedItem() {
		return redItem;
	}

	public int getPage() {
		return page;
	}

	public int getSlot() {
		return slot;
	}

	public double getBuyPrice() {
		return buy;
	}

	public double getSellPrice() {
		return sell;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public void setRedItem(RedItem redItem) {
		this.redItem = redItem;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public void setBuy(double buy) {
		this.buy = buy;
	}

	public void setSell(double sell) {
		this.sell = sell;
	}
}
