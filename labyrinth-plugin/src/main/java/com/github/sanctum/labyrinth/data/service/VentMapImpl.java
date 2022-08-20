package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherCollectors;
import com.github.sanctum.panther.container.PantherEntryMap;
import com.github.sanctum.panther.container.PantherList;
import com.github.sanctum.panther.container.PantherMap;
import com.github.sanctum.panther.container.PantherSet;
import com.github.sanctum.panther.event.Vent;
import com.github.sanctum.panther.event.VentMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VentMapImpl extends VentMap {

	final PantherMap<Vent.Host, PantherMap<String, PantherSet<Vent.Link>>> listeners = new PantherEntryMap<>();
	final PantherMap<Vent.Host, PantherMap<Class<? extends Vent>, PantherMap<Vent.Priority, PantherSet<Vent.Subscription<?>>>>> subscriptions = new PantherEntryMap<>();
	final PantherMap<String, PantherSet<Vent.Subscription.Extender<?>>> extenders = new PantherEntryMap<>();
	final Obligation obligation = () -> "To provide a local cache for custom event handling.";

	@Override
	public @NotNull Obligation getObligation() {
		return obligation;
	}

	@Override
	public void subscribe(Vent.@NotNull Host host, @NotNull Object listener) {
		Vent.Link link = new Vent.Link(host, listener) {
		};
		if (listener instanceof Vent.Link) {
			link = (Vent.Link) listener;
		}
		if (listener instanceof Listener && host instanceof Plugin) {
			Bukkit.getPluginManager().registerEvents((Listener) listener, (Plugin) host);
		}
		listeners.computeIfAbsent(host, h -> new PantherEntryMap<>()).computeIfAbsent(link.getKey(), s -> new PantherSet<>())
				.add(link);
	}

	@Override
	public void subscribe(Vent.@NotNull Subscription<?> subscription) {
		subscriptions.computeIfAbsent(subscription.getHost(), p -> new PantherEntryMap<>())
				.computeIfAbsent(subscription.getEventType(), t -> new PantherEntryMap<>())
				.computeIfAbsent(subscription.getPriority(), p -> new PantherSet<>())
				.add(subscription);
	}

	@Override
	public void subscribe(Vent.Subscription.@NotNull Extender<?> extender) {
		extenders.computeIfAbsent(extender.getKey(), s -> new PantherSet<>()).add(extender);
	}

	@Override
	public void subscribeAll(Vent.@NotNull Subscription<?> subscription, Vent.Subscription<?>... subscriptions) {
		subscribe(subscription);
		for (Vent.Subscription<?> sub : subscriptions) {
			subscribe(sub);
		}
	}

	@Override
	public void subscribeAll(Vent.@NotNull Host host, @NotNull Object... listeners) {
		for (Object o : listeners) {
			subscribe(host, o);
		}
	}

	@Override
	public void unsubscribe(@NotNull Object listener) {
		if (listener instanceof Vent.Link) {
			listeners.get(((Vent.Link) listener).getHost()).get(((Vent.Link) listener).getKey()).remove((Vent.Link) listener);
		}
		Optional<Vent.Link> optional = getLinks().stream().filter(l -> l.getParent().equals(listener))
				.findFirst();
		if (optional.isPresent()) {
			Vent.Link link = optional.get();
			if (listener instanceof Listener && link.getHost() instanceof Plugin) {
				Bukkit.getPluginManager().registerEvents((Listener) listener, (Plugin) link.getHost());
			}
			listeners.get(link.getHost()).get(link.getKey()).remove(link);
		}
	}

	@Override
	public void unsubscribe(Vent.@NotNull Subscription<?> subscription) {
		Optional.ofNullable(subscriptions.get(subscription.getHost()))
				.map(m -> m.get(subscription.getEventType()))
				.map(m -> m.get(subscription.getPriority()))
				.ifPresent(s -> s.remove(subscription));
	}

	@Override
	public void unsubscribe(Vent.Subscription.@NotNull Extender<?> extender) {
		Optional.ofNullable(extenders.get(extender.getKey())).ifPresent(s -> s.remove(extender));
	}

	@Override
	public void unsubscribe(Vent.@NotNull Host host, @NotNull String key) {
		Optional.ofNullable(listeners.get(host)).map(m -> m.get(key))
				.ifPresent(s -> s.removeIf(l -> key.equals(l.getKey())));
	}

	@Override
	public void unsubscribe(Vent.@NotNull Host host, @Nullable String key, Object listener) {
		Optional.ofNullable(listeners.get(host)).map(m -> m.get(key))
				.ifPresent(s -> s.removeIf(l -> listener.equals(l.getParent())));
	}

	@Override
	public <T extends Vent> void unsubscribe(@NotNull Class<T> eventType, @NotNull String key) {
		Optional<Vent.Subscription<?>> subscription = subscriptions.values().stream().flatMap(
				m -> Optional.ofNullable(m.get(eventType))
						.map(PantherMap::values)
						.map(PantherCollection::stream)
						.map(s -> s.flatMap(PantherCollection::stream)).orElse(Stream.empty())
		).filter(s -> s.getKey().map(key::equals).orElse(false)).findFirst();
		subscription.ifPresent(sub -> subscriptions.get(sub.getHost()).get(eventType).get(sub.getPriority()).remove(sub)
		);
	}

	@Override
	public void unsubscribeAll(@NotNull String key) {
		getSubscriptions().forEach(s -> {
			if (s.getKey().map(key::equals).orElse(false)) {
				s.remove();
			}
		});
		getLinks().forEach(l -> {
			if (l.getKey() != null && l.getKey().equals(key)) {
				l.remove();
			}
		});
	}

	@Override
	public void unsubscribeAll(@NotNull Predicate<Vent.Subscription<?>> fun) {
		getSubscriptions().stream().filter(fun).forEach(Vent.Subscription::remove);
	}

	@Override
	public void unsubscribeAll(Vent.@NotNull Host host) {
		getLinks().stream().filter(l -> host.equals(l.getHost())).forEach(Vent.Link::remove);
	}

	@Override
	public <T extends Vent> void unsubscribeAll(@NotNull Class<T> eventType, @NotNull String key) {
		subscriptions.values().forEach(m ->
				Optional.ofNullable(m.get(eventType))
						.map(PantherMap::values)
						.map(PantherCollection::stream)
						.ifPresent(s -> s.forEachOrdered(set -> set.removeIf(sub -> sub.getKey().map(key::equals).orElse(false)))));
	}

	@Override
	public PantherCollection<Vent.Link> getLinks() {
		return listeners.values().stream().map(PantherMap::values).flatMap(PantherCollection::stream).flatMap(PantherSet::stream)
				.collect(PantherCollectors.toList());
	}

	@Override
	public PantherCollection<Vent.Link> getLinks(Vent.@NotNull Host host) {
		return listeners.get(host).values().stream().flatMap(PantherCollection::stream).collect(PantherCollectors.toList());
	}

	@Override
	public PantherCollection<Vent.Subscription<?>> getSubscriptions() {
		return subscriptions.values().stream().flatMap(m -> m.values().stream()).flatMap(m -> m.values().stream())
				.flatMap(PantherSet::stream).collect(PantherCollectors.toList());
	}

	@Override
	public PantherCollection<Vent.Subscription<?>> getSubscriptions(Vent.@NotNull Host host) {
		return Optional.ofNullable(subscriptions.get(host)).map(PantherMap::values)
				.map(PantherCollection::stream)
				.map(s -> s.flatMap(m -> m.values().stream()))
				.map(s -> s.flatMap(PantherCollection::stream))
				.map(s -> s.collect(PantherCollectors.toList()))
				.orElse(new PantherList<>());
	}

	@Override
	public <T extends Vent> Stream<Vent.Subscription<T>> getSubscriptions(@NotNull Class<T> tClass, Vent.@NotNull Priority priority) {
		return subscriptions.values().stream().map(m -> Optional.ofNullable(m.get(tClass)).map(v -> v.get(priority))
						.orElse(new PantherSet<>()))
				.flatMap(PantherCollection::stream).map(s -> (Vent.Subscription<T>) s);
	}

	@Override
	public Vent.Link getLink(@NotNull String key) {
		return getLinks().stream().filter(l -> l.getKey().equals(key)).findAny().orElse(null);
	}

	@Override
	public <T extends Vent> Vent.Subscription<T> getSubscription(@NotNull Class<T> eventType, @NotNull String key) {
		return (Vent.Subscription<T>) subscriptions.values().stream()
				.flatMap(m -> Optional.ofNullable(m.get(eventType)).map(PantherMap::values)
						.map(PantherCollection::stream).orElse(Stream.empty()))
				.flatMap(PantherCollection::stream)
				.filter(s -> s.getKey().map(key::equals).orElse(false)).findAny().orElse(null);
	}

	@Override
	public Stream<Vent.Subscription.Extender<?>> getExtenders(@NotNull String key) {
		return extenders.computeIfAbsent(key, s -> new PantherSet<>()).stream();
	}

}
