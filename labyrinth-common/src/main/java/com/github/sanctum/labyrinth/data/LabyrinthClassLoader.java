package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.panther.util.AbstractClassLoader;
import java.io.File;
import java.io.IOException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class LabyrinthClassLoader<T> extends AbstractClassLoader<T> {

	protected LabyrinthClassLoader(@NotNull File file, ClassLoader parent, Object... args) throws IOException {
		super(file, LabyrinthProvider.getInstance().getPluginInstance(), parent, args);
	}

	protected LabyrinthClassLoader(@NotNull Plugin plugin, @NotNull File file, ClassLoader parent, Object... args) throws IOException {
		super(file, plugin, parent, args);
	}
}
