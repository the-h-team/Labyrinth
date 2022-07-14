package com.github.sanctum.labyrinth.gui.unity.event;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.impl.ItemElement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MenuClickEvent extends MenuInteractEvent {
	final ItemElement<?> itemElement;
	final ItemStack item;

	public MenuClickEvent(@NotNull org.bukkit.entity.Player player, @NotNull Menu menu, @NotNull ItemElement<?> itemElement) {
		super(Type.CLICK, player, menu);
		this.itemElement = itemElement;
		this.item = itemElement.getElement();
	}

	public @NotNull ItemStack getItem() {
		return item;
	}

	public @NotNull ItemElement<?> getItemElement() {
		return itemElement;
	}
}
