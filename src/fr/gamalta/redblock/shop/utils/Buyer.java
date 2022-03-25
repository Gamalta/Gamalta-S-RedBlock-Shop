package fr.gamalta.redblock.shop.utils;

import org.bukkit.entity.Player;

import fr.gamalta.redblock.shop.gui.OpenMenu;

public class Buyer {

	private Player player;
	private long lastGuiClick;
	private OpenMenu openMenu;
	private boolean switchingGui;

	public Buyer(Player player) {

		this.player = player;
		lastGuiClick = 0L;
	}

	public Player getPlayer() {

		return player;
	}

	public long getLastGuiClick() {

		return lastGuiClick;
	}

	public OpenMenu getOpenMenu() {

		return openMenu;
	}

	public boolean isSwitchingGui() {

		return switchingGui;
	}

	public boolean hasOpenMenu() {

		return (openMenu != null);
	}

	public void setLastGuiClick(long lastGuiClick) {
		this.lastGuiClick = lastGuiClick;
	}

	public void setOpenMenu(OpenMenu openMenu) {
		this.openMenu = openMenu;
	}

	public void setSwitchingGui(boolean switchingGui) {
		this.switchingGui = switchingGui;
	}
}
