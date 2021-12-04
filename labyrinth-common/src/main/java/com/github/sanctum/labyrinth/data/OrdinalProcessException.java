package com.github.sanctum.labyrinth.data;

public class OrdinalProcessException extends RuntimeException{
	private static final long serialVersionUID = 1216693413091563136L;

	public OrdinalProcessException(String message) {
		super(message);
	}

	public OrdinalProcessException(Throwable cause) {
		super(cause);
	}

	public OrdinalProcessException(String message, Throwable cause) {
		super(message, cause);
	}

	public OrdinalProcessException(String message, StackTraceElement[] stackTrace) {
		super(message);
		setStackTrace(stackTrace);
	}

}
