package com.github.sanctum.labyrinth.library;

import org.bukkit.inventory.Inventory;

/**
 * An object used to envelope inventory contents for proper removal or insertion of a specific match case.
 *
 * @param <T> The matcher in use.
 */
public abstract class ItemSync<T extends ItemMatcher> implements CompostElement {

	private final Class<T> matcher;
	private Inventory parent;
	private final int amount;

	public ItemSync(Class<T> matcher) {
		this.matcher = matcher;
		this.amount = -1;
	}

	public ItemSync(Class<T> matcher, Inventory inventory) {
		this.matcher = matcher;
		this.parent = inventory;
		this.amount = -1;
	}

	public ItemSync(Class<T> matcher, Inventory inventory, int amount) {
		this.matcher = matcher;
		this.parent = inventory;
		this.amount = amount;
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public Inventory getParent() {
		return parent;
	}

	/**
	 * @return The item matcher this synchronization aligns with.
	 */
	public Class<T> getMatcher() {
		return matcher;
	}

	@Override
	public Deployable<Void> remove(ItemCompost compost) {
		return Deployable.of(null, unused -> compost.remove(this));
	}

}
