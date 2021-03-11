package com.github.sanctum.labyrinth.gui.builder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * An object designed to provide quick and easy filler to the remaining slots
 * of your paginated GUI.
 */
public class BorderElement {

	private final PaginatedBuilder builder;

	private ItemStack materialB;

	private ItemStack materialF;

	protected BorderElement(PaginatedBuilder builder) {
		this.builder = builder;
	}

	/**
	 * Set the border to surround the given menu items by material.
	 *
	 * @param material The material to stylize with
	 * @return A border element.
	 */
	public BorderElement setBorderType(Material material) {
		this.materialB = new ItemStack(material);
		return this;
	}

	/**
	 * Set the material to fill remaining empty slots with
	 *
	 * @param material The material to stylize with.
	 * @return A border element.
	 */
	public BorderElement setFillType(Material material) {
		this.materialF = new ItemStack(material);
		return this;
	}

	/**
	 * Set the border to surround the given menu items by specific ItemStack
	 *
	 * @param material The specific ItemStack to stylize with.
	 * @return A border element.
	 */
	public BorderElement setBorderType(ItemStack material) {
		this.materialB = material;
		return this;
	}

	/**
	 * Set the specific ItemStack to fill remaining empty slots with.
	 *
	 * @param material The specific ItemStack to stylize with.
	 * @return A border element.
	 */
	public BorderElement setFillType(ItemStack material) {
		this.materialF = material;
		return this;
	}

	/**
	 * Complete the changes to the border element and convert back to a {@link PaginatedBuilder}
	 *
	 * @return The previous paginated builder with the newly applied values.
	 */
	public PaginatedBuilder fill() {
		if (this.materialF != null) {
			builder.fill = this.materialF;
		}
		builder.border = this.materialB;
		return builder;
	}

}
