package fr.gamalta.redblock.shop.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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

public class SellMoreMenu {

	private FileConfiguration config;
	private Shop main;
	private Utils utils;
	private RedLib lib;
	private String name;
	private int size;
	private List<ButtonItem> buttons = new ArrayList<>();
	private HashMap<Integer, Integer> stacks = new HashMap<>();

	public SellMoreMenu(Shop main) {

		this.main = main;
		utils = new Utils();
		lib = new RedLib();
		config = new Configuration(main, main.parentFileName + "/Inventories", "SellMoreMenu").getConfig();
	}

	public void init() {

		buttons.clear();
		stacks.clear();
		name = config.getString("Name", "Magasin");
		size = config.getInt("Size", 54);

		for (String string : config.getConfigurationSection("Stacks").getKeys(false)) {

			stacks.put(Integer.valueOf(string), config.getInt("Stacks." + string));

		}

		for (String string : config.getConfigurationSection("Items").getKeys(false)) {

			String key = "Items." + string + ".";
			int slot = Integer.valueOf(string);

			if (config.contains(key + ".Button")) {

				ButtonType buttonType = ButtonType.valueOf(config.getString(key + ".Button"));

				buttons.add(new ButtonItem(null, ItemType.DECORATIVE, main.buttons.get(buttonType), 0, slot, buttonType));
			}
		}
	}

	public Inventory getPlayerFormatedSellMoreMenu(Player player, ShopItem shopItem) {

		RedItem mainItem = shopItem.getRedItem().clone();
		Inventory inventory = Bukkit.createInventory(null, size, lib.color(PlaceholderAPI.setPlaceholders(player, name.replace("%item%", utils.formatItemName(mainItem)))));

		mainItem.setLores(PlaceholderAPI.setPlaceholders(player, mainItem.getLores()));
		mainItem.setName(PlaceholderAPI.setPlaceholders(player, mainItem.getName()));
		mainItem.addLore(main.settingsCFG.getStringList("Default.Type.SellOnly.Lore"));
		mainItem.replace("%item%", utils.formatItemName(mainItem));

		for (Entry<Integer, Integer> entry : stacks.entrySet()) {

			RedItem redItem = mainItem.clone();
			redItem.replace("%buy%", (shopItem.getBuyPrice() * redItem.getMaterial().getMaxStackSize() * entry.getValue()) + "");
			redItem.replace("%sell%", (shopItem.getSellPrice() * redItem.getMaterial().getMaxStackSize() * entry.getValue()) + "");
			redItem.setAmount(entry.getValue());
			inventory.setItem(entry.getKey(), redItem.create());
		}

		for (ButtonItem buttonItem : buttons) {

			RedItem redItem = buttonItem.getRedItem().clone();
			redItem.setName(PlaceholderAPI.setPlaceholders(player, redItem.getName()));
			redItem.setLores(PlaceholderAPI.setPlaceholders(player, redItem.getLores()));
			redItem.replace("%item%", utils.formatItemName(shopItem.getRedItem()));
			redItem.replace("%buy%", shopItem.getBuyPrice() + "");
			redItem.replace("%sell%", shopItem.getSellPrice() + "");

			inventory.setItem(buttonItem.getSlot(), redItem.create());

		}

		return inventory;
	}

	public List<ButtonItem> getButtons() {
		return buttons;
	}

	public HashMap<Integer, Integer> getStacks() {

		return stacks;
	}
}
