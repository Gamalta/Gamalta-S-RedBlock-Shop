package fr.gamalta.redblock.shop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.gamalta.lib.config.Configuration;
import fr.gamalta.lib.item.RedItem;
import fr.gamalta.lib.logger.LogFormatter;
import fr.gamalta.redblock.customitems.api.CustomItemAPI;
import fr.gamalta.redblock.shop.commands.shopCmd;
import fr.gamalta.redblock.shop.listeners.onInventoryClickEvent;
import fr.gamalta.redblock.shop.listeners.onInventoryCloseEvent;
import fr.gamalta.redblock.shop.listeners.onPlayerJoinEvent;
import fr.gamalta.redblock.shop.listeners.onPlayerQuitEvent;
import fr.gamalta.redblock.shop.utils.Buyer;
import fr.gamalta.redblock.shop.utils.type.ButtonType;
import net.milkbowl.vault.economy.Economy;

public class Shop extends JavaPlugin {
	
	private static Shop shop;
	
	public static Shop getInstance() {
		
		return shop;
	}
	
	public String parentFileName = "Shop";
	public Configuration settingsCFG = new Configuration(this, parentFileName, "Settings");
	public Configuration messagesCFG = new Configuration(this, parentFileName, "Messages");
	public HashMap<ButtonType, RedItem> buttons;
	public HashMap<Player, Buyer> buyers = new HashMap<>();
	public List<String> bannedWorlds = new ArrayList<>();
	public Logger logger = getLogger();
	public FileHandler fileHandler;
	public Economy economy;
	
	private ShopManager shopManager;
	
	public ShopManager getShopManager() {
		return shopManager;
	}
	
	public void initButtons() {
		
		buttons = new HashMap<>();
		
		for (ButtonType buttonType : ButtonType.values()) {
			
			if (settingsCFG.contains("Buttons." + buttonType.name())) {
				
				if (settingsCFG.contains("Buttons." + buttonType.name() + ".CustomItem")) {
					
					buttons.put(buttonType, CustomItemAPI.getRedItemById(settingsCFG.getString("Buttons." + buttonType.name() + ".CustomItem")));
					
				} else {
					
					buttons.put(buttonType, new RedItem(this, settingsCFG, "Buttons." + buttonType.name()));
					
				}
			}
		}
	}
	
	private void initCommands() {

		getCommand("shop").setExecutor(new shopCmd(this));
	}
	
	private void initEconomy() {
		
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		
		if (economyProvider == null) {
			
			logger.warning("Vault economy or economy plugin not found, disabling the plugin.");
			getServer().getPluginManager().disablePlugin(this);
			
		} else {
			
			economy = economyProvider.getProvider();
			logger.info("Vault economy support enabled.");
		}
	}
	
	private void initListeners() {
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new onPlayerJoinEvent(this), this);
		pm.registerEvents(new onPlayerQuitEvent(this), this);
		pm.registerEvents(new onInventoryCloseEvent(this), this);
		pm.registerEvents(new onInventoryClickEvent(this), this);
		
	}
	
	public void initLogger() {
		
		if (settingsCFG.getBoolean("Log.File")) {
			
			File log = new File("plugins/RedBlock/Log/Shop.log");
			
			if (!log.getParentFile().exists()) {
				log.getParentFile().mkdir();
			}
			
			if (!log.exists()) {
				
				try {
					log.createNewFile();
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
			
			try {
				
				fileHandler = new FileHandler(log.getAbsolutePath(), true);
				
			} catch (IOException iOException) {
				
				iOException.printStackTrace();
				return;
			}
			
			fileHandler.setFormatter(new LogFormatter());
			logger.addHandler(fileHandler);
			logger.setUseParentHandlers(false);
			logger.info("Enabling Shop v1.0");
			
			if (settingsCFG.getBoolean("Log.Console")) {
				
				logger.setUseParentHandlers(true);
			}
		}
	}
	
	public void initShops() {
		
		shop = this;
		shopManager = new ShopManager(this);
		shopManager.init();
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			buyers.put(player, new Buyer(player));
		}
	}
	
	@Override
	public void onDisable() {
		
		if (buyers != null) {
			
			for (Buyer buyer : buyers.values()) {
				
				if (buyer.hasOpenMenu()) {
					buyer.getPlayer().closeInventory();
				}
			}
		}
		
		if (fileHandler != null) {
			
			fileHandler.close();
			getLogger().removeHandler(fileHandler);
		}
	}
	
	@Override
	public void onEnable() {
		
		initButtons();
		initShops();
		initLogger();
		initEconomy();
		initCommands();
		initListeners();
		
		bannedWorlds = settingsCFG.getStringList("BannedWorlds");
	}
}
