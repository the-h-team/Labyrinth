package com.github.sanctum.labyrinth.formatting;

/**
 * @author Hempfest
 */
public interface StartingCompliment<T> extends ComponentCompliment<T> {

	void apply(PaginatedList<T> pagination, int page, int max);

}
