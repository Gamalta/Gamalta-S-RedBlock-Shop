package fr.gamalta.redblock.shop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.gamalta.lib.message.Message;
import fr.gamalta.redblock.shop.api.TransactionResult;
import fr.gamalta.redblock.shop.api.TransactionResultType;
import fr.gamalta.redblock.shop.api.event.ShopPostTransactionEvent;
import fr.gamalta.redblock.shop.api.event.ShopPreTransactionEvent;
import fr.gamalta.redblock.shop.gui.BuyMenu;
import fr.gamalta.redblock.shop.gui.BuyMoreMenu;
import fr.gamalta.redblock.shop.gui.MainMenu;
import fr.gamalta.redblock.shop.gui.Menu;
import fr.gamalta.redblock.shop.gui.OpenMenu;
import fr.gamalta.redblock.shop.gui.SellMenu;
import fr.gamalta.redblock.shop.gui.SellMoreMenu;
import fr.gamalta.redblock.shop.item.ShopItem;
import fr.gamalta.redblock.shop.utils.Buyer;
import fr.gamalta.redblock.shop.utils.Utils;
import fr.gamalta.redblock.shop.utils.type.MenuType;

public class ShopManager {

	private Shop main;
	private Utils utils;
	private MainMenu mainMenu;
	private Menu menu;
	private BuyMenu buyMenu;
	private SellMenu sellMenu;
	private BuyMoreMenu buyMoreMenu;
	private SellMoreMenu sellMoreMenu;

	public enum Action {
		BUY,
		SELL,
		SELL_ALL
	}

	public ShopManager(Shop main) {

		this.main = main;
		utils = new Utils();
		mainMenu = new MainMenu(main);
		menu = new Menu(main);
		buyMenu = new BuyMenu(main);
		sellMenu = new SellMenu(main);
		buyMoreMenu = new BuyMoreMenu(main);
		sellMoreMenu = new SellMoreMenu(main);

	}

	public void init() {

		mainMenu.init();
		menu.init();
		buyMenu.init();
		sellMenu.init();
		buyMoreMenu.init();
		sellMoreMenu.init();

	}

	public String getShopIdBySlot(int slot) {

		return mainMenu.getItems().get(slot).getShopId();
	}

	public void openMainMenu(Player player) {

		utils.openInventory(player, main.buyers.get(player), mainMenu.getPlayerFormatedMainMenu(player), new OpenMenu("Main", 0, MenuType.MAIN_MENU, null));

	}

	public void openShop(Player player, String shopId) {

		openShop(player, shopId, 0);
	}

	public void openShop(Player player, String shopId, int page) {

		utils.openInventory(player, main.buyers.get(player), menu.getPlayerFormatedShopMenu(player, shopId, page), new OpenMenu(shopId, page, MenuType.MENU, null));

	}

	public void openBuyMenu(Player player, String shopId, ShopItem shopItem) {

		utils.openInventory(player, main.buyers.get(player), buyMenu.getPlayerFormatedBuyMenu(player, shopItem), new OpenMenu(shopId, 0, MenuType.BUY_MENU, shopItem));

	}

	public void openBuyMoreMenu(Player player, String shopId, ShopItem shopItem) {

		utils.openInventory(player, main.buyers.get(player), buyMoreMenu.getPlayerFormatedBuyMoreMenu(player, shopItem), new OpenMenu(shopId, 0, MenuType.BUY_MORE_MENU, shopItem));

	}

	public void openSellMenu(Player player, String shopId, ShopItem shopItem) {

		utils.openInventory(player, main.buyers.get(player), sellMenu.getPlayerFormatedSellMenu(player, shopItem), new OpenMenu(shopId, 0, MenuType.SELL_MENU, shopItem));

	}

	public void openSellMoreMenu(Player player, String shopId, ShopItem shopItem) {

		utils.openInventory(player, main.buyers.get(player), sellMoreMenu.getPlayerFormatedSellMoreMenu(player, shopItem), new OpenMenu(shopId, 0, MenuType.SELL_MORE_MENU, shopItem));

	}

	public void closeGui(Player player) {

		Buyer buyer = main.buyers.get(player);

		if (buyer.hasOpenMenu()) {

			player.closeInventory();
			buyer.setOpenMenu(null);
		}
	}

	public void handleBuy(Player player, ShopItem shopItem, int amount, boolean silent) {

		TransactionResultType transactionResultType = TransactionResultType.SUCCESS;
		double price = shopItem.getBuyPrice() * amount;
		ItemStack redItem = shopItem.getRedItem().create();

		ShopPreTransactionEvent shopPreTransactionEvent = new ShopPreTransactionEvent(Action.BUY, shopItem, player, amount, price, silent);
		Bukkit.getPluginManager().callEvent(shopPreTransactionEvent);

		if (shopPreTransactionEvent.isCancelled()) {

			transactionResultType = TransactionResultType.FAILURE_CANCELLED;

		} else {

			price = shopPreTransactionEvent.getPrice();
			amount = shopPreTransactionEvent.getAmount();

			if (main.economy.has(player, price)) {

				if (utils.hasAvailableSlot(player, redItem, amount)) {

					main.economy.withdrawPlayer(player, price);
					int maxStackSize = redItem.getType().getMaxStackSize();
					int togive = amount;

					while (togive > 0) {

						if (togive > maxStackSize) {

							redItem.setAmount(maxStackSize);
							player.getInventory().addItem(redItem);
							togive -= maxStackSize;

						} else {

							redItem.setAmount(togive);
							player.getInventory().addItem(redItem);
							togive = 0;
						}
					}

					player.updateInventory();

					if (main.settingsCFG.getBoolean("Log.File") || main.settingsCFG.getBoolean("Log.Console")) {

						main.logger.info(main.messagesCFG.getString("Log.Buy").replace("%player%", player.getName()).replace("%amount%", amount + "").replace("%item%", utils.formatItemName(redItem)).replace("%price%", Utils.formatCurrency(price)));
					}

					if (!silent) {

						player.spigot().sendMessage(new Message(main.messagesCFG, "Buy").setPlaceHolderPlayer(player).replace("%amount%", amount + "").replace("%item%", utils.formatItemName(redItem)).replace("%price%", Utils.formatCurrency(price)).create());
					}
				} else {

					transactionResultType = TransactionResultType.FAILURE_FULL_INVENTORY;
					player.spigot().sendMessage(new Message(main.messagesCFG, "InventoryFull").setPlaceHolderPlayer(player).replace("%amount%", amount + "").replace("%item%", utils.formatItemName(redItem)).create());

				}
			} else {

				transactionResultType = TransactionResultType.FAILURE_NO_MONEY;
				player.spigot().sendMessage(new Message(main.messagesCFG, "NoMoney").setPlaceHolderPlayer(player).replace("%amount%", amount + "").replace("%item%", utils.formatItemName(redItem)).replace("%price%", Utils.formatCurrency(price)).create());

			}
		}
		TransactionResult shopTransactionResult = new TransactionResult(ShopManager.Action.BUY, transactionResultType, shopItem, player, amount, price, silent);
		ShopPostTransactionEvent shopPostTransactionEvent = new ShopPostTransactionEvent(shopTransactionResult);
		Bukkit.getPluginManager().callEvent(shopPostTransactionEvent);
	}

	public void handleSell(Player player, ShopItem shopItem, int amount, boolean silent) {

		TransactionResultType transactionResultType = TransactionResultType.SUCCESS;
		ItemStack redItem = shopItem.getRedItem().clone().create();
		double price = shopItem.getSellPrice() * amount;

		ShopPreTransactionEvent shopPreTransactionEvent = new ShopPreTransactionEvent(Action.SELL, shopItem, player, amount, price, silent);
		Bukkit.getPluginManager().callEvent(shopPreTransactionEvent);

		if (shopPreTransactionEvent.isCancelled()) {

			transactionResultType = TransactionResultType.FAILURE_CANCELLED;

		} else {

			price = shopPreTransactionEvent.getPrice();
			amount = shopPreTransactionEvent.getAmount();

			if (utils.getAllItem(player, redItem) >= amount) {

				utils.removeItem(player, redItem, amount);
				main.economy.depositPlayer(player, price);
				player.updateInventory();

				if (main.settingsCFG.getBoolean("Log.File") || main.settingsCFG.getBoolean("Log.Console")) {

					main.logger.info(main.messagesCFG.getString("Log.Sell").replace("%player%", player.getName()).replace("%amount%", amount + "").replace("%item%", utils.formatItemName(redItem)).replace("%price%", Utils.formatCurrency(price)));
				}

				if (!silent) {

					player.spigot().sendMessage(new Message(main.messagesCFG, "Sell").replace("%item%", utils.formatItemName(redItem)).replace("%amount%", amount + "").replace("%price%", price + "").create());

				}
			} else {

				transactionResultType = TransactionResultType.FAILURE_NO_ITEMS;
				player.spigot().sendMessage(new Message(main.messagesCFG, "NoNumberItem").replace("%item%", utils.formatItemName(redItem)).replace("%amount%", amount + "").create());

			}
		}

		TransactionResult transactionResult = new TransactionResult(Action.SELL, transactionResultType, shopItem, player, amount, price, silent);
		ShopPostTransactionEvent shopPostTransactionEvent = new ShopPostTransactionEvent(transactionResult);
		Bukkit.getPluginManager().callEvent(shopPostTransactionEvent);
	}

	public void handleSellAll(Player player, ShopItem shopItem, boolean silent) {

		TransactionResultType transactionResultType = TransactionResultType.SUCCESS;
		ItemStack redItem = shopItem.getRedItem().clone().create();
		int amount = utils.getAllItem(player, redItem);
		double price = shopItem.getSellPrice() * amount;

		ShopPreTransactionEvent shopPreTransactionEvent = new ShopPreTransactionEvent(Action.SELL_ALL, shopItem, player, amount, price, silent);
		Bukkit.getPluginManager().callEvent(shopPreTransactionEvent);

		if (shopPreTransactionEvent.isCancelled()) {

			transactionResultType = TransactionResultType.FAILURE_CANCELLED;

		} else {

			price = shopPreTransactionEvent.getPrice();
			amount = shopPreTransactionEvent.getAmount();

			if (amount != 0) {

				utils.removeItem(player, redItem, amount);
				main.economy.depositPlayer(player, price);
				player.updateInventory();

				if (main.settingsCFG.getBoolean("Log.File") || main.settingsCFG.getBoolean("Log.Console")) {

					main.logger.info(main.messagesCFG.getString("Log.Sell").replace("%player%", player.getName()).replace("%amount%", amount + "").replace("%item%", utils.formatItemName(redItem)).replace("%price%", Utils.formatCurrency(price)));
				}

				if (!silent) {

					player.spigot().sendMessage(new Message(main.messagesCFG, "SellAll").replace("%item%", utils.formatItemName(redItem)).replace("%amount%", amount + "").replace("%price%", price + "").create());

				}
			} else {

				transactionResultType = TransactionResultType.FAILURE_NO_ITEMS;
				player.spigot().sendMessage(new Message(main.messagesCFG, "NoItem").replace("%item%", utils.formatItemName(redItem)).create());

			}
		}

		TransactionResult transactionResult = new TransactionResult(Action.SELL_ALL, transactionResultType, shopItem, player, amount, price, silent);
		ShopPostTransactionEvent shopPostTransactionEvent = new ShopPostTransactionEvent(transactionResult);
		Bukkit.getPluginManager().callEvent(shopPostTransactionEvent);

	}

	public MainMenu getMainMenu() {
		return mainMenu;
	}

	public Menu getMenu() {
		return menu;
	}

	public BuyMenu getBuyMenu() {
		return buyMenu;
	}

	public BuyMoreMenu getBuyMoreMenu() {
		return buyMoreMenu;
	}

	public SellMenu getSellMenu() {
		return sellMenu;
	}

	public SellMoreMenu getSellMoreMenu() {
		return sellMoreMenu;
	}
}
