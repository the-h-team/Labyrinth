package com.github.sanctum.labyrinth.gui.basalt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

public enum InventoryPane {

	/**
	 * The top bar of slots within the inventory.
	 */
	TOP(i -> IntStream.range(0, 9).toArray()),

	/**
	 * The bottom bar of slots within the inventory.
	 */
	BOTTOM(i -> IntStream.range(i - 9, i).toArray()),

	/**
	 * The middle space of the inventory.
	 */
	MIDDLE(i -> {
		if (i <= 18) {
			return IntStream.range(0, 9).toArray();
		}
		return IntStream.range(10, i).filter(n -> n < i - 9 && n % 9 != 0 && n % 9 != 8).toArray();
	}),

	/**
	 * The left bar of slots within the inventory.
	 */
	LEFT(i -> IntStream.iterate(0, n -> n + 9).limit(i / 9).toArray()),

	/**
	 * The right bar of slots within the inventory.
	 */
	RIGHT(i -> IntStream.iterate(8, n -> n + 9).limit(i / 9).toArray());

	private final Function<Integer, int[]> generatorFunction;
	private final Map<Integer, int[]> cache = new HashMap<>();

	InventoryPane(final Function<Integer, int[]> generatorFunction) {
		this.generatorFunction = generatorFunction;
	}

	public int[] get(int slots) {
		int[] result = cache.computeIfAbsent(slots, generatorFunction);
		return Arrays.copyOf(result, result.length);
	}

}
