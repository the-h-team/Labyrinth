package com.github.sanctum.labyrinth.gui.menuman;

/**
 * A functional interface providing RUNTIME operations in the event of a menu close
 */
@FunctionalInterface
public interface PaginatedMenuClose<T> {

	void closeEvent(PaginatedCloseAction<T> paginatedClose);

}
