package com.github.sanctum.labyrinth.event.custom;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.task.Schedule;

public final class VentMapImpl extends VentMap {

	final Map<Plugin, Map<String, Set<VentListener>>> listeners;

	final Map<Plugin, Map<Class<? extends Vent>, Map<Vent.Priority, Set<Vent.Subscription<?>>>>> subscriptions;

	public VentMapImpl() {
		listeners = new HashMap<>();
		subscriptions = new HashMap<>();
	}

	@Override
	public <T extends Vent> void unsubscribe(@NotNull Class<T> eventType, @NotNull String key) {
		Optional<Vent.Subscription<?>> subscription = subscriptions.values().stream().flatMap(
				m -> Optional.ofNullable(m.get(eventType))
						.map(Map::values).map(Collection::stream)
						.map(s -> s.flatMap(Collection::stream)).orElse(Stream.empty())
		).filter(s -> s.getKey().map(key::equals).orElse(false)).findFirst();
		subscription.ifPresent(sub ->
				Schedule.sync(() -> subscriptions.get(sub.getUser()).get(eventType).get(sub.getPriority()).remove(sub))
		);
	}

	@Override
	public <T extends Vent> void unsubscribeAll(@NotNull Class<T> eventType, @NotNull String key) {
		subscriptions.values().forEach(m ->
				Optional.ofNullable(m.get(eventType))
						.map(Map::values)
						.map(Collection::stream)
						.ifPresent(s ->
								s.forEachOrdered(set -> Schedule.sync(
										() -> set.removeIf(sub -> sub.getKey().map(key::equals).orElse(false))
								).waitReal(1))));
	}

	@Override
	public void unsubscribe(final Vent.Subscription<?> subscription) {
		Optional.ofNullable(subscriptions.get(subscription.getUser()))
				.map(m -> m.get(subscription.getEventType()))
				.map(m -> m.get(subscription.getPriority()))
				.ifPresent(s -> s.remove(subscription));
	}

	@Override
	public void unsubscribeAll(@NotNull String key) {
		unsubscribeAll(s -> s.getKey().map(key::equals).orElse(false));
	}

	@Override
	public void unsubscribeAll(Predicate<Vent.Subscription<?>> fun) {
		subscriptions.values().forEach(v -> v.values().forEach(m -> m.values().forEach(
				set -> Schedule.sync(() -> set.removeIf(fun))
		)));
	}

	@Override
	public void unregister(@NotNull Object listener) {
		Optional<VentListener> listenerOptional = getListeners().stream().filter(l -> l.getListener().equals(listener))
				.findFirst();
		if (listenerOptional.isPresent()) {
			VentListener ventListener = listenerOptional.get();
			Schedule.sync(() -> listeners.get(ventListener.getHost()).get(ventListener.getKey()).remove(ventListener))
					.run();
		}
	}

	public void unregister(Plugin host, @Nullable String key, Object listener) {
		Optional.ofNullable(listeners.get(host)).map(m -> m.get(key))
				.ifPresent(s -> Schedule.sync(() -> s.removeIf(l -> listener.equals(l.getListener()))).run());
	}

	@Override
	public void unregisterAll(@NotNull Plugin host) {
		listeners.remove(host);
	}

	@Override
	public List<VentListener> list(@NotNull Plugin plugin) {
		return listeners.get(plugin).values().stream().flatMap(Collection::stream).collect(Collectors.toList());
	}

	@Override
	public List<Vent.Subscription<?>> narrow(@NotNull Plugin plugin) {
		return Optional.ofNullable(subscriptions.get(plugin)).map(Map::values)
				.map(Collection::stream)
				.map(s -> s.flatMap(m -> m.values().stream()))
				.map(s -> s.flatMap(Collection::stream))
				.map(s -> s.collect(Collectors.toList()))
				.orElse(Collections.emptyList());
	}

	@Override
	public VentListener get(String key) {
		return getListeners().stream().filter(l -> l.getKey().equals(key)).findAny().orElse(null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Vent> Vent.Subscription<T> get(@NotNull Class<T> eventType, @NotNull String key) {
		return (Vent.Subscription<T>) subscriptions.values().stream()
				.flatMap(m -> Optional.ofNullable(m.get(eventType)).map(Map::values)
						.map(Collection::stream).orElse(Stream.empty()))
				.flatMap(Collection::stream)
				.filter(s -> s.getKey().map(key::equals).orElse(false)).findAny().orElse(null);
	}

	@Override
	public void register(@NotNull Plugin host, @NotNull Object listener) {
		VentListener list = new VentListener(host, listener);
		listeners.computeIfAbsent(host, h -> new HashMap<>()).computeIfAbsent(list.getKey(), s -> new HashSet<>())
				.add(list);
	}

	@Override
	public void registerAll(@NotNull Plugin host, @NotNull Object... listener) {
		for (Object o : listener) {
			register(host, o);
		}
	}

	@Override
	public <T extends Vent> void subscribe(Vent.Subscription<T> subscription) {
		if (subscription == null) {
			LabyrinthProvider.getInstance().getLogger()
					.severe("Null subscription found from unknown source (Not labyrinth).");
			return;
		}
		subscriptions.computeIfAbsent(subscription.getUser(), p -> new HashMap<>())
				.computeIfAbsent(subscription.getEventType(), t -> new HashMap<>())
				.computeIfAbsent(subscription.getPriority(), p -> new HashSet<>())
				.add(subscription);
	}

	@Override
	public void chain(Vent.Link link) {
		if (link == null) {
			LabyrinthProvider.getInstance().getLogger()
					.severe("Null subscription link found from unknown source (Not labyrinth).");
			return;
		}
		Vent.Subscription<?> parent = null;
		for (Vent.Subscription<?> sub : link.subscriptions) {
			if (sub.getParent() != null) {
				parent = sub.getParent();
			}
			subscribe(sub);
		}

		if (parent != null) {
			subscribe(parent);
		}

	}

	@Override
	public List<Vent.Subscription<?>> getSubscriptions() {
		return subscriptions.values().stream().flatMap(m -> m.values().stream()).flatMap(m -> m.values().stream())
				.flatMap(Set::stream).collect(Collectors.toList());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Vent> Stream<Vent.Subscription<T>> getSubscriptions(final Class<T> tClass,
																		  final Vent.Priority priority) {
		return subscriptions.values().stream().map(m -> Optional.ofNullable(m.get(tClass)).map(v -> v.get(priority))
						.orElse(Collections.emptySet()))
				.flatMap(Collection::stream).map(s -> (Vent.Subscription<T>) s);
	}


	@Override
	public List<VentListener> getListeners() {
		return listeners.values().stream().map(Map::values).flatMap(Collection::stream).flatMap(Set::stream)
				.collect(Collectors.toList());
	}
}
