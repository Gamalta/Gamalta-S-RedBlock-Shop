package fr.gamalta.redblock.shop.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.gamalta.lib.RedLib;
import fr.gamalta.lib.config.Configuration;
import fr.gamalta.lib.item.RedItem;
import fr.gamalta.redblock.customitems.api.CustomItemAPI;
import fr.gamalta.redblock.shop.Shop;
import fr.gamalta.redblock.shop.item.ButtonItem;
import fr.gamalta.redblock.shop.item.ShopItem;
import fr.gamalta.redblock.shop.utils.type.ButtonType;
import fr.gamalta.redblock.shop.utils.type.ItemType;
import me.clip.placeholderapi.PlaceholderAPI;

public class MainMenu {

	private FileConfiguration config;
	private Shop main;
	private RedLib lib;
	private String name;
	private int size;
	private List<ShopItem> items = new ArrayList<>();
	private List<ButtonItem> buttons = new ArrayList<>();

	public MainMenu(Shop main) {

		this.main = main;
		lib = new RedLib();
		config = new Configuration(main, main.parentFileName + "/Inventories", "MainMenu").getConfig();
	}

	public void init() {

		items.clear();
		buttons.clear();
		name = config.getString("Name", "Magasin");
		size = config.getInt("Size", 56);

		for (String string : config.getConfigurationSection("Items").getKeys(false)) {

			String key = "Items." + string + ".";

			int slot = Integer.parseInt(string);
			String shopId = config.getString(key + "ShopId", "none");

			if (config.contains(key + "Button")) {

				ButtonType buttonType = ButtonType.valueOf(config.getString(key + "Button"));

				buttons.add(new ButtonItem(shopId, ItemType.DECORATIVE, main.buttons.get(buttonType), 0, slot, buttonType));

			} else {

				RedItem redItem;

				if (config.contains(key + "CustomItem")) {

					redItem = CustomItemAPI.getRedItemById(config.getString(key + "CustomItem"));
					redItem.setAmount(config.getInt(key + "Amount", redItem.getAmount()));

				} else {

					redItem = new RedItem(main, config, "Items." + string);

				}

				items.add(new ShopItem(shopId, ItemType.DECORATIVE, redItem, 0, slot, -1D, -1D));

			}
		}
	}

	public Inventory getPlayerFormatedMainMenu(Player player) {

		Inventory inventory = Bukkit.createInventory(null, size, lib.color(PlaceholderAPI.setPlaceholders(player, name)));

		List<ShopItem> itemList = new ArrayList<>();
		itemList.addAll(items);
		itemList.addAll(buttons);

		for (ShopItem shopItem : itemList) {

			RedItem redItem = shopItem.getRedItem().clone();
			redItem.setName(PlaceholderAPI.setPlaceholders(player, redItem.getName()));
			redItem.setLores(PlaceholderAPI.setPlaceholders(player, redItem.getLores()));

			inventory.setItem(shopItem.getSlot(), redItem.create());

		}

		return inventory;
	}

	public List<ShopItem> getItems() {
		return items;
	}

	public List<ButtonItem> getButtons() {

		return buttons;
	}
}
