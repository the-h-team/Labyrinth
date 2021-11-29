package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.PluginMessageEvent;
import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.formatting.Message;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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

	public Object getResponse(@NotNull Plugin plugin, @NotNull Object o) {
		try {
			return new Vent.Call<>(PluginMessageEvent.class.getDeclaredConstructor(PluginMessage.class, PluginChannel.class).newInstance(new PluginMessage<Object>() {
				@Override
				public Plugin getPlugin() {
					return plugin;
				}

				@Override
				public Object getMessage() {
					return o;
				}
			}, this)).run().getResponse();
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException failed) {
			return null;
		}
	}

}
