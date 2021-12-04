package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.data.LabyrinthPluginChannel;
import com.github.sanctum.labyrinth.data.LabyrinthPluginMessage;
import com.github.sanctum.labyrinth.event.custom.DefaultEvent;

/**
 * An event responsible for relaying messages containing different object-oriented information.
 */
public final class LabyrinthPluginMessageEvent extends DefaultEvent {

	private final LabyrinthPluginMessage<?> message;
	private final LabyrinthPluginChannel<?> channel;
	private Object response;

	public LabyrinthPluginMessageEvent(LabyrinthPluginMessage<?> message, LabyrinthPluginChannel<?> channel) {
		this.message = message;
		this.channel = channel;
	}

	/**
	 * @return The channel messaged.
	 */
	public LabyrinthPluginChannel<?> getChannel() {
		return channel;
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}

	/**
	 * @return The message received.
	 */
	public LabyrinthPluginMessage<?> getMessage() {
		return message;
	}
}
