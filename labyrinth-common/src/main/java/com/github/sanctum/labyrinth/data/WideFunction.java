package com.github.sanctum.labyrinth.data;

/**
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface WideFunction<W, F, R> {

	R accept(W w, F f);

}
