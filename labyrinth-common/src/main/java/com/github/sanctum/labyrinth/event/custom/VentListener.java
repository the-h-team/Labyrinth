package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.service.AnnotationDiscovery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class VentListener {

	private final Object listener;

	private final Plugin host;

	private final String label;

	private final Map<Class<? extends Vent>, Map<Vent.Priority, Set<SubscriberCall<?>>>> eventMap = new HashMap<>();

	public VentListener(Plugin host, Object listener) {
		this.listener = listener;
		this.host = host;
		this.label = readKey();
		buildEventHandlers();
	}

	public Plugin getHost() {
		return host;
	}

	private String readKey() {
		return AnnotationDiscovery.of(LabeledAs.class, getListener()).map((r, u) -> r.value());
	}

	private void buildEventHandlers() {
		AnnotationDiscovery<Subscribe, ?> discovery = AnnotationDiscovery.of(Subscribe.class, listener);
		discovery.filter(m -> m.getParameters().length == 1 && Vent.class.isAssignableFrom(m.getParameters()[0].getType())
							  && m.isAnnotationPresent(Subscribe.class) && Modifier.isPublic(m.getModifiers()));
		discovery.methods().forEach(m -> {
					@SuppressWarnings("unchecked")
					Class<? extends Vent> mClass = (Class<? extends Vent>) m.getParameters()[0].getType();
					Vent.Priority priority = discovery.read(m).stream().findAny().map(Subscribe::priority)
							.orElse(Vent.Priority.MEDIUM);
					registerSubscription(m, mClass, listener, priority);
				}

		);

	}

	public @Nullable String getKey() {
		return label;
	}

	public Object getListener() {
		return listener;
	}

	public void remove() {
		LabyrinthProvider.getInstance().getEventMap().unregister(this);
	}

	private <T extends Vent> void registerSubscription(Method method, Class<T> tClass,
													   Object listener, final Vent.Priority priority) {
		SubscriberCall<T> call = (t, s) -> {
			try {
				method.invoke(listener);
			} catch (IllegalAccessException | InvocationTargetException e) {
				Bukkit.getLogger().severe("Internal error hindered the " + listener.getClass().getName() + "#"
										  + method.getName() + " to handle events. Check method accessibility");
			} catch (Exception e) {
				Bukkit.getLogger().severe("Could not pass event " + tClass.getName() + " to " + host);
				e.printStackTrace();
			}
		};
		eventMap.computeIfAbsent(tClass, c -> new HashMap<>())
				.computeIfAbsent(priority, p -> new HashSet<>())
				.add(call);
	}

	@SuppressWarnings("unchecked")
	public <T extends Vent> Stream<SubscriberCall<T>> getHandlers(Class<T> eventClass, Vent.Priority priority) {
		return Optional.ofNullable(eventMap.get(eventClass)).map(m -> m.get(priority)).map(Set::stream)
				.map(s -> s.map(c -> (SubscriberCall<T>) c)).orElse(Stream.empty());
	}

	@Override
	public boolean equals(Object o1) {
		if (this == o1) return true;
		if (!(o1 instanceof VentListener)) return false;
		VentListener listener = (VentListener) o1;
		return this.listener.equals(listener.listener) &&
			   getHost().equals(listener.getHost());
	}

	@Override
	public int hashCode() {
		return Objects.hash(listener, getHost());
	}

}
