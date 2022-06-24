package com.github.sanctum.labyrinth.gui.unity.event;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.impl.ItemElement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MenuDragItemEvent extends MenuInteractEvent {
	final ItemElement<?> itemElement;
	final ItemStack item;

	public MenuDragItemEvent(@NotNull Menu menu, @NotNull Player player, @NotNull ItemElement<?> itemElement) {
		super(Type.DRAG, player, menu);
		this.item = itemElement.getElement();
		this.itemElement = itemElement;
	}

	public @NotNull ItemStack getItem() {
		return item;
	}

	public @NotNull ItemElement<?> getItemElement() {
		return itemElement;
	}
}
