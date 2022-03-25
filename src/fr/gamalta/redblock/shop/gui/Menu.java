package fr.gamalta.redblock.shop.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import fr.gamalta.redblock.shop.utils.Utils;
import fr.gamalta.redblock.shop.utils.type.ButtonType;
import fr.gamalta.redblock.shop.utils.type.ItemType;
import me.clip.placeholderapi.PlaceholderAPI;

public class Menu {

	private final Shop main;
	private final Utils utils;
	private final RedLib lib;
	private final HashMap<String, MenuBuilder> menus = new HashMap<>();

	public Menu(Shop main) {

		this.main = main;
		utils = new Utils();
		lib = new RedLib();
	}

	public void init() {

		menus.clear();
		List<String> shops = main.settingsCFG.getStringList("Shops");

		for (String shopId : shops) {

			FileConfiguration config = new Configuration(main, main.parentFileName + "/Inventories/", shopId, "DefaultMenu").getConfig();
			List<String> names = config.getStringList("Name");
			int size = config.getInt("Size", 56);
			List<ShopItem> items = new ArrayList<>();
			List<ButtonItem> buttons = new ArrayList<>();

			for (String firstKey : config.getConfigurationSection("Items").getKeys(false)) {

				int page = Integer.valueOf(firstKey);

				for (String secondKey : config.getConfigurationSection("Items." + firstKey).getKeys(false)) {

					String key = "Items." + firstKey + "." + secondKey + ".";

					int slot = Integer.parseInt(secondKey);

					if (config.contains(key + "Button")) {

						ButtonType buttonType = ButtonType.valueOf(config.getString(key + "Button"));
						buttons.add(new ButtonItem(shopId, ItemType.DECORATIVE, main.buttons.get(buttonType), page, slot, buttonType));

					} else {

						RedItem redItem;
						ItemType itemType;
						double buy = config.getDouble(key + "Buy", -1D);
						double sell = config.getDouble(key + "Sell", -1D);

						if (buy == -1D) {

							if (sell == -1D) {

								itemType = ItemType.DECORATIVE;

							} else {

								itemType = ItemType.SELL_ONLY;
							}

						} else {

							if (sell != -1D) {

								itemType = ItemType.NORMAL;

							} else {

								itemType = ItemType.BUY_ONLY;
							}
						}

						if (config.contains(key + "CustomItem")) {

							redItem = CustomItemAPI.getRedItemById(config.getString(key + "CustomItem"));
							redItem.setAmount(config.getInt(key + "Amount", redItem.getAmount()));

						} else {

							redItem = new RedItem(main, config, "Items." + firstKey + "." + secondKey);

						}

						items.add(new ShopItem(shopId, itemType, redItem, page, slot, buy, sell));

					}

				}
			}

			menus.put(shopId, new MenuBuilder(shopId, names, size, items, buttons));
		}
	}

	public Inventory getPlayerFormatedShopMenu(Player player, String shopId, int page) {

		MenuBuilder menuBuilder = menus.get(shopId);
		Inventory inventory = Bukkit.createInventory(null, menuBuilder.getSize(), lib.color(PlaceholderAPI.setPlaceholders(player, menuBuilder.getName().get(page))));

		List<ShopItem> itemList = new ArrayList<>();
		itemList.addAll(menuBuilder.getItems());
		itemList.addAll(menuBuilder.getButtons());

		for (ShopItem shopItem : itemList) {

			if (shopItem.getPage() == page) {

				RedItem redItem = shopItem.getRedItem().clone();
				redItem.setName(PlaceholderAPI.setPlaceholders(player, redItem.getName()));

				switch (shopItem.getItemType()) {

				case NORMAL:

					redItem.addLore(lib.color(main.settingsCFG.getStringList("Default.Type.Normal.Lore")));
					break;

				case BUY_ONLY:

					redItem.addLore(lib.color(main.settingsCFG.getStringList("Default.Type.BuyOnly.Lore")));
					break;

				case SELL_ONLY:

					redItem.addLore(lib.color(main.settingsCFG.getStringList("Default.Type.SellOnly.Lore")));
					break;
				default:
					break;
				}

				redItem.setLores(PlaceholderAPI.setPlaceholders(player, redItem.getLores()));
				redItem.replace("%item%", utils.formatItemName(redItem));
				redItem.replace("%buy%", shopItem.getBuyPrice() + "");
				redItem.replace("%sell%", shopItem.getSellPrice() + "");

				inventory.setItem(shopItem.getSlot(), redItem.create());
			}
		}

		return inventory;
	}

	public Map<String, MenuBuilder> getMenus() {
		return menus;
	}
}
