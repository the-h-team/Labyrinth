package com.github.sanctum.labyrinth.gui.builder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BorderElement {

	private final PaginatedBuilder builder;

	private ItemStack materialB;

	private ItemStack materialF;

	protected BorderElement(PaginatedBuilder builder) {
		this.builder = builder;
	}

	public BorderElement setBorderType(Material material) {
		this.materialB = new ItemStack(material);
		return this;
	}

	public BorderElement setFillType(Material material) {
		this.materialF = new ItemStack(material);
		return this;
	}

	public BorderElement setBorderType(ItemStack material) {
		this.materialB = material;
		return this;
	}

	public BorderElement setFillType(ItemStack material) {
		this.materialF = material;
		return this;
	}

	public PaginatedBuilder fill() {
		if (this.materialF != null) {
			builder.fill = this.materialF;
		}
		builder.border = this.materialB;
		return builder;
	}

}
