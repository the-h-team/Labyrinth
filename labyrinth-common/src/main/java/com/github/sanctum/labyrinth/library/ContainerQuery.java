package com.github.sanctum.labyrinth.library;

import java.util.Iterator;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link ItemStack} query and modification utility using a bukkit {@link Container} object.
 *
 * @param <T> The type of container at use
 */
public final class ContainerQuery<T extends Container> implements Iterable<ItemStack> {

	final T container;

	ContainerQuery(T container) {
		this.container = container;
	}

	/**
	 * Create a new container modification using an inventory snapshot.
	 *
	 * @return A container modification instance.
	 */
	public ContainerModification<T> edit() {
		if (!isValid()) throw new UnsupportedOperationException("Cannot edit non containers!");
		return new ContainerModification<>(this);
	}

	/**
	 * Check for existing items within this container query.
	 *
	 * @return A container questionnaire
	 */
	public ContainerQuestion<T> check() {
		if (!isValid()) throw new UnsupportedOperationException("Cannot parse non containers!");
		return new ContainerQuestion<>(this);
	}

	/**
	 * @return true if this query is valid for use.
	 */
	public boolean isValid() {
		return container != null;
	}

	/**
	 * Get a container query for a given block location.
	 *
	 * @param block The block to use.
	 * @return A fresh container query or null if the specific block isn't a container.
	 */
	public static ContainerQuery<Container> of(Block block) {
		if (block instanceof Container) {
			return new ContainerQuery<>((Container) block);
		}
		return null;
	}

	/**
	 * Get a container query for a specific container.
	 *
	 * @param container The container to use
	 * @param <T> The type of container.
	 * @return A fresh container query
	 */
	public static <T extends Container> ContainerQuery<T> of(@NotNull T container) {
		return new ContainerQuery<>(container);
	}

	@NotNull
	@Override
	public Iterator<ItemStack> iterator() {
		return container.getInventory().iterator();
	}
}
