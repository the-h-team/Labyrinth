package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.event.LabyrinthVentCall;
import com.github.sanctum.labyrinth.formatting.Message;
import com.github.sanctum.panther.event.Vent;
import com.github.sanctum.panther.util.TypeAdapter;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A labyrinth plugin channel designed to provide developers with a means to inquire desired object responses from output messaging through {@link LabyrinthPluginMessageEvent}
 *
 * @param <T> The type of object this channel references
 */
public final class LabyrinthPluginChannel<T> {

	public static final LabyrinthPluginChannel<Message> MESSAGE = new LabyrinthPluginChannel<>("message", Message.class);
	public static final LabyrinthPluginChannel<Object> DEFAULT = new LabyrinthPluginChannel<>("default", Object.class);

	private final String name;
	private final Class<T> tClass;

	public LabyrinthPluginChannel(String name, Class<T> tClass) {
		this.tClass = tClass;
		this.name = name;
	}

	public LabyrinthPluginChannel(String name, TypeAdapter<T> flag) {
		this.tClass = flag.getType();
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

	public @Nullable Object getResponse(@NotNull Plugin sender, @NotNull Object o) {
		try {
			return new LabyrinthVentCall<>(LabyrinthPluginMessageEvent.class.getDeclaredConstructor(LabyrinthPluginMessage.class, LabyrinthPluginChannel.class).newInstance(new LabyrinthPluginMessage<Object>() {
				@Override
				public Plugin getPlugin() {
					return sender;
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
