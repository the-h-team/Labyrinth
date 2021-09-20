package com.github.sanctum.labyrinth.library;

import org.bukkit.inventory.Inventory;

public class ItemSync<T extends ItemMatcher> {

	private final Class<T> matcher;
	private Inventory parent;
	private int amount;

	public ItemSync(Class<T> matcher) {
		this.matcher = matcher;
	}

	public ItemSync(Class<T> matcher, Inventory inventory) {
		this.matcher = matcher;
		this.parent = inventory;
	}

	public ItemSync(Class<T> matcher, Inventory inventory, int amount) {
		this.matcher = matcher;
		this.parent = inventory;
		this.amount = amount;
	}

	public ItemSync<T> setParent(Inventory inventory) {
		this.parent = inventory;
		return this;
	}

	public ItemSync<T> setAmountToRemove(int amountToRemove) {
		this.amount = amountToRemove;
		return this;
	}

	public int getAmount() {
		return amount;
	}

	public Inventory getParent() {
		return parent;
	}

	public Class<T> getMatcher() {
		return matcher;
	}

	public Deployable<Void> remove(ItemCompost compost) {
		return Deployable.of(null, unused -> compost.remove(this));
	}

}
