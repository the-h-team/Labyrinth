package com.github.sanctum.labyrinth.gui.basalt;

public class Menu {

	private final boolean isSharable;
	private final InventoryProperties properties;

	public Menu(String title, InventoryFormat format, boolean sharable) {
		this.isSharable = sharable;
		this.properties = new InventoryProperties(format, title);
	}

	public InventoryContainer getContainer() {
		if (isSharable) {
			// new instance
		}
		return null; // cached instance
	}

}
