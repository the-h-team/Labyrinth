package com.github.sanctum.labyrinth.data;

import org.bukkit.plugin.Plugin;

/**
 * An object-oriented plugin message.
 *
 * @param <T> The object type this message represents.
 */
public interface PluginMessage<T> {

	Plugin getPlugin();

	T getMessage();

}
