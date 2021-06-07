package com.github.sanctum.labyrinth.gui.printer;

import java.util.function.Supplier;
import org.bukkit.inventory.ItemStack;

public class ItemBuilder {

	protected AnvilItemClick click;

	protected ItemStack item;

	protected ItemBuilder() {
	}

	protected ItemBuilder(ItemStack item, AnvilItemClick click) {
		this.click = click;
		this.item = item;
	}

	public static ItemBuilder next() {
		return new ItemBuilder();
	}

	public static ItemBuilder next(Supplier<ItemStack> item, AnvilItemClick click) {
		return new ItemBuilder(item.get(), click);
	}

	public void setClick(AnvilItemClick click) {
		this.click = click;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public ItemStack getItem() {
		return item;
	}
}
