package com.github.sanctum.labyrinth.interfacing;

import com.github.sanctum.labyrinth.data.service.Service;
import org.jetbrains.annotations.NotNull;

/**
 * A non-linked snapshot initialization service.
 *
 * @param <T> The type of snapshot.
 */
public interface Duplication<T extends Snapshot> extends Service {

	T newDuplicate();

	void inform(@NotNull Snapshot snapshot);

}
