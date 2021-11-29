package com.github.sanctum.labyrinth.data;

/**
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface WideConsumer<W, C> {

	void accept(W w, C c);

}
