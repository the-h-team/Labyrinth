package com.github.sanctum.labyrinth.library;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An interface used to mark items for removal within an {@link ItemCompost}
 *
 * <pre>
 *     Example:
 *
 *     {@code public class ItemRemover implements ItemMatcher {
 *        @Override
 *        public boolean compares(ItemStack item) {
 * 		    ItemStack myComparison = Items.edit().setType(Material.GUNPOWDER).setTitle("Im an item").build();
 * 		    return item.isSimilar(myComparison);
 *        }
 * }}
 *
 *
 * </pre>
 */
@FunctionalInterface
public interface ItemMatcher {

	@Deprecated
	boolean compares(ItemStack item);

	default boolean comparesTo(@NotNull ItemStack item) {
		return compares(item);
	}

}
