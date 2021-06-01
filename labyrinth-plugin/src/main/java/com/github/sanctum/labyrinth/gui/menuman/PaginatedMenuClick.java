package com.github.sanctum.labyrinth.gui.menuman;

/**
 * A functional interface providing RUNTIME operations in the event of a menu click
 */
@FunctionalInterface
public interface PaginatedMenuClick<T> {

	void clickEvent(PaginatedClickAction<T> paginatedClick);

}
