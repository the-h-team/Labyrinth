package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.formatting.Message;
import com.github.sanctum.labyrinth.library.Mailer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A service for default message creation.
 */
public interface MessagingService extends Service, Message.Factory {

	/**
	 * Get a new message operation instance.
	 *
	 * @see MessagingService#getEmptyMailer()
	 * @deprecated This utility is being replaced, use {@link MessagingService#getEmptyMailer()} instead.
	 * @return A new message instance.
	 */
	@Deprecated()
	@NotNull com.github.sanctum.labyrinth.library.Message getNewMessage();


	/**
	 * Get a new object for sending numerous types of displayable messages!
	 *
	 * @return A new empty mailer instance.
	 */
	@NotNull Mailer getEmptyMailer();

	/**
	 * Get a new object for sending numerous types of displayable messages!
	 *
	 * @return A new empty mailer instance.
	 */
	@NotNull Mailer getEmptyMailer(CommandSender sender);

	/**
	 * Get a new object for sending numerous types of displayable messages!
	 *
	 * @return A new empty mailer instance.
	 */
	@NotNull Mailer getEmptyMailer(Plugin plugin);

}
