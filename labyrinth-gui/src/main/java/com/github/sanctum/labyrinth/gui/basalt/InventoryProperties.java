package com.github.sanctum.labyrinth.gui.basalt;

import org.jetbrains.annotations.NotNull;

public class InventoryProperties {

	private final InventoryFormat type;
	private final boolean shareable;
	private InventorySize size;
	private String title;

	public InventoryProperties(@NotNull InventoryFormat format, @NotNull InventorySize size, @NotNull String title, boolean shareable) {
		this.title = title;
		this.type = format;
		this.size = size;
		this.shareable = shareable;
	}

	public InventoryFormat getFormat() {
		return type;
	}

	public InventorySize getSize() {
		return size;
	}

	public String getTitle() {
		return title;
	}

	public boolean isShareable() {
		return shareable;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSize(InventorySize size) {
		this.size = size;
	}
}
