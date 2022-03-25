package fr.gamalta.redblock.shop.item;

import fr.gamalta.lib.item.RedItem;
import fr.gamalta.redblock.shop.utils.type.ButtonType;
import fr.gamalta.redblock.shop.utils.type.ItemType;

public class ButtonItem extends ShopItem {

	private ButtonType buttonType;

	public ButtonItem(String shopId, ItemType itemType, RedItem redItem, int page, int slot, ButtonType buttonType) {

		super(shopId, itemType, redItem, page, slot, -1, -1);
		this.buttonType = buttonType;
	}

	public ButtonType getButtonType() {
		return buttonType;
	}

	public void setButtonType(ButtonType buttonType) {
		this.buttonType = buttonType;
	}
}
