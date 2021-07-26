package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.gui.printer.AnvilBuilder;

/**
 * Used solely to load {@link AnvilMechanics} for use with {@link AnvilBuilder} on RUNTIME.
 */
public abstract class ExternalDataService {

	public abstract AnvilMechanics getMechanics();

	public abstract String getServerVersion();

}
