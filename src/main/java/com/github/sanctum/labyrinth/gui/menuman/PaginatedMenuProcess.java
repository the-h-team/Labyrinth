package com.github.sanctum.labyrinth.gui.menuman;

/**
 * A functional interface providing RUNTIME operations in the event of collection to menu item processing.
 */
@FunctionalInterface
public interface PaginatedMenuProcess<T> {

	void accept(PaginatedProcessAction<T> processElement);

}
