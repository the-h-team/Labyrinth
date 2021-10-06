package com.github.sanctum.labyrinth.library;

import org.bukkit.inventory.ItemStack;

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

	boolean compares(ItemStack item);

}
