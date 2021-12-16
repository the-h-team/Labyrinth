package com.github.sanctum.labyrinth.library;

import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ContainerModification<T extends Container> {

	final ContainerQuery<T> query;
	final Inventory snapshot;

	ContainerModification(ContainerQuery<T> query) {
		this.query = query;
		this.snapshot = query.container.getSnapshotInventory();
	}

	public ContainerModification<T> set(@NotNull ItemStack... items) {
		snapshot.setContents(items);
		return this;
	}

	public ContainerModification<T> add(@NotNull ItemStack... items) {
		snapshot.addItem(items);
		return this;
	}

	public ContainerModification<T> put(int index, @NotNull ItemStack item) {
		snapshot.setItem(index, item);
		return this;
	}

	public ContainerModification<T> remove(int index) {
		ItemStack test = snapshot.getItem(index);
		if (test != null) snapshot.remove(test);
		return this;
	}

	public ContainerModification<T> remove(@NotNull ItemStack item) {
		snapshot.remove(item);
		return this;
	}

	public ContainerQuery<T> update() {
		query.container.update();
		return query;
	}


}
