package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Vent {

	private Plugin plugin;

	private int id;

	private boolean async;

	private boolean cancelled;

	protected Vent() {
	}

	public Vent(boolean isAsync) {
		this.async = isAsync;
	}

	protected Vent(boolean isAsync, int id) {
		this.id = id;
		this.async = isAsync;
	}

	protected final void setHost(Plugin plugin) {
		if (this.plugin != null) throw new IllegalStateException("Plugin already initialized!");
		this.plugin = plugin;
	}

	public abstract String getName();

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public final boolean isAsynchronous() {
		return this.async;
	}

	protected final boolean isDefault() {
		return this.id == 420;
	}

	public final Plugin getHost() {
		return this.plugin;
	}

	public <T extends Vent> Class<T> getType() {
		return (Class<T>) getClass();
	}

	public static class Subscription<T extends Vent> {

		private final Class<T> eventType;
		private final SubscriberCall<T> action;
		private final Priority priority;
		private final Plugin user;
		private String key;

		public Subscription(Class<T> eventType, Plugin user, Priority priority, SubscriberCall<T> action) {
			this.eventType = eventType;
			this.user = user;
			this.priority = priority;
			this.action = action;
		}

		public Subscription(Class<T> eventType, String key, Plugin user, Priority priority, SubscriberCall<T> action) {
			this.eventType = eventType;
			this.key = key;
			this.user = user;
			this.priority = priority;
			this.action = action;
		}

		public void remove() {
			if (key != null) {
				Call.getMap().unsubscribe(eventType, key);
			} else {
				Call.getMap().SUBSCRIPTIONS.forEach(s -> {
					if (s.equals(this)) {
						Schedule.sync(() -> Call.getMap().SUBSCRIPTIONS.remove(this)).waitReal(1);
					}
				});
			}
		}

		public Optional<String> getKey() {
			return Optional.ofNullable(this.key);
		}

		public Plugin getUser() {
			return user;
		}

		public Priority getPriority() {
			return priority;
		}

		public SubscriberCall<T> getAction() {
			return action;
		}

		public Class<T> getEventType() {
			return eventType;
		}
	}

	public static <T extends Vent> void subscribe(Subscription<T> subscription) {
		Call.getMap().SUBSCRIPTIONS.add(subscription);
	}

	public static <T extends Vent> void unsubscribeAll(Class<T> labyrinthEvent, Plugin user) {

		VentMap map = Call.getMap();

		for (Subscription<?> s : map.SUBSCRIPTIONS) {
			if (s.getEventType().isAssignableFrom(labyrinthEvent)) {
				if (s.getUser().equals(user)) {
					Schedule.sync(() -> map.SUBSCRIPTIONS.remove(s)).run();
				}
			}
		}
	}

	public enum Runtime {
		Synchronous, Asynchronous
	}

	public enum Priority {

		READ_ONLY(0),

		LOW(1),

		MEDIUM(2),

		HIGH(3),

		HIGHEST(4);

		private final int level;

		Priority(int level) {
			this.level = level;
		}

		public int getLevel() {
			return level;
		}
	}

	public static class Call<T extends Vent> {

		private final T event;

		private final Runtime type;

		public Call(Runtime type, T event) {
			this.event = event;
			this.type = type;
		}

		public T run() {

			JavaPlugin plugin = JavaPlugin.getProvidingPlugin(event.getClass());
			VentMap map = getMap();

			switch (type) {
				case Synchronous:
					if (event.isAsynchronous()) throw new RuntimeException("This event can only be ran asynchronously");

					event.setHost(plugin);

					map.SUBSCRIPTIONS.stream().sorted(Comparator.comparingInt(value -> value.getPriority().getLevel())).forEach(s -> {

						if (s.getEventType().isAssignableFrom(event.getType())) {
							switch (s.getPriority()) {
								case READ_ONLY:
								case LOW:
								case MEDIUM:
								case HIGH:
								case HIGHEST:
									((SubscriberCall<T>) s.getAction()).accept(event, (Subscription<T>) s);
									break;
							}
						}

					});
					return event;

				case Asynchronous:
					if (!event.isAsynchronous()) throw new RuntimeException("This event can only be ran synchronously");

					event.setHost(plugin);

					return CompletableFuture.supplyAsync(() -> {

						event.setHost(plugin);

						map.SUBSCRIPTIONS.stream().sorted(Comparator.comparingInt(value -> value.getPriority().getLevel())).forEach(s -> {

							if (s.getEventType().isAssignableFrom(event.getType())) {
								switch (s.getPriority()) {
									case READ_ONLY:
									case LOW:
									case MEDIUM:
									case HIGH:
									case HIGHEST:
										((SubscriberCall<T>) s.getAction()).accept(event, (Subscription<T>) s);
										break;
								}
							}

						});
						return event;

					}).join();
				default:
					throw new IllegalArgumentException("An invalid RunType was provided!");
			}
		}

		public CompletableFuture<T> complete() {

			JavaPlugin plugin = JavaPlugin.getProvidingPlugin(event.getClass());
			VentMap map = getMap();

			switch (this.type) {
				case Asynchronous:

					if (!event.isAsynchronous()) throw new RuntimeException("This event can only be ran synchronously");

					event.setHost(plugin);

					return CompletableFuture.supplyAsync(() -> {

						event.setHost(plugin);

						map.SUBSCRIPTIONS.stream().sorted(Comparator.comparingInt(value -> value.getPriority().getLevel())).forEach(s -> {

							if (s.getEventType().isAssignableFrom(event.getType())) {
								switch (s.getPriority()) {
									case READ_ONLY:
									case LOW:
									case MEDIUM:
									case HIGH:
									case HIGHEST:
										((SubscriberCall<T>) s.getAction()).accept(event, (Subscription<T>) s);
										break;
								}
							}

						});
						return event;

					});

				case Synchronous:
				default:
					throw new IllegalArgumentException("An invalid RunType was provided!");
			}
		}

		public static VentMap getMap() {
			return ((Labyrinth) Labyrinth.getInstance()).getEventMap();
		}


	}
}
