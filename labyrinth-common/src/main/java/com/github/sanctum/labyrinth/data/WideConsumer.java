package com.github.sanctum.labyrinth.data;
@FunctionalInterface
public interface WideConsumer<T, V> {

	void accept(T t, V v);

}
