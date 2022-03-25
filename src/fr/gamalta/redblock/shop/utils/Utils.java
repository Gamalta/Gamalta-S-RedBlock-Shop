package fr.gamalta.redblock.shop.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.gamalta.lib.item.RedItem;
import fr.gamalta.redblock.customitems.api.CustomItemAPI;
import fr.gamalta.redblock.shop.Shop;
import fr.gamalta.redblock.shop.gui.OpenMenu;

public class Utils {

	public static String formatCurrency(double money) {

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.getDefault());
		decimalFormatSymbols.setDecimalSeparator(',');
		decimalFormatSymbols.setGroupingSeparator(' ');
		DecimalFormat decimalFormat = new DecimalFormat("###,##0.##", decimalFormatSymbols);
		decimalFormat.setMaximumIntegerDigits(12);
		decimalFormat.setMaximumFractionDigits(2);

		String str = decimalFormat.format(money);

		return str;
	}

	public String formatItemName(RedItem redItem) {

		if (redItem.getName() != null) {

			return redItem.getName();

		} else {

			return (redItem.getMaterial() + "").toLowerCase().replace("_", " ");

		}
	}

	public String formatItemName(ItemStack redItem) {

		if (redItem != null) {

			ItemMeta redItemMeta = redItem.getItemMeta();

			if (redItemMeta.hasDisplayName()) {

				return redItemMeta.getDisplayName();

			} else {

				return CustomItemAPI.getType(redItem);
			}
		}
		return "";
	}

	public boolean hasAvailableSlot(Player player, ItemStack item, int amount) {

		int available = 0;
		int emptyslot = 0;
		int maxStackSize = item.getType().getMaxStackSize();

		if (player != null && amount > 0) {

			for (ItemStack itemStack : player.getInventory().getStorageContents()) {

				if (itemStack == null) {
					emptyslot++;

				} else if (itemStack.isSimilar(item)) {

					available += (64 - itemStack.getAmount());
				}
			}

			if (item.getType().getMaxDurability() > 0) {

				return emptyslot >= amount;

			} else {

				return (emptyslot * maxStackSize + (Math.min(available, maxStackSize))) >= amount;

			}
		} else {

			return false;
		}
	}

	public boolean compareInventories(Inventory inventory1, Inventory inventory2) {

		if (inventory1.getType() != inventory2.getType()) {

			return false;
		}

		ItemStack[] itemStacks1 = inventory1.getContents();
		ItemStack[] itemStacks2 = inventory2.getContents();

		if (itemStacks1.length != itemStacks2.length) {

			return false;
		}

		for (byte b = 0; b < itemStacks1.length; b++) {

			if (itemStacks1[b] != null || itemStacks2[b] != null) {

				if ((itemStacks1[b] == null && itemStacks2[b] != null) || (itemStacks1[b] != null && itemStacks2[b] == null) || !itemStacks1[b].isSimilar(itemStacks2[b])) {

					return false;
				}
			}
		}
		return true;
	}

	public void openInventory(Player player, Buyer buyer, Inventory inventory, OpenMenu openMenu) {

		buyer.setSwitchingGui(true);

		Bukkit.getScheduler().runTaskLater(Shop.getInstance(), () -> {

			player.openInventory(inventory);

			Bukkit.getScheduler().runTaskLater(Shop.getInstance(), () -> {

				if (compareInventories(inventory, player.getOpenInventory().getTopInventory())) {

					buyer.setSwitchingGui(false);
					buyer.setOpenMenu(openMenu);

				} else {

					buyer.setOpenMenu(null);
					player.closeInventory();
					updateInventory(player, buyer);
				}

			}, 1L);

		}, 1L);
	}

	public void updateInventory(Player player, Buyer buyer) {

		Bukkit.getScheduler().runTaskLater(Shop.getInstance(), () -> {

			if (buyer.hasOpenMenu()) {

				player.updateInventory();
			}

		}, 1L);
	}

	public int getAllItem(Player player, ItemStack itemStack) {

		ItemStack[] contents = player.getInventory().getContents();
		String string = CustomItemAPI.getType(itemStack);
		int amount = 0;

		for (ItemStack item : contents) {

			if (item != null && CustomItemAPI.getType(item).equals(string)) {

				amount += item.getAmount();

			}
		}

		return amount;
	}

	public void removeItem(Player player, ItemStack itemStack, int i) {

		ItemStack[] contents = player.getInventory().getContents();
		String string = CustomItemAPI.getType(itemStack);
		int slot = 0;
		int amount = new Integer(i);

		for (ItemStack item : contents) {

			if (item != null && CustomItemAPI.getType(item).equals(string)) {

				if (amount >= item.getMaxStackSize()) {

					player.getInventory().setItem(slot, null);
					amount -= item.getAmount();

				} else {

					item.setAmount(item.getAmount() - amount);
					player.getInventory().setItem(slot, item);
					amount = 0;

				}
			}

			slot++;
		}

		player.updateInventory();
	}
}
