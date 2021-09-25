package com.github.sanctum.labyrinth.event.custom;

public class SubscriptionRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 6739394700886658490L;

	public SubscriptionRuntimeException(String message) {
		super(message);
	}

	public SubscriptionRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}
