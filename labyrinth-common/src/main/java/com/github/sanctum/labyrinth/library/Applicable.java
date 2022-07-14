package com.github.sanctum.labyrinth.library;

import com.github.sanctum.panther.annotation.Removal;
import java.io.Serializable;

/**
 * Use this functional interface to form lambdas/references that execute on run time for you.
 * Either passing values or running code, this can come in handy and also extends functionality from the Serializable interface.
 *
 * @author Hempfest
 * @deprecated This will remain usable until version 1.8.1 where it will be moved to panther-common
 */
@FunctionalInterface
@Deprecated
@Removal(inVersion = "1.8.1")
public interface Applicable extends Runnable, Serializable {

	@Override
	void run();

	/**
	 * Execute any information applied within reference.
	 */
	default void apply(Applicable applicable) {
		run();
		applicable.run();
	}
}
