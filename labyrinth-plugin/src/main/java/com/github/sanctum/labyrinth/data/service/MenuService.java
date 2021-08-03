package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.unity.construct.Menu;
import com.github.sanctum.labyrinth.unity.construct.MenuOptional;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

/**
 * Provides easy menu creation access.
 */
public interface MenuService {

	/**
	 * Get or make the desired gui type from a single builder!
	 *
	 * Default menu types are: {@link com.github.sanctum.labyrinth.unity.impl.menu.PaginatedMenu}, {@link com.github.sanctum.labyrinth.unity.impl.menu.PrintableMenu} & {@link com.github.sanctum.labyrinth.unity.impl.menu.SingularMenu}
	 *
	 * @param type The type of menu this is.
	 * @param predicate The prerequisite to locating an already stored instance.
	 * @param <T> The type of menu.
	 * @return A menu optional.
	 */
	@NotNull <T extends Menu> MenuOptional<T> getMenu(Class<T> type, Predicate<Menu> predicate);

}
