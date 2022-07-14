package com.github.sanctum.labyrinth.event;

import com.github.sanctum.panther.event.Vent;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

public class LabyrinthVentCall<T extends Vent> extends Vent.Call<T> {

	public LabyrinthVentCall(@NotNull T event) {
		super(event);
	}

	public CompletableFuture<T> schedule() {
		Vent.Runtime.Asynchronous.validate(event);
		return CompletableFuture.supplyAsync(this::run);
	}

}
