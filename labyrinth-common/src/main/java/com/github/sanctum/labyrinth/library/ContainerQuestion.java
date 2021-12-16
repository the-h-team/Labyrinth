package com.github.sanctum.labyrinth.library;

import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ContainerQuestion<T extends Container> {

	final ContainerQuery<T> queue;

	ContainerQuestion(ContainerQuery<T> queue) {
		this.queue = queue;
	}

	public boolean has(@NotNull ItemStack itemStack) {
		return queue.container.getInventory().contains(itemStack);
	}

	public boolean has(@NotNull Material material) {
		return queue.container.getInventory().contains(material);
	}

	public boolean has(@NotNull ItemStack itemStack, int amount) {
		return queue.container.getInventory().contains(itemStack, amount);
	}

	public boolean has(@NotNull Material material, int amount) {
		return queue.container.getInventory().contains(material, amount);
	}

	public boolean isEmpty() {
		return queue.container.getInventory().isEmpty();
	}

}
