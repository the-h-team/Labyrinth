package com.github.sanctum.labyrinth.gui.shared;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper object for {@link SharedMenu} instantiation.
 */
public class SharedBuilder {

	/**
	 * <strong>Within your plugins onEnable</strong> instantiate a new shared menu.
	 *
	 * @param plugin the plugin to register the menu under
	 * @param id     the unique id to find the menu with
	 * @param name   the name that displays for the menu
	 * @param size   the size of the menu
	 * @return a new menu creation object
	 */
	public static SharedMenu create(Plugin plugin, String id, String name, int size) {
		return new SharedMenu(plugin, id) {
			@Override
			public @NotNull String getName() {
				return name;
			}

			@Override
			public int getSize() {
				return size;
			}
		}.inject();
	}

}
