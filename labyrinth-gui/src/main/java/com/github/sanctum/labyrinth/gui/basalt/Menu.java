package com.github.sanctum.labyrinth.gui.basalt;

import org.jetbrains.annotations.NotNull;

public class Menu {

	private final InventoryProperties properties;
	private  InventoryContainer container;

	public Menu(InventoryProperties properties) {
		this.properties = properties;
	}

	public InventoryContainer getContainer() {
		if (getProperties().isShareable()) {
			// new instance
		}
		return null; // cached instance
	}

	public @NotNull InventoryProperties getProperties() {
		return properties;
	}

}
