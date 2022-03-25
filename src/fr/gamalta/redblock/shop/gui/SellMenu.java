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
import fr.gamalta.redblock.shop.Shop;
import fr.gamalta.redblock.shop.item.ButtonItem;
import fr.gamalta.redblock.shop.item.ShopItem;
import fr.gamalta.redblock.shop.utils.Utils;
import fr.gamalta.redblock.shop.utils.type.ButtonType;
import fr.gamalta.redblock.shop.utils.type.ItemType;
import me.clip.placeholderapi.PlaceholderAPI;

public class SellMenu {

	private FileConfiguration config;
	private Shop main;
	private Utils utils;
	private RedLib lib;
	private String name;
	private int size;
	private int itemSlot;
	private List<ButtonItem> buttons = new ArrayList<>();

	public SellMenu(Shop main) {

		this.main = main;
		utils = new Utils();
		lib = new RedLib();
		config = new Configuration(main, main.parentFileName + "/Inventories", "SellMenu").getConfig();
	}

	public void init() {

		buttons.clear();
		name = config.getString("Name", "Magasin");
		size = config.getInt("Size", 54);
		itemSlot = config.getInt("ItemSlot", 22);

		for (String string : config.getConfigurationSection("Items").getKeys(false)) {

			String key = "Items." + string + ".";

			if (config.contains(key + "Button")) {

				ButtonType buttonType = ButtonType.valueOf(config.getString(key + "Button"));
				int slot = Integer.valueOf(string);

				buttons.add(new ButtonItem(null, ItemType.DECORATIVE, main.buttons.get(buttonType), 0, slot, buttonType));

			}
		}
	}

	public Inventory getPlayerFormatedSellMenu(Player player, ShopItem shopItem) {

		RedItem mainItem = shopItem.getRedItem().clone();
		Inventory inventory = Bukkit.createInventory(null, size, lib.color(PlaceholderAPI.setPlaceholders(player, name.replace("%item%", utils.formatItemName(mainItem)))));

		mainItem.setLores(PlaceholderAPI.setPlaceholders(player, mainItem.getLores()));
		mainItem.setName(PlaceholderAPI.setPlaceholders(player, mainItem.getName()));
		mainItem.addLore(main.settingsCFG.getStringList("Default.Type.SellOnly.Lore"));
		mainItem.replace("%item%", utils.formatItemName(mainItem));
		mainItem.replace("%buy%", shopItem.getBuyPrice() + "");
		mainItem.replace("%sell%", shopItem.getSellPrice() + "");
		mainItem.setAmount(1);

		inventory.setItem(itemSlot, mainItem.create());

		for (ButtonItem buttonItem : buttons) {

			ButtonType buttonType = buttonItem.getButtonType();

			if (!(mainItem.getMaterial().getMaxStackSize() == 1 && buttonType.canEdit())) {

				if (buttonType != ButtonType.SET_1 && buttonType != ButtonType.REMOVE_1 && buttonType != ButtonType.REMOVE_10) {

					RedItem redItem = buttonItem.getRedItem().clone();
					redItem.setName(PlaceholderAPI.setPlaceholders(player, redItem.getName()));
					redItem.setLores(PlaceholderAPI.setPlaceholders(player, redItem.getLores()));
					redItem.replace("%item%", utils.formatItemName(redItem));
					redItem.replace("%max_stack_size%", mainItem.getMaterial().getMaxStackSize() + "");

					if (buttonType == ButtonType.SET_MAX_STACK_SIZE) {
						redItem.setAmount(mainItem.getMaterial().getMaxStackSize());
					}

					inventory.setItem(buttonItem.getSlot(), redItem.create());
				}
			}
		}

		return inventory;
	}

	public List<ButtonItem> getButtons() {
		return buttons;
	}

	public int getItemSlot() {
		return itemSlot;
	}
}
