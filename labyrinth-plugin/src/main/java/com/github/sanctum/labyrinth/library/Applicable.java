package com.github.sanctum.labyrinth.library;

import java.io.Serializable;

/**
 * Use this functional interface to form lambdas/references that execute on run time for you.
 * Either passing values or running code, this can come in handy and also extends functionality from the Serializable interface.
 */
@FunctionalInterface
public interface Applicable extends Serializable {
	/**
	 * Execute any information applied within reference.
	 */
	void apply();
}
