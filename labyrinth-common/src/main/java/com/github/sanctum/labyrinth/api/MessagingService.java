package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.library.Message;
import org.jetbrains.annotations.NotNull;

/**
 * A service for default message creation.
 */
public interface MessagingService extends Service {

	/**
	 * Get a new message operation instance.
	 *
	 * @return A new message instance.
	 */
	@NotNull Message getNewMessage();

}
