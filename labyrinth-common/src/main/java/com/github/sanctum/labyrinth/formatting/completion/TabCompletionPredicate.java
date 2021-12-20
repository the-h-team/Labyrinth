package com.github.sanctum.labyrinth.formatting.completion;

import java.util.List;

class TabCompletionPredicate {

	final List<String> completions;
	final int predicateIndex;
	final String predicate;

	TabCompletionPredicate(int predicateIndex, String predicate, List<String> completions) {
		this.predicateIndex = predicateIndex;
		this.predicate = predicate;
		this.completions = completions;
	}
}
