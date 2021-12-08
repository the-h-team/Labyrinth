package com.github.sanctum.labyrinth.interfacing;

/**
 * An interface used only to mark classes supporting snapshots.
 */
public interface Snapshot {

	boolean update() throws IllegalArgumentException;

}
