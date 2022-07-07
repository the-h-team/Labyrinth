package com.github.sanctum.labyrinth.command;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.interfacing.Token;
import org.jetbrains.annotations.NotNull;

public final class LabyrinthCommandToken implements Token<Labyrinth> {

	final Labyrinth labyrinth;
	final boolean valid;

	public LabyrinthCommandToken(@NotNull Labyrinth labyrinth) {
		this.labyrinth = labyrinth;
		this.valid = !labyrinth.isEnabled();
	}

	public boolean isValid() {
		return valid;
	}

	public Labyrinth get() {
		return labyrinth;
	}
}
