package com.github.sanctum.labyrinth.interfacing;

import com.github.sanctum.labyrinth.library.Applicable;
import org.jetbrains.annotations.NotNull;

public interface ResourceCheck extends Applicable {

	String getAuthor();

	String getResource();

	int getId();

	String getCurrent();

	String getLatest();

	static ResourceCheck of(@NotNull String project, @NotNull String author, int id) {
		return new SpigotUpdate(project, author, id);
	}

}
