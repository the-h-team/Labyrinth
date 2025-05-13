package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.data.LabyrinthPluginChannel;
import com.github.sanctum.labyrinth.data.LabyrinthPluginMessage;
import com.github.sanctum.labyrinth.data.LabyrinthPluginMessageEvent;
import com.github.sanctum.labyrinth.data.service.Constant;
import com.github.sanctum.labyrinth.event.LabyrinthVentCall;
import com.github.sanctum.labyrinth.formatting.Message;
import com.github.sanctum.labyrinth.library.Mailer;
import com.github.sanctum.panther.annotation.FieldsFrom;
import com.github.sanctum.panther.util.Deployable;
import com.github.sanctum.panther.util.TypeAdapter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * A service for default message creation.
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
public interface MessagingService extends Service, Message.Factory {

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

	default <T> Deployable<Object> sendPluginMessage(Plugin plugin, T object) {
		return sendPluginMessage(plugin, object, LabyrinthPluginChannel.DEFAULT);
	}

	default <T> Deployable<Object> sendPluginMessage(Plugin plugin, T object, @FieldsFrom(LabyrinthPluginChannel.class) LabyrinthPluginChannel<?> channel) {
		return Deployable.of(() -> new LabyrinthVentCall<>(new LabyrinthPluginMessageEvent(new LabyrinthPluginMessage<T>() {
			@Override
			public Plugin getPlugin() {
				return plugin;
			}

			@Override
			public T getMessage() {
				return object;
			}
		}, channel)).run().getResponse(), 0);
	}

	default LabyrinthPluginChannel<?>[] getDefaultPluginChannels() {
		TypeAdapter<LabyrinthPluginChannel<?>> flag = TypeAdapter.get();
		return Constant.values(flag.getType(), flag.getType()).toArray(new LabyrinthPluginChannel[0]);
	}

}
