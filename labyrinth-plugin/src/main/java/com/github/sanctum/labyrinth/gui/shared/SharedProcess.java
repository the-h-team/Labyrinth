package com.github.sanctum.labyrinth.gui.shared;

/**
 * A functional interface providing RUNTIME operations in the event of a menu click
 */
@FunctionalInterface
public interface SharedProcess {

	void clickEvent(SharedClick sharedClick);

}
