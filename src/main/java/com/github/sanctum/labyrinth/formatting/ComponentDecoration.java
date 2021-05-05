package com.github.sanctum.labyrinth.formatting;

@FunctionalInterface
public interface ComponentDecoration<T> {

	void apply(T object, int page, int max, int placement);

}
