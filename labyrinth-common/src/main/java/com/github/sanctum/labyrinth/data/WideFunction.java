package com.github.sanctum.labyrinth.data;

/**
 * A Bi-Function like interface for converting 2 objects into one result.
 *
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface WideFunction<W, F, R> {

	R accept(W w, F f);

}
