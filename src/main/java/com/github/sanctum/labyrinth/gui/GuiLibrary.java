package com.github.sanctum.labyrinth.gui;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Companion class to all menus. This is needed to pass information across the entire
 *  menu system no matter how many inventories are opened or closed.
 *
 *  Each player has one of these objects, and only one.
 */
public class GuiLibrary {
	private final Player viewer;
	private final UUID viewerID;
	private String data;
	private String data2;
	
	public GuiLibrary(Player p) {
		this.viewer = p;
		this.viewerID = p.getUniqueId();
	}

	public Player getViewer() { return viewer; }
	
	public UUID getUUID() {
		return viewerID;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public String getData2() {
		return data2;
	}
}
