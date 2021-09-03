package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.api.Service;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.service.AnnotationDiscovery;

/**
 * Core class for internal listener implementation
 * Wraps around any objects, detects methods annotated with {@link Subscribe} and creates SubscriberCalls with it.
 * Also, it recognises methods annotated with {@link Extend} and adds them to the method linking pool
 * Always has s string as key, which may be "null", when not specified by {@link LabeledAs};
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

	private final List<VentExtender<?>> extenders = new LinkedList<>();

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
		buildExtensors();
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
					Optional<Subscribe> subscribe = discovery.read(m).stream().findAny();
					@SuppressWarnings("unchecked")
					Class<? extends Vent> mClass = (Class<? extends Vent>) m.getParameters()[0].getType();
					if (subscribe.isPresent()) {
						registerSubscription(m, mClass, subscribe.get());
					} else {
						Bukkit.getLogger().severe("Error registering " + m.getDeclaringClass() + "#" +
												  m.getName());
					}
				}

		);
	}

	private void buildExtensors() {
		AnnotationDiscovery<Extend, ?> discovery = AnnotationDiscovery.of(Extend.class, listener);
		discovery.filter(m -> m.getParameters().length == 1 && m.isAnnotationPresent(Extend.class)
							  && Modifier.isPublic(m.getModifiers()));
		discovery.methods().forEach(m -> {
			Optional<Extend> extend = discovery.read(m).stream().findAny();
			if (extend.isPresent()) {
				Class<?> parameterClass = m.getParameters()[0].getType();
				registerExtender(m, parameterClass, extend.get());
			} else {
				Bukkit.getLogger().severe("Error registering " + m.getDeclaringClass() + "#" + m.getName());
			}
		});
	}

	private <T> void registerExtender(final Method m, final Class<T> parameterClass, Extend extend) {
		VentExtender<?> extender;
		String key = extend.identifier();
		if (m.getReturnType().equals(Void.TYPE)) {
			extender = new VentExtender<>(parameterClass, t -> invokeAsExtender(m, Object.class, t),
					key, this);
		} else {
			extender = new LinkingVentExtender<>(parameterClass, t -> invokeAsExtender(m, m.getReturnType(), t),
					key, this, extend.resultProcessors());
		}
		extenders.add(extender);
		LabyrinthProvider.getInstance().getEventMap().registerExtender(extender);
	}

	/**
	 * Helper method to build and event subscription out of the given method of the listener.
	 * The constructed subscription will include exception catching for any errors occurring while using the
	 * subscriber call and will display an informative message in that case, including the stacktrace.
	 *
	 * @param method    the method to use
	 * @param tClass    the class the method accepts as first and only parameter
	 * @param subscribe the annotation containing the conditions of the registration
	 * @param <T>       the type parameter of tClass
	 */
	private <T extends Vent> void registerSubscription(Method method, Class<T> tClass, Subscribe subscribe) {
		SubscriberCall<T> call;
		if (method.getReturnType().equals(Void.TYPE)) {
			//register as SubscriberCall lambda
			call = (t, s) -> invokeAsListener(method, tClass.getName(), Object.class, t);
		} else {
			//register as linking object
			Class<?> resultClass = method.getReturnType();
			call = new ListenerCall<>(subscribe.resultProcessors(),
					t -> invokeAsListener(method, tClass.getName(), resultClass, t));
		}
		eventMap.computeIfAbsent(tClass, c -> new HashMap<>())
				.computeIfAbsent(subscribe.priority(), p -> new HashSet<>())
				.add(call);
	}

	private <T> CallInfo<T> invokeAsListener(Method method, String eventName, Class<T> resultClass, Object... params) {
		String reflectionError = "Internal error hindered the " + listener.getClass().getName() + "#"
								 + method.getName() + " to handle events. Check method accessibility" +
								 " and parameters!";
		String callError = "Could not pass event " + eventName + " to " + host;
		return invoke(method, reflectionError, callError, resultClass, params);
	}

	private <T> CallInfo<T> invokeAsExtender(Method method, Class<T> resultClass, Object... params) {
		String passed = "passed elements " + Arrays.toString(params);
		String reflectionError = "Internal error hindered the " + listener.getClass().getName() + "#"
								 + method.getName() + " to further process " + passed +
								 ". Check method accessibility and parameters!";
		String callError = "Could not process" + passed + " at " + host;
		return invoke(method, reflectionError, callError, resultClass, params);
	}

	private <T> CallInfo<T> invoke(Method method, String refError, String callError, Class<T> retC, Object... params) {
		try {
			return new CallInfo<>(true, retC.cast(method.invoke(listener, params)));
		} catch (IllegalAccessException | InvocationTargetException e) {
			Bukkit.getLogger().severe(refError);
		} catch (Exception e) {
			Bukkit.getLogger().severe(callError);
			e.printStackTrace();
		}
		return new CallInfo<>(false, null);
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
		VentMap map = LabyrinthProvider.getInstance().getEventMap();
		map.unregister(host, getKey(), listener);
		extenders.forEach(map::unregisterExtender);
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

	public static class ListenerCall<S, T extends Vent> implements SubscriberCall<T> {

		private final String[] extenders;

		private final Function<T, CallInfo<S>> eventHandler;

		public ListenerCall(final String[] extenders, final Function<T, CallInfo<S>> eventHandler) {
			this.extenders = extenders;
			this.eventHandler = eventHandler;
		}

		@Override
		public void accept(final T event, final Vent.Subscription<T> unused) {
			CallInfo<S> callInfo = eventHandler.apply(event);
			if (callInfo.success) {
				for (String target : extenders)
					VentExtender.runExtensions(target, callInfo.result);
			}
		}
	}

	static class VentExtender<T> {

		private final Class<T> type;

		private final Consumer<T> extender;

		private final String key;

		private final Object parent;

		VentExtender(final Class<T> type, final Consumer<T> extender, final String key, final Object parent) {
			this.type = type;
			this.extender = extender;
			this.key = key;
			this.parent = parent;
		}

		public static void runExtensions(String key, Object toProcess) {
			LabyrinthProvider.getInstance().getEventMap().getExtenders(key)
					.filter(e -> e.getType().isAssignableFrom(toProcess.getClass()))
					.forEach(e -> runFinisher(e, toProcess));
		}

		private static <E> void runFinisher(VentExtender<E> ventExtender, Object toProcess) {
			ventExtender.extender.accept(ventExtender.getType().cast(toProcess));
		}

		public String getKey() {
			return key;
		}

		public Object getParent() {
			return parent;
		}

		public Class<T> getType() {
			return type;
		}
	}

	static final class LinkingVentExtender<S, T> extends VentExtender<T> {

		LinkingVentExtender(final Class<T> type, Function<T, CallInfo<S>> base, final String key,
							final Object parent, String[] targets) {
			super(type, buildExtender(base, targets), key, parent);
		}

		private static <T, S> Consumer<T> buildExtender(Function<T, CallInfo<S>> base, String[] targets) {
			return t -> {
				CallInfo<S> callInfo = base.apply(t);
				if (callInfo.success) {
					for (String target : targets) {
						VentExtender.runExtensions(target, callInfo.result);
					}
				}
			};
		}
	}

	static final class CallInfo<T> {
		private final boolean success;
		private final T result;

		CallInfo(final boolean success, final T result) {
			this.success = success;
			this.result = result;
		}
	}

}
