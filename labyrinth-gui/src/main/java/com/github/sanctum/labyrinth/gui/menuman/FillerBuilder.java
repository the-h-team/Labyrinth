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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Fluent interface builder for menu-filling elements
 */
public final class FillerBuilder {
	private final MenuBuilder menuBuilder;
	private final MenuElement menuElement;
	private ClickAction menuAction;

	/**
	 * Create an {@link FillerBuilder} for a MenuBuilder with a MenuElement.
	 *
	 * @param menuBuilder the target MenuBuilder
	 * @param menuElement a new MenuElement
	 */
	protected FillerBuilder(MenuBuilder menuBuilder, MenuElement menuElement) {
		this.menuBuilder = menuBuilder;
		this.menuElement = menuElement;
	}

	/**
	 * Set a new action for this element.
	 * <p>
	 * Set to null to remove.
	 *
	 * @param clickAction which accepts MenuClick
	 * @return this builder
	 */
	public FillerBuilder setAction(ClickAction clickAction) {
		this.menuAction = clickAction;
		return this;
	}

	/**
	 * Set a new base item for this element.
	 *
	 * @param itemStack a valid ItemStack
	 * @return this builder
	 */
	public FillerBuilder setItem(@NotNull ItemStack itemStack) {
		this.menuElement.baseItem = itemStack;
		return this;
	}

	/**
	 * Set a new display text for this element.
	 *
	 * @param text display name of item
	 * @return this builder
	 */
	public FillerBuilder setText(String text) {
		this.menuElement.displayName = text;
		return this;
	}

	/**
	 * Set new lore text for this element.
	 *
	 * @param lore String varargs new lore
	 * @return this builder
	 */
	public FillerBuilder setLore(String... lore) {
		if (lore == null) {
			this.menuElement.lore = null;
			return this;
		}
		this.menuElement.lore = new ArrayList<>(Arrays.asList(lore));
		return this;
	}

	/**
	 * Add a line to lore for this element.
	 *
	 * @param line line of lore to be added
	 * @return this builder
	 */
	public FillerBuilder addLore(@NotNull String line) {
		if (this.menuElement.lore == null) this.menuElement.lore = new ArrayList<>();
		this.menuElement.lore.add(line);
		return this;
	}

	/**
	 * Apply this filler to the menu.
	 *
	 * @return the MenuBuilder
	 */
	public MenuBuilder set() {
		menuBuilder.fillerItem = menuElement;
		menuBuilder.fillerAction = menuAction;
		return menuBuilder;
	}
}
