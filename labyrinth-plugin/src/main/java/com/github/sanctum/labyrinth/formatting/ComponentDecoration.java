package com.github.sanctum.labyrinth.formatting;

/**
 * @author Hempfest
 */
@FunctionalInterface
public interface ComponentDecoration<T> {

	void apply(PaginatedList<T> pagination, T object, int page, int max, int placement);

}
