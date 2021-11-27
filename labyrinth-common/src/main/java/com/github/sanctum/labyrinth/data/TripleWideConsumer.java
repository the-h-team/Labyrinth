package com.github.sanctum.labyrinth.data;

public interface TripleWideConsumer<T, W, C> {

	void accept(T t, W w, C c);

}
