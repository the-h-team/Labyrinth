package com.github.sanctum.labyrinth.event.custom;

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
import org.jetbrains.annotations.NotNull;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.service.AnnotationDiscovery;

/**
 * Core class for internal listener implementation
 * Wraps around any objects, detects methods annotated with {@link Subscribe} and creates SubscriberCalls with it.
 * Always has s string as key, which may be "null";
 */

public class VentListener {

	/**
	 * The object holding the listening methods
	 */
	private final Object listener;

	/**
	 * The plugin providing the listener
	 */
	private final Plugin host;

	/**
	 * The optional identifier of this VentListener
	 */
	private final String label;

	/**
	 * A mapping for retrieving all methods to call easily
	 */
	private final Map<Class<? extends Vent>, Map<Vent.Priority, Set<SubscriberCall<?>>>> eventMap = new HashMap<>();

	/**
	 * Creates an VentListener out of the passed listener object and with the plugin as callback for communication
	 *
	 * @param host     the plugin providing the listener object
	 * @param listener the listener object
	 */
	public VentListener(Plugin host, Object listener) {
		this.listener = listener;
		this.host = host;
		this.label = readKey();
		buildEventHandlers();
	}


	/**
	 * Tries to detect a {@link LabeledAs} annotation on this class which contains the key.
	 *
	 * @return the string int the detected annotation, or "null" if none is present
	 */
	private String readKey() {
		String result = AnnotationDiscovery.of(LabeledAs.class, getListener()).map((r, u) -> r.value());
		return Objects.toString(result);
	}

	/**
	 * Detects all annotated methods and converts them into SubscriberCall methods.
	 */
	private void buildEventHandlers() {
		AnnotationDiscovery<Subscribe, ?> discovery = AnnotationDiscovery.of(Subscribe.class, listener);
		discovery.filter(m -> m.getParameters().length == 1 && Vent.class.isAssignableFrom(m.getParameters()[0].getType())
							  && m.isAnnotationPresent(Subscribe.class) && Modifier.isPublic(m.getModifiers()));
		discovery.methods().forEach(m -> {
					@SuppressWarnings("unchecked")
					Class<? extends Vent> mClass = (Class<? extends Vent>) m.getParameters()[0].getType();
					Vent.Priority priority = discovery.read(m).stream().findAny().map(Subscribe::priority)
							.orElse(Vent.Priority.MEDIUM);
					registerSubscription(m, mClass, priority);
				}

		);
	}

	/**
	 * Helper method to build and event subscription out of the given method of the listener.
	 * The constructed subscription will include exception catching for any errors occurring while using the
	 * subscriber call and will display an informative message in that case, including the stacktrace.
	 *
	 * @param method   the method to use
	 * @param tClass   the class the method accepts as first and only parameter
	 * @param priority the priority provided by the methods annotation
	 * @param <T>      the type parameter of tClass
	 */
	private <T extends Vent> void registerSubscription(Method method, Class<T> tClass, final Vent.Priority priority) {
		SubscriberCall<T> call = (t, s) -> {
			try {
				method.invoke(listener, t);
			} catch (IllegalAccessException | InvocationTargetException e) {
				Bukkit.getLogger().severe("Internal error hindered the " + listener.getClass().getName() + "#"
										  + method.getName() + " to handle events. Check method accessibility" +
										  " and parameters");
			} catch (Exception e) {
				Bukkit.getLogger().severe("Could not pass event " + tClass.getName() + " to " + host);
				e.printStackTrace();
			}
		};
		eventMap.computeIfAbsent(tClass, c -> new HashMap<>())
				.computeIfAbsent(priority, p -> new HashSet<>())
				.add(call);
	}

	/**
	 * Method used to retrieve all handling subscriber calls of one specific type and priority.
	 *
	 * @param eventClass the type the subscribers should accept
	 * @param priority   the priority the subscribers should have
	 * @param <T>        type parameter of the eventClass
	 * @return a Stream containing subscriber calls which meet the requirements
	 * @see Vent.Call#run()
	 * @see Vent.Call#complete()
	 */
	@SuppressWarnings("unchecked")
	public <T extends Vent> Stream<SubscriberCall<T>> getHandlers(Class<T> eventClass, Vent.Priority priority) {
		return Optional.ofNullable(eventMap.get(eventClass)).map(m -> m.get(priority)).map(Set::stream)
				.map(s -> s.map(c -> (SubscriberCall<T>) c)).orElse(Stream.empty());
	}

	/**
	 * @return the plugin providing the listener of this object
	 */
	public Plugin getHost() {
		return host;
	}

	/**
	 * @return the identifier for this listener, or "null", if none was set
	 */
	public @NotNull String getKey() {
		return label;
	}

	/**
	 * @return the listener object of this VentListener
	 */
	public Object getListener() {
		return listener;
	}

	/**
	 * Removes this Listener from event service, so that no more calls will be executed on it.
	 */
	public void remove() {
		LabyrinthProvider.getInstance().getEventMap().unregister(host, getKey(), listener);
	}


	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final VentListener that = (VentListener) o;
		return listener.equals(that.listener) && host.equals(that.host) && label.equals(that.label) && eventMap.equals(that.eventMap);
	}

	@Override
	public int hashCode() {
		return Objects.hash(listener, host, label);
	}

	@Override
	public String toString() {
		return "VentListener{" +
			   "listener=" + listener +
			   ", host=" + host +
			   ", label='" + label + '\'' +
			   ", eventMap=" + eventMap +
			   '}';
	}
}
