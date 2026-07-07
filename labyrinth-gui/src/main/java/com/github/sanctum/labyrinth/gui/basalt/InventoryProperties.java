package com.github.sanctum.labyrinth.gui.basalt;

import com.github.sanctum.labyrinth.formatting.string.FormattedString;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class InventoryProperties {

	private final InventoryFormat type;
	private InventoryContainer container;
	private InventoryModificationTask processor;
	private final boolean shareable;
	private boolean livemeta;
	private InventorySize size;
	private String title, id;
	private Inventory passthrough;

	public InventoryProperties(@NotNull Inventory inventory, @NotNull String title, @NotNull String id, boolean shareable) {
		this.title = title;
		this.id = id;
		this.type = InventoryFormat.NORMAL;
		this.size = InventorySize.valueOf(inventory.getSize());
		this.shareable = shareable;
		this.passthrough = inventory;
	}

	public InventoryProperties(@NotNull InventoryFormat format, @NotNull InventorySize size, @NotNull String title, @NotNull String id, boolean shareable) {
		this.title = title;
		this.id = id;
		this.type = format;
		this.size = size;
		this.shareable = shareable;
	}

	public String getId() {
		return id;
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

	public boolean isLivemeta() {
		return livemeta;
	}

	public InventoryModificationTask getInventoryModificationTask() {
		return processor;
	}

	public InventoryProperties setIsLivemeta(boolean livemeta) {
		this.livemeta = livemeta;
		return this;
	}

	public InventoryProperties setTitle(String title) {
		this.title = title;
		return this;
	}

	public InventoryProperties setSize(InventorySize size) {
		this.size = size;
		return this;
	}

	public InventoryProperties setProcessor(InventoryModificationTask processor) {
		this.processor = processor;
		return this;
	}

	protected @NotNull Inventory pass() {
		if (this.container == null) throw new IllegalStateException("Inventory Holder not specified!");
		return passthrough != null ? passthrough : Bukkit.createInventory(container, getSize().getSize(), new FormattedString(getTitle()).translate().color().get());
	}

	// set the pointer to the inventory holder used for event shit
	protected InventoryProperties hold(InventoryContainer inventoryContainer) {
		this.container = inventoryContainer;
		return this;
	}
}
