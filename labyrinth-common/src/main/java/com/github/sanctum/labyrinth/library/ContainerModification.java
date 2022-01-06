package com.github.sanctum.labyrinth.library;

import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ContainerModification<T extends Container> extends ContainerQuestion<T> {

	final ContainerQuery<T> query;
	final Inventory snapshot;

	ContainerModification(ContainerQuery<T> query) {
		super(query);
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

	public boolean has(@NotNull ItemStack itemStack) {
		return snapshot.contains(itemStack);
	}

	public boolean has(@NotNull Material material) {
		return snapshot.contains(material);
	}

	public boolean has(@NotNull ItemStack itemStack, int amount) {
		return snapshot.contains(itemStack, amount);
	}

	public boolean has(@NotNull Material material, int amount) {
		return snapshot.contains(material, amount);
	}

	public boolean isEmpty() {
		return snapshot.isEmpty();
	}
}
