package com.github.sanctum.labyrinth.data;

import org.bukkit.plugin.Plugin;

public interface PluginMessage<T> {

	Plugin getPlugin();

	T getMessage();

}
