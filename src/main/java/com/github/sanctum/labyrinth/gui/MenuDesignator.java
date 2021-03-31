package com.github.sanctum.labyrinth.gui;

import com.github.sanctum.labyrinth.gui.builder.PaginatedMenu;
import com.github.sanctum.labyrinth.gui.menuman.Menu;
import com.github.sanctum.labyrinth.gui.shared.SharedMenu;

/**
 * To be used along side {@link Enum} enumeration.
 * Specify a menu type to manage.
 */
public interface MenuDesignator {

	/**
	 * Get a singular GUI screen.
	 *
	 * @return The desired menu-man singular Menu.
	 */
	Menu get();

	/**
	 * Get a paginated GUI screen.
	 *
	 * @return The desired pagu-man paged Menu.
	 */
	PaginatedMenu supply();

	/**
	 * Get a shared inventory menu.
	 *
	 * @return The desired intractable and shareable Menu.
	 */
	SharedMenu share();

}
