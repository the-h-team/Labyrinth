package com.github.sanctum.labyrinth.interfacing;

import com.github.sanctum.labyrinth.api.Service;

/**
 * A non-linked snapshot initialization service.
 *
 * @param <T> The type of snapshot.
 */
public interface Catchable<T extends Snapshot> extends Service {

	T getSnapshot();

}
