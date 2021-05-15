package com.github.sanctum.labyrinth.formatting;

public interface ComponentCompliment<T> {

	void apply(PaginatedList<T> pagination, int page, int max);

}
