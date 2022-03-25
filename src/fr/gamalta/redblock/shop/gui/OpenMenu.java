package fr.gamalta.redblock.shop.gui;

import fr.gamalta.redblock.shop.item.ShopItem;
import fr.gamalta.redblock.shop.utils.type.MenuType;

public class OpenMenu {

	private String shopId;
	private int page;
	private MenuType menuType;
	private ShopItem shopItem;

	public OpenMenu(String shopId, int page, MenuType menuType, ShopItem shopItem) {

		this.shopId = shopId;
		this.page = page;
		this.menuType = menuType;
		this.shopItem = shopItem;
	}

	public String getShopId() {
		return shopId;
	}

	public int getPage() {
		return page;
	}

	public MenuType getMenuType() {
		return menuType;
	}

	public ShopItem getMainItem() {
		return shopItem;
	}
}
