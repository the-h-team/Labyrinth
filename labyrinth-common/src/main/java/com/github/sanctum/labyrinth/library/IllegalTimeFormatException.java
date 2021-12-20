package com.github.sanctum.labyrinth.library;

public class IllegalTimeFormatException extends Exception {
	private static final long serialVersionUID = -3988653302903744084L;

	public IllegalTimeFormatException(String message) {
		super(message);
	}

	public IllegalTimeFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
