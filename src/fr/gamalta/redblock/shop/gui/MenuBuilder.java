package fr.gamalta.redblock.shop.gui;

import java.util.ArrayList;
import java.util.List;

import fr.gamalta.redblock.shop.item.ButtonItem;
import fr.gamalta.redblock.shop.item.ShopItem;

public class MenuBuilder {

	private String shopId;
	private List<String> name;
	private int size;
	private List<ShopItem> items;
	private List<ButtonItem> buttons;

	public MenuBuilder(String shopId, List<String> name, int size, List<ShopItem> items, List<ButtonItem> buttons) {

		this.shopId = shopId;
		this.name = name;
		this.size = size;
		this.items = items;
		this.buttons = buttons;
	}

	public String getShopId() {
		return shopId;
	}

	public List<String> getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	public List<ShopItem> getItems() {
		return new ArrayList<>(items);
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public void setName(List<String> name) {
		this.name = name;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setItems(List<ShopItem> items) {
		this.items = items;
	}

	public void addItem(ShopItem item) {

		items.add(item);
	}

	public List<ButtonItem> getButtons() {
		return new ArrayList<>(buttons);
	}

	public void addButton(ButtonItem buttons) {

		items.add(buttons);
	}

	public void removeButton(ButtonItem buttons) {

		items.remove(buttons);
	}

	public void setButtons(List<ButtonItem> buttons) {
		this.buttons = buttons;
	}
}
