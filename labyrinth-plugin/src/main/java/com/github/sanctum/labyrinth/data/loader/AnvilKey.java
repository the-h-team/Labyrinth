package com.github.sanctum.labyrinth.data.loader;

/**
 * Used solely to load {@link AnvilMechanics} for use with {@link AnvilBuilder} on RUNTIME.
 */
public abstract class AnvilKey {


	public abstract AnvilMechanics get();

	public abstract String version();


}
