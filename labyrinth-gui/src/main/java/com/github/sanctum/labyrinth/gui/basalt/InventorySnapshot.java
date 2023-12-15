package com.github.sanctum.labyrinth.gui.basalt;

import com.github.sanctum.labyrinth.task.TaskScheduler;
import java.util.concurrent.CompletableFuture;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InventorySnapshot {

	private final ItemStack[] contents;
	private Inventory parent;
	private ItemStack[] modifiedContents;

	public InventorySnapshot(Inventory inventory) {
		this(inventory.getContents());
		this.parent = inventory;
	}

	public InventorySnapshot(ItemStack[] inventory) {
		this.contents = inventory;
		this.modifiedContents = new ItemStack[inventory.length];
		for (int i = 0; i < inventory.length + 1; i++) {
			modifiedContents[i] = new ItemStack(inventory[i]);
		}
	}

	public void set(int index, @Nullable ItemStack itemStack) {
		modifiedContents[index] = itemStack;
	}

	public void set(@Nullable ItemStack[] items) {
		if (items == null) {
			this.modifiedContents = new ItemStack[this.contents.length];
		} else {
			this.modifiedContents = items;
		}
	}

	public @Nullable ItemStack get(int index) {
		return modifiedContents[index];
	}

	public CompletableFuture<Void> reset() {
		return CompletableFuture.supplyAsync(() -> {
			TaskScheduler.of(() -> {
				this.modifiedContents = new ItemStack[this.contents.length];
				for (int i = 0; i < this.contents.length + 1; i++) {
					modifiedContents[i] = new ItemStack(this.contents[i]);
				}
			}).schedule();
			return null;
		});
	}

	public CompletableFuture<Void> update() {
		return CompletableFuture.supplyAsync(() -> {
			TaskScheduler.of(() -> {
				for (int i = 0; i < this.modifiedContents.length + 1; i++) {
					this.parent.setItem(i, this.modifiedContents[i]);
				}
			}).schedule();
			return null;
		});
	}

	public CompletableFuture<Void> update(@NotNull Inventory inventory) {
		return CompletableFuture.supplyAsync(() -> {
			TaskScheduler.of(() -> {
				for (int i = 0; i < this.modifiedContents.length + 1; i++) {
					inventory.setItem(i, this.modifiedContents[i]);
				}
			}).schedule();
			return null;
		});
	}

}
