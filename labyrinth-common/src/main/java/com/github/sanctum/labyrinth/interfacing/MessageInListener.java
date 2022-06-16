package com.github.sanctum.labyrinth.interfacing;

import org.jetbrains.annotations.NotNull;

public interface MessageInListener {

	void onReceiveMessage(@NotNull String message);

	default void onReceiveSuggestion(@NotNull String suggestion) {}

}
