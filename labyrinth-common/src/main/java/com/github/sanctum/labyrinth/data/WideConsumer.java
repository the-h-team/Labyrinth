package com.github.sanctum.labyrinth.data;

/**
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface WideConsumer<T, V> {

	void accept(T t, V v);

}
