package com.github.sanctum.labyrinth.data.service;

/**
 * Used solely to load {@link AnvilMechanics} for use with {@link AnvilBuilder} on RUNTIME.
 */
public abstract class ExternalDataService {


	public abstract AnvilMechanics getMechanics();

	public abstract String getServerVersion();


}
