package com.github.sanctum.labyrinth.gui.builder;

/**
 * A functional interface providing RUNTIME operations in the event of a menu close
 */
@FunctionalInterface
public interface InventoryClose {

	void closeEvent(PaginatedClose paginatedClose);

}
