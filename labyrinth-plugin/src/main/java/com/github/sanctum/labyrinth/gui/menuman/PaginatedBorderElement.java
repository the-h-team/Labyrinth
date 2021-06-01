package com.github.sanctum.labyrinth.gui.menuman;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * An object designed to provide quick and easy filler to the remaining slots
 * of your paginated GUI.
 */
public class PaginatedBorderElement<T> {

	private final PaginatedBuilder<T> builder;

	private ItemStack materialB;

	private ItemStack materialF;

	protected PaginatedBorderElement(PaginatedBuilder<T> builder) {
		this.builder = builder;
	}

	/**
	 * Set the border to surround the given menu items by material.
	 *
	 * @param material The material to stylize with
	 * @return A border element.
	 */
	public PaginatedBorderElement<T> setBorderType(Material material) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		this.materialB = item;
		return this;
	}

	/**
	 * Set the material to fill remaining empty slots with
	 *
	 * @param material The material to stylize with.
	 * @return A border element.
	 */
	public PaginatedBorderElement<T> setFillType(Material material) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		this.materialF = item;
		return this;
	}

	/**
	 * Set the border to surround the given menu items by specific ItemStack
	 *
	 * @param material The specific ItemStack to stylize with.
	 * @return A border element.
	 */
	public PaginatedBorderElement<T> setBorderType(ItemStack material) {
		this.materialB = material;
		return this;
	}

	/**
	 * Set the specific ItemStack to fill remaining empty slots with.
	 *
	 * @param material The specific ItemStack to stylize with.
	 * @return A border element.
	 */
	public PaginatedBorderElement<T> setFillType(ItemStack material) {
		this.materialF = material;
		return this;
	}

	/**
	 * Complete the changes to the border element and convert back to a {@link com.github.sanctum.labyrinth.gui.builder.PaginatedBuilder}
	 *
	 * @return The previous paginated builder with the newly applied values.
	 */
	public PaginatedBuilder<T> build() {
		if (this.materialF != null) {
			builder.FILLER_ITEM = this.materialF;
		}
		builder.BORDER_ITEM = this.materialB;
		return builder;
	}

}
