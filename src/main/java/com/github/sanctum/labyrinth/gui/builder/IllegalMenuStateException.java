package com.github.sanctum.labyrinth.gui.builder;

/**
 * Thrown when no loading procedure is presented for the given menu.
 */
public final class IllegalMenuStateException extends Throwable {

	public IllegalMenuStateException() {
		super();
	}

	public IllegalMenuStateException(String message) {
		super(message);
	}

	public IllegalMenuStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalMenuStateException(Throwable cause) {
		super(cause);
	}

	protected IllegalMenuStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
