package fr.gamalta.redblock.shop.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.gamalta.lib.item.RedItem;
import fr.gamalta.redblock.shop.Shop;
import fr.gamalta.redblock.shop.gui.BuyMenu;
import fr.gamalta.redblock.shop.gui.BuyMoreMenu;
import fr.gamalta.redblock.shop.gui.MainMenu;
import fr.gamalta.redblock.shop.gui.Menu;
import fr.gamalta.redblock.shop.gui.MenuBuilder;
import fr.gamalta.redblock.shop.gui.OpenMenu;
import fr.gamalta.redblock.shop.gui.SellMenu;
import fr.gamalta.redblock.shop.gui.SellMoreMenu;
import fr.gamalta.redblock.shop.item.ButtonItem;
import fr.gamalta.redblock.shop.item.ShopItem;
import fr.gamalta.redblock.shop.utils.Buyer;
import fr.gamalta.redblock.shop.utils.type.ButtonType;
import fr.gamalta.redblock.shop.utils.type.ItemType;
import me.clip.placeholderapi.PlaceholderAPI;

public class onInventoryClickEvent implements Listener {
	
	private Shop main;
	
	public onInventoryClickEvent(Shop main) {
		
		this.main = main;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event) {
		
		Player player = (Player) event.getWhoClicked();
		Buyer buyer = main.buyers.get(player);
		Integer slot = event.getRawSlot();
		Inventory inventory = event.getInventory();

		if (buyer == null) {
			main.buyers.put(player, new Buyer(player));
		}

		if (buyer.hasOpenMenu()) {
			
			event.setCancelled(true);
			
			if (buyer.getLastGuiClick() == 0L || System.currentTimeMillis() - buyer.getLastGuiClick() > 250L) {
				
				buyer.setLastGuiClick(System.currentTimeMillis());
				OpenMenu openMenu = buyer.getOpenMenu();
				List<ButtonItem> buttons;
				List<ShopItem> items;
				HashMap<Integer, Integer> stacks;
				List<ButtonItem> newButton = new ArrayList<>();
				Boolean updateAmount = false;
				
				switch (openMenu.getMenuType()) {
					
					case MAIN_MENU:
						
						MainMenu mainMenu = main.getShopManager().getMainMenu();
						buttons = mainMenu.getButtons();
						items = mainMenu.getItems();
						
						for (ShopItem shopItem : items) {
							
							if (shopItem.getSlot() == slot) {
								
								main.getShopManager().openShop(player, shopItem.getShopId());
								break;
							}
						}
						
						for (ButtonItem buttonItem : buttons) {
							
							if (buttonItem.getSlot() == slot) {
								
								if (buttonItem.getButtonType() == ButtonType.EXIT) {
									
									main.getShopManager().closeGui(player);
								}
								break;
							}
						}
						break;
					
					case MENU:
						
						Menu menu = main.getShopManager().getMenu();
						MenuBuilder menuBuilder = menu.getMenus().get(openMenu.getShopId());
						buttons = menuBuilder.getButtons();
						items = menuBuilder.getItems();
						boolean isItems = false;
						
						for (ShopItem shopItem : items) {
							
							if (shopItem.getPage() == openMenu.getPage() && shopItem.getSlot() == slot) {
								
								isItems = true;
								switch (event.getClick()) {
									
									case LEFT:
										
										if (shopItem.getItemType() == ItemType.BUY_ONLY || shopItem.getItemType() == ItemType.NORMAL) {
											
											main.getShopManager().openBuyMenu(player, openMenu.getShopId() + ".Buy", shopItem);
										}
										
										break;
									
									case RIGHT:
										if (shopItem.getItemType() == ItemType.SELL_ONLY || shopItem.getItemType() == ItemType.NORMAL) {
											
											main.getShopManager().openSellMenu(player, openMenu.getShopId() + ".Sell", shopItem);
										}
										break;
									
									case MIDDLE:
										if (shopItem.getItemType() == ItemType.SELL_ONLY || shopItem.getItemType() == ItemType.NORMAL) {
											
											main.getShopManager().handleSellAll(player, shopItem, false);
											main.getShopManager().closeGui(player);
											
										}
										break;
									default:
										break;
								}
								break;
							}
							
						}
						
						if (!isItems) {
							
							for (ButtonItem buttonItem : buttons) {
								
								if (buttonItem.getPage() == openMenu.getPage() && buttonItem.getSlot() == slot) {
									
									switch (buttonItem.getButtonType()) {
										
										case EXIT:
											
											main.getShopManager().openMainMenu(player);
											break;
										
										case PREVIOUS:
											
											main.getShopManager().openShop(player, openMenu.getShopId(), openMenu.getPage() - 1);
											break;
										
										case NEXT:
											
											main.getShopManager().openShop(player, openMenu.getShopId(), openMenu.getPage() + 1);
											break;
										default:
											break;
									}
									break;
								}
							}
						}
						break;
					
					case BUY_MENU:
						
						BuyMenu buyMenu = main.getShopManager().getBuyMenu();
						buttons = buyMenu.getButtons();
						
						for (ButtonItem buttonItem : buttons) {
							
							if (buttonItem.getSlot() == slot) {
								
								int mainSlot = buyMenu.getItemSlot();
								ItemStack item = inventory.getItem(mainSlot);
								int amount = 1;
								
								if (item != null) {
									
									amount = item.getAmount();
								}
								
								switch (buttonItem.getButtonType()) {
									
									case EXIT:
										
										main.getShopManager().openShop(player, openMenu.getShopId().replace(".Buy", ""));
										break;
									
									case BUY_MORE:
										
										main.getShopManager().openBuyMoreMenu(player, openMenu.getShopId() + "More", openMenu.getMainItem());
										break;
									
									case SET_MAX_STACK_SIZE:
										
										updateAmount = true;
										if (amount != item.getType().getMaxStackSize() && item != null) {
											item.setAmount(item.getType().getMaxStackSize());
											inventory.setItem(mainSlot, item);
										}
										break;
									
									case SET_1:
										
										updateAmount = true;
										if (amount != 1 && item != null) {
											
											item.setAmount(1);
											inventory.setItem(mainSlot, item);
										}
										break;
									
									case ADD_10:
										
										updateAmount = true;
										if (amount < item.getType().getMaxStackSize() - 9 && item != null) {
											
											item.setAmount(amount + 10);
											inventory.setItem(mainSlot, item);
										}
										break;
									
									case ADD_1:
										
										updateAmount = true;
										if (amount < item.getType().getMaxStackSize() && item != null) {
											
											item.setAmount(amount + 1);
											inventory.setItem(mainSlot, item);
										}
										break;
									
									case REMOVE_10:
										
										updateAmount = true;
										if (amount > 10 && item != null) {
											
											item.setAmount(amount - 10);
											inventory.setItem(mainSlot, item);
										}
										break;
									
									case REMOVE_1:
										
										updateAmount = true;
										if (amount > 1 && item != null) {
											
											item.setAmount(amount - 1);
											inventory.setItem(mainSlot, item);
										}
										break;
									
									case BUY_VALIDATE:
										
										main.getShopManager().handleBuy(player, openMenu.getMainItem(), amount, false);
										main.getShopManager().closeGui(player);
										break;
									default:
										break;
								}
								
								int maxStackSize = item.getType().getMaxStackSize();
								
								if (updateAmount && maxStackSize != 1) {
									
									amount = item.getAmount();
									
									for (ButtonItem btnItem : buttons) {
										
										ButtonType buttonType = btnItem.getButtonType();
										
										if (buttonType.canEdit()) {
											
											inventory.setItem(btnItem.getSlot(), null);
											
											if (amount > 1) {
												
												if (buttonType == ButtonType.SET_1) {
													
													newButton.add(btnItem);
												}
												
												if (buttonType == ButtonType.REMOVE_1) {
													
													newButton.add(btnItem);
												}
												
												if (amount > 10) {
													
													if (buttonType == ButtonType.REMOVE_10) {
														
														newButton.add(btnItem);
													}
													
												}
											}
											
											if (amount < maxStackSize) {
												
												if (buttonType == ButtonType.SET_MAX_STACK_SIZE) {
													
													newButton.add(btnItem);
												}
												if (buttonType == ButtonType.ADD_1) {
													
													newButton.add(btnItem);
												}
												
												if (amount < maxStackSize - 9) {
													if (buttonType == ButtonType.ADD_10) {
														
														newButton.add(btnItem);
													}
												}
												
											}
										}
										
									}
									
									for (ButtonItem btnItem : newButton) {
										
										RedItem redItem = btnItem.getRedItem().clone();
										redItem.setName(PlaceholderAPI.setPlaceholders(player, redItem.getName()));
										redItem.setLores(PlaceholderAPI.setPlaceholders(player, redItem.getLores()));
										
										if (btnItem.getButtonType() == ButtonType.SET_MAX_STACK_SIZE) {
											
											redItem.replace("%max_stack_size%", item.getType().getMaxStackSize() + "");
											redItem.setAmount(item.getType().getMaxStackSize());
										}
										
										inventory.setItem(btnItem.getSlot(), redItem.create());
									}
								}
								break;
							}
						}
						
						break;
					
					case BUY_MORE_MENU:
						
						BuyMoreMenu buyMoreMenu = main.getShopManager().getBuyMoreMenu();
						buttons = buyMoreMenu.getButtons();
						stacks = buyMoreMenu.getStacks();
						
						if (stacks.containsKey(slot)) {
							
							ShopItem item = openMenu.getMainItem();
							main.getShopManager().handleBuy(player, item, stacks.get(slot) * item.getRedItem().getMaterial().getMaxStackSize(), false);
							main.getShopManager().closeGui(player);
						} else {
							
							for (ButtonItem buttonItem : buttons) {
								
								if (buttonItem.getSlot() == slot) {
									
									if (buttonItem.getButtonType() == ButtonType.EXIT) {
										
										main.getShopManager().openShop(player, openMenu.getShopId().replace(".BuyMore", ""));
									}
									break;
								}
							}
						}
						break;
					
					case SELL_MENU:
						
						SellMenu sellMenu = main.getShopManager().getSellMenu();
						buttons = sellMenu.getButtons();
						
						for (ButtonItem buttonItem : buttons) {
							
							if (buttonItem.getSlot() == slot) {
								
								int mainSlot = sellMenu.getItemSlot();
								ItemStack item = inventory.getItem(mainSlot);
								int amount = item.getAmount();
								
								switch (buttonItem.getButtonType()) {
									
									case EXIT:
										
										main.getShopManager().openShop(player, openMenu.getShopId().replace(".Sell", ""));
										break;
									case SELL_ALL:
										
										main.getShopManager().handleSellAll(player, openMenu.getMainItem(), false);
										main.getShopManager().closeGui(player);
										break;
									case SET_MAX_STACK_SIZE:
										
										updateAmount = true;
										if (amount != item.getType().getMaxStackSize() && item != null) {
											item.setAmount(item.getType().getMaxStackSize());
											inventory.setItem(mainSlot, item);
										}
										break;
									
									case SET_1:
										
										updateAmount = true;
										if (amount != 1 && item != null) {
											
											item.setAmount(1);
											inventory.setItem(mainSlot, item);
										}
										break;
									
									case ADD_10:
										
										updateAmount = true;
										if (amount < item.getType().getMaxStackSize() - 9 && item != null) {
											
											item.setAmount(amount + 10);
											inventory.setItem(mainSlot, item);
										}
										break;
									
									case ADD_1:
										
										updateAmount = true;
										if (amount < item.getType().getMaxStackSize() && item != null) {
											
											item.setAmount(amount + 1);
											inventory.setItem(mainSlot, item);
										}
										break;
									
									case REMOVE_10:
										
										updateAmount = true;
										if (amount > 10 && item != null) {
											
											item.setAmount(amount - 10);
											inventory.setItem(mainSlot, item);
										}
										break;
									
									case REMOVE_1:
										
										updateAmount = true;
										if (amount > 1 && item != null) {
											
											item.setAmount(amount - 1);
											inventory.setItem(mainSlot, item);
										}
										break;
									
									case SELL_MORE:
										
										main.getShopManager().openSellMoreMenu(player, openMenu.getShopId() + "More", openMenu.getMainItem());
										break;
									
									case SELL_VALIDATE:
										
										main.getShopManager().handleSell(player, openMenu.getMainItem(), amount, false);
										main.getShopManager().closeGui(player);
										break;
									default:
										break;
								}
								
								int maxStackSize = item.getType().getMaxStackSize();
								
								if (updateAmount && maxStackSize != 1) {
									
									amount = item.getAmount();
									
									for (ButtonItem btnItem : buttons) {
										
										ButtonType buttonType = btnItem.getButtonType();
										
										if (buttonType.canEdit()) {
											
											inventory.setItem(btnItem.getSlot(), null);
											
											if (amount > 1) {
												
												if (buttonType == ButtonType.SET_1) {
													
													newButton.add(btnItem);
												}
												
												if (buttonType == ButtonType.REMOVE_1) {
													
													newButton.add(btnItem);
												}
												
												if (amount > 10) {
													
													if (buttonType == ButtonType.REMOVE_10) {
														
														newButton.add(btnItem);
													}
													
												}
											}
											
											if (amount < maxStackSize) {
												
												if (buttonType == ButtonType.SET_MAX_STACK_SIZE) {
													
													newButton.add(btnItem);
												}
												if (buttonType == ButtonType.ADD_1) {
													
													newButton.add(btnItem);
												}
												
												if (amount < maxStackSize - 9) {
													if (buttonType == ButtonType.ADD_10) {
														
														newButton.add(btnItem);
													}
												}
												
											}
										} else {
											
											newButton.add(btnItem);
											
										}
										
									}
									
									for (ButtonItem btnItem : newButton) {
										
										RedItem redItem = btnItem.getRedItem().clone();
										redItem.setName(PlaceholderAPI.setPlaceholders(player, redItem.getName()));
										redItem.setLores(PlaceholderAPI.setPlaceholders(player, redItem.getLores()));
										
										if (btnItem.getButtonType() == ButtonType.SET_MAX_STACK_SIZE) {
											redItem.replace("%max_stack_size%", item.getType().getMaxStackSize() + "");
											redItem.setAmount(item.getType().getMaxStackSize());
										}
										
										inventory.setItem(btnItem.getSlot(), redItem.create());
									}
								}
								break;
							}
						}
						break;
					
					case SELL_MORE_MENU:
						
						SellMoreMenu sellMoreMenu = main.getShopManager().getSellMoreMenu();
						buttons = sellMoreMenu.getButtons();
						stacks = sellMoreMenu.getStacks();
						
						if (stacks.containsKey(slot)) {
							
							ShopItem item = openMenu.getMainItem();
							main.getShopManager().handleSell(player, item, stacks.get(slot) * item.getRedItem().getMaterial().getMaxStackSize(), false);
							main.getShopManager().closeGui(player);
							
						} else {
							for (ButtonItem buttonItem : buttons) {
								
								if (buttonItem.getSlot() == slot) {
									
									if (buttonItem.getButtonType() == ButtonType.EXIT) {
										
										main.getShopManager().openShop(player, openMenu.getShopId().replace(".SellMore", ""));
									}
									break;
								}
							}
						}
						
						break;
				}
			}
		}
	}
}
