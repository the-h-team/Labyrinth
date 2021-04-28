package com.github.sanctum.labyrinth.gui.builder;

/**
 * A functional interface providing RUNTIME operations in the event of collection to menu item processing.
 */
@FunctionalInterface
public interface InventoryProcess {

	void accept(ProcessElement processElement);

}
