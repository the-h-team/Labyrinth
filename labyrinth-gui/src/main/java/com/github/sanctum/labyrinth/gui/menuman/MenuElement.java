/*
 *  Copyright 2021 ms5984 (Matt) <https://github.com/ms5984>
 *
 *  This file is part of MenuMan, a module of Labyrinth.
 *
 *  MenuMan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either getServerVersion 3 of the
 *  License, or (at your option) any later getServerVersion.
 *
 *  MenuMan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.sanctum.labyrinth.gui.menuman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Encapsulation to promote lazy-initialization of ItemStacks
 */
public final class MenuElement {
	/**
	 * Initial ItemStack. May or may not be styled.
	 */
	protected ItemStack baseItem;
	/**
	 * A custom title for the final ItemStack.
	 */
	protected String displayName;
	/**
	 * A custom description for the ItemStack.
	 */
	protected List<String> lore;

	/**
	 * Specify MenuElement with no initial styling.
	 *
	 * @param baseItem ItemStack to copy
	 */
	protected MenuElement(ItemStack baseItem) {
		this.baseItem = baseItem;
	}

	/**
	 * Specify MenuElement with preconfigured styling.
	 *
	 * @param baseItem ItemStack to copy
	 * @param text     custom text
	 * @param lore     custom lore
	 */
	protected MenuElement(ItemStack baseItem, String text, String... lore) {
		this.baseItem = baseItem;
		this.displayName = text;
		this.lore = new ArrayList<>(Arrays.asList(lore));
	}

	/**
	 * Generate the final ItemStack, styled if needed.
	 *
	 * @return generated ItemStack
	 */
	@SuppressWarnings("ConstantConditions")
	public ItemStack generateComplete() {
		if (displayName != null || lore != null) {
			final ItemStack finalItem = new ItemStack(baseItem);
			final ItemMeta meta = finalItem.getItemMeta();
			if (displayName != null) {
				meta.setDisplayName(displayName);
			}
			if (lore != null && !lore.isEmpty()) {
				meta.setLore(lore);
			}
			finalItem.setItemMeta(meta);
			return finalItem;
		}
		return baseItem;
	}
}
