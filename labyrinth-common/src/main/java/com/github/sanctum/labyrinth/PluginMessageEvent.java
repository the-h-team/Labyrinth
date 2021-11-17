package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.data.PluginChannel;
import com.github.sanctum.labyrinth.data.PluginMessage;
import com.github.sanctum.labyrinth.event.custom.DefaultEvent;
import com.github.sanctum.labyrinth.event.custom.Vent;

/**
 * An event responsible for relaying messages containing different object-oriented information.
 */
public final class PluginMessageEvent extends DefaultEvent {

	private final PluginMessage<?> message;
	private final PluginChannel<?> channel;

	PluginMessageEvent(PluginMessage<?> message, PluginChannel<?> channel) {
		this.message = message;
		this.channel = channel;
	}

	/**
	 * @return The channel messaged.
	 */
	public PluginChannel<?> getChannel() {
		return channel;
	}

	/**
	 * @return The message received.
	 */
	public PluginMessage<?> getMessage() {
		return message;
	}
}
