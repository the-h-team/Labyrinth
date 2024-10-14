package com.github.sanctum.labyrinth.interfacing;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface WebResponse {

	@NotNull String get();

}
