package com.github.sanctum.labyrinth.api;

public class MenuNotCacheableException extends RuntimeException {

	private static final long serialVersionUID = -2407402930976351480L;

	public MenuNotCacheableException(String message) {
		super(message);
	}

	public MenuNotCacheableException(String message, Throwable cause) {
		super(message, cause);
	}

}
