package com.github.sanctum.labyrinth.data;

import org.jetbrains.annotations.NotNull;

public final class InvalidJsonAdapterException extends RuntimeException {

	InvalidJsonAdapterException() {
		super();
	}

	InvalidJsonAdapterException(@NotNull String message) {
		super(message);
	}

}
