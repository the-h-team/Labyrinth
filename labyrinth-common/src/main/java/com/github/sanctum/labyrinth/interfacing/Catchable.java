package com.github.sanctum.labyrinth.interfacing;

public interface Catchable<T extends Snapshot> {

	T getSnapshot();

}
