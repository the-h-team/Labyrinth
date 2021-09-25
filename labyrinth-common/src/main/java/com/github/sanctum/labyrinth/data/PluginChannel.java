package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.formatting.Message;

public final class PluginChannel<T> {

	public static final PluginChannel<Message> MESSAGE = new PluginChannel<>("message", Message.class);
	public static final PluginChannel<Object> DEFAULT = new PluginChannel<>("default", Object.class);

	private final String name;
	private final Class<T> tClass;

	PluginChannel(String name, Class<T> tClass) {
		this.tClass = tClass;
		this.name = name;
	}

	public T cast(Object message) {
		return getType().cast(message);
	}

	public String getName() {
		return this.name;
	}

	public Class<T> getType() {
		return this.tClass;
	}

}
