package com.github.sanctum.labyrinth.gui.basalt;

public class InventoryProperties {

	private int size;
	private String title;
	private InventoryFormat type;

	public InventoryProperties(InventoryFormat format, String title) {
		this.title = title;
		this.type = format;
		this.size = 4;// decide size with new enum
	}

	public InventoryFormat getFormat() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public int getSize() {
		return size;
	}
}
