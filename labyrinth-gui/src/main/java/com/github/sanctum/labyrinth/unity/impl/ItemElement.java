package com.github.sanctum.labyrinth.unity.impl;

import com.github.sanctum.labyrinth.unity.construct.Menu;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemElement<V> extends Menu.Element<ItemStack, Menu.Click> {

	private boolean slotted;

	private Navigation navigation;

	private int slot;

	private V data;

	private ItemStack item;

	private Menu.Click click;

	public ItemElement() {}

	public ItemElement(V data) {
		this.data = data;
	}

	public Optional<V> getData() {
		return Optional.ofNullable(this.data);
	}

	public Optional<Integer> getSlot() {
		return slotted ? Optional.of(this.slot) : Optional.empty();
	}

	public ItemElement<V> setSlot(int slot) {
		this.slot = slot;
		this.slotted = true;
		return this;
	}

	public ItemElement<V> setClick(Menu.Click click) {
		this.click = click;
		return this;
	}

	public ItemElement<V> setElement(ItemStack item) {
		this.item = item;
		return this;
	}

	public ItemElement<V> setNavigation(Navigation navigation) {
		this.navigation = navigation;
		return this;
	}

	public @Nullable Navigation getNavigation() {
		return this.navigation;
	}

	@Override
	public @NotNull ItemStack getElement() {
		return this.item;
	}

	@Override
	public Menu.Click getAttachment() {
		return this.click;
	}

	public String getName() {
		return getElement().getItemMeta() != null ? getElement().getItemMeta().getDisplayName() : getElement().getType().name();
	}

	public enum Navigation {
		Previous,
		Next,
		Back
	}

}
