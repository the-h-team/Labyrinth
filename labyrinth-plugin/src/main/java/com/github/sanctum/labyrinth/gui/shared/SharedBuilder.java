package com.github.sanctum.labyrinth.gui.shared;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper object for {@link SharedMenu} instantiation.
 */
public class SharedBuilder {

	/**
	 * Within your plugins onEnable instantiate a new shared menu.
	 *
	 * @param plugin The plugin to register the menu under.
	 * @param id     The unique id to find the menu with.
	 * @param name   The name that display's for the menu
	 * @param size   The size of the menu
	 * @return A menu creation object.
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
