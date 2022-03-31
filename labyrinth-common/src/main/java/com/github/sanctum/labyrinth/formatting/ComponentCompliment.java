package com.github.sanctum.labyrinth.formatting;

/**
 * @author Hempfest
 */
@Deprecated
public interface ComponentCompliment<T> {

	@SuppressWarnings("EmptyMethod")
	void apply(PaginatedList<T> pagination, int page, int max);

}
