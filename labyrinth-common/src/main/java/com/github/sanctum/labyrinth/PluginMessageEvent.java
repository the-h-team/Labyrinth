package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.data.PluginChannel;
import com.github.sanctum.labyrinth.data.PluginMessage;
import com.github.sanctum.labyrinth.event.custom.Vent;

public final class PluginMessageEvent extends Vent {

	private final PluginMessage<?> message;
	private final PluginChannel<?> channel;

	PluginMessageEvent(PluginMessage<?> message, PluginChannel<?> channel) {
		this.message = message;
		this.channel = channel;
	}

	public PluginChannel<?> getChannel() {
		return channel;
	}

	public PluginMessage<?> getMessage() {
		return message;
	}
}
