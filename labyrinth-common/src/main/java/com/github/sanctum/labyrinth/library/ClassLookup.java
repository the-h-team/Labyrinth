package com.github.sanctum.labyrinth.library;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ClassLookup {

	Class<?> accept(@NotNull String className);

}
