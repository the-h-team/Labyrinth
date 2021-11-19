package com.github.sanctum.labyrinth.formatting.pagination;

import java.util.Collection;
import java.util.Collections;

class LabyrinthPagination<T> extends AbstractPaginatedCollection<T> {

	LabyrinthPagination() {
		super(Collections.emptyList());
	}

	LabyrinthPagination(Collection<T> collection) {
		super(collection);
	}

	@SafeVarargs
	LabyrinthPagination(T... collection) {
		super(collection);
	}

}
