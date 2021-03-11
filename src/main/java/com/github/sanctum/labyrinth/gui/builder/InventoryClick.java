package com.github.sanctum.labyrinth.gui.builder;

/**
 * A functional interface providing RUNTIME operations in the event of a menu click
 */
@FunctionalInterface
public interface InventoryClick {

	void clickEvent(PaginatedClick paginatedClick);

}
