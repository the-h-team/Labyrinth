package com.github.sanctum.labyrinth.formatting;

@FunctionalInterface
public interface ComponentDecoration<T> {

	void apply(PaginatedList<T> pagination, T object, int page, int max, int placement);

}
