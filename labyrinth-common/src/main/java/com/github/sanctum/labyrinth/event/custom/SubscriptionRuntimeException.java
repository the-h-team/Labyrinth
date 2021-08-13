package com.github.sanctum.labyrinth.event.custom;

public class SubscriptionRuntimeException extends RuntimeException {

	public SubscriptionRuntimeException(String message) {
		super(message);
	}

	public SubscriptionRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}
