package com.github.sanctum.labyrinth.common;

import java.util.Iterator;
import java.util.function.Function;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link ItemStack} operation and modification utility utilizing a bukkit {@link Container} object.
 *
 * @param <T> The type of container at use
 */
public final class ContainerOperation<T extends Container> implements Iterable<ItemStack> {

	final T container;

	ContainerOperation(T container) {
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
	public ContainerPredicate<T> check() {
		if (!isValid()) throw new UnsupportedOperationException("Cannot parse non containers!");
		return new ContainerPredicate<>(this);
	}

	/**
	 * Map the type of container this is.
	 *
	 * @param function The mapping function
	 * @param <R> The type of container to cast
	 * @return a newly mapped container query containing the same container object.
	 */
	public <R extends Container> ContainerOperation<R> map(Function<Container, R> function) {
		return of(function.apply(container));
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
	public static ContainerOperation<Container> of(Block block) {
		if (block instanceof Container) {
			return new ContainerOperation<>((Container) block);
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
	public static <T extends Container> ContainerOperation<T> of(@NotNull T container) {
		return new ContainerOperation<>(container);
	}

	@NotNull
	@Override
	public Iterator<ItemStack> iterator() {
		return container.getInventory().iterator();
	}
}
