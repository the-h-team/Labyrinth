package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Tag;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Vent {

	private Plugin plugin;

	private int id;

	private boolean async;

	private CancelState state = CancelState.ON;

	private boolean cancelled;

	protected Vent() {
	}

	protected Vent(boolean isAsync) {
		this.async = isAsync;
	}

	@SuppressWarnings("SameParameterValue")
	protected Vent(boolean isAsync, int id) {
		this.id = id;
		this.async = isAsync;
	}

	protected final void setState(CancelState state) {
		this.state = state;
	}

	protected final void setHost(Plugin plugin) {
		if (this.plugin != null) throw new IllegalStateException("Plugin already initialized!");
		this.plugin = plugin;
	}

	public String getName() {
		return this.getType().getSimpleName();
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	protected CancelState getState() {
		return state;
	}

	public boolean isCancelled() {
		if (this.state == CancelState.OFF) return false;
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

	public static final class Link {

		private final Subscription<?> parent;

		protected final Set<Subscription<?>> subscriptions;

		public Link(Subscription<?> parent) {
			this.subscriptions = new HashSet<>();
			this.parent = parent;
		}

		public <T extends Vent> Link next(Subscription<T> subscription) {
			subscription.setParent(parent);
			subscriptions.add(subscription);
			return this;
		}

	}

	public static class Subscription<T extends Vent> {

		private final Class<T> eventType;
		private final SubscriberCall<T> action;
		private final Priority priority;
		private final Plugin user;
		private String key;
		private Subscription<?> parent;

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
				getMap().unsubscribe(eventType, key);

				getMap().subscriptions.forEach(s -> {

					if (s.getParent().equals(this)) {
						Schedule.sync(() -> getMap().subscriptions.remove(this)).waitReal(1);
					}

				});

			} else {
				getMap().subscriptions.forEach(s -> {

					if (s.getParent().equals(this)) {
						Schedule.sync(() -> getMap().subscriptions.remove(this)).waitReal(1);
					}

				});
				getMap().subscriptions.forEach(s -> {
					if (s.equals(this)) {
						Schedule.sync(() -> getMap().subscriptions.remove(this)).waitReal(1);
					}
				});
			}
		}

		public Subscription<?> getParent() {
			return parent;
		}

		protected void setParent(Subscription<?> parent) {
			this.parent = parent;
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

		public static final class Builder<T extends Vent> {

			private final Class<T> tClass;
			private Subscription<T> subscription;
			private String key;
			private Plugin plugin;
			private Priority priority;

			private Builder(Class<T> tClass) {
				this.tClass = tClass;
			}

			public static <T extends Vent> Builder<T> target(Class<T> event) {
				return new Builder<>(event);
			}

			public Builder<T> from(String key) {
				this.key = key;
				return this;
			}

			public Builder<T> from(Plugin plugin) {
				this.plugin = plugin;
				return this;
			}

			public Builder<T> assign(Priority priority) {
				this.priority = priority;
				return this;
			}

			public Builder<T> use(SubscriberCall<T> call) {
				if (this.key != null) {
					this.subscription = new Subscription<>(tClass, key, plugin, priority, call);
				}
				this.subscription = new Subscription<>(tClass, plugin, priority, call);
				return this;
			}

			public Subscription<T> finish() {
				return this.subscription;
			}

			public Subscription<T> assign(SubscriberCall<T> call) {
				if (this.key != null) {
					return new Subscription<>(tClass, key, plugin, priority, call);
				}
				return new Subscription<>(tClass, plugin, priority, call);
			}

		}



	}

	public static <T extends Vent> void subscribe(Subscription<T> subscription) {
		if (subscription == null) {
			LabyrinthProvider.getInstance().getLogger().severe("Null subscription found from unknown source (Not labyrinth).");
			return;
		}
		getMap().subscriptions.add(subscription);
	}

	public static void chain(Link link) {
		if (link == null) {
			LabyrinthProvider.getInstance().getLogger().severe("Null subscription link found from unknown source (Not labyrinth).");
			return;
		}
		Subscription<?> parent = null;
		for (Subscription<?> sub : link.subscriptions) {
			if (sub.getParent() != null) {
				parent = sub.getParent();
			}
			getMap().subscriptions.add(sub);
		}

		if (parent != null) {
			getMap().subscriptions.add(parent);
		}

	}

	public static <T extends Vent> void unsubscribeAll(Class<T> labyrinthEvent, Plugin user) {
		for (Subscription<?> s : getMap().subscriptions) {
			if (s.getEventType().isAssignableFrom(labyrinthEvent)) {
				if (s.getUser().equals(user)) {
					Schedule.sync(() -> getMap().subscriptions.remove(s)).run();
				}
			}
		}
	}

	public enum CancelState {
		ON, OFF
	}

	public enum Runtime {
		Synchronous, Asynchronous
	}

	public enum Priority {

		READ_ONLY(5),

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

		private boolean warned;

		private T copy;

		private final Runtime type;

		public Call(T event) {
			this.event = event;
			this.copy = event;
			this.type = Runtime.Synchronous;
		}

		public Call(Runtime type, T event) {
			this.event = event;
			this.copy = event;
			this.type = type;
		}

		public T run() {

			JavaPlugin plugin = JavaPlugin.getProvidingPlugin(event.getClass());
			VentMap map = getMap();

			switch (type) {
				case Synchronous:
					if (event.isAsynchronous()) throw new IllegalStateException("This event can only be run asynchronously");

					if (event.getHost() == null) {
						event.setHost(plugin);
					}

					map.subscriptions.stream().sorted(Comparator.comparingInt(value -> value.getPriority().getLevel())).forEach(s -> {

						if (s.getEventType().isAssignableFrom(event.getType())) {
							switch (s.getPriority()) {
								case LOW:
								case MEDIUM:
								case HIGH:
								case HIGHEST:
									if (event.getState() == CancelState.ON) {
										if (!event.isCancelled()) {
											((SubscriberCall<T>) s.getAction()).accept(event, (Subscription<T>) s);
											this.copy = event;
										}
									} else {
										((SubscriberCall<T>) s.getAction()).accept(event, (Subscription<T>) s);
										this.copy = event;
									}
									break;
								case READ_ONLY:
									((SubscriberCall<T>) s.getAction()).accept(copy, (Subscription<T>) s);
									break;
							}
						}

					});
					return event;

				case Asynchronous:
					if (!event.isAsynchronous()) throw new IllegalStateException("This event can only be run synchronously");

					event.setHost(plugin);

					return CompletableFuture.supplyAsync(() -> {

						if (event.getHost() == null) {
							event.setHost(plugin);
						}

						map.subscriptions.stream().sorted(Comparator.comparingInt(value -> value.getPriority().getLevel())).forEach(s -> {

							if (s.getEventType().isAssignableFrom(event.getType())) {
								switch (s.getPriority()) {
									case LOW:
									case MEDIUM:
									case HIGH:
									case HIGHEST:
										if (event.getState() == CancelState.ON) {
											if (!event.isCancelled()) {
												if (!warned) {
													LabyrinthProvider.getInstance().getLogger().warning("- Illegal asynchronous task call from plugin " + s.getUser().getName() + " for event " + event.getName());
													LabyrinthProvider.getInstance().getLogger().warning("- Recommended use is via Vent#complete()");
													this.warned = true;
												}
												((SubscriberCall<T>) s.getAction()).accept(event, (Subscription<T>) s);
												this.copy = event;
											}
										} else {
											if (!warned) {
												LabyrinthProvider.getInstance().getLogger().warning("- Illegal asynchronous task call from plugin " + s.getUser().getName() + " for event " + event.getName());
												LabyrinthProvider.getInstance().getLogger().warning("- Recommended use is via Vent#complete()");
												this.warned = true;
											}
											((SubscriberCall<T>) s.getAction()).accept(event, (Subscription<T>) s);
											this.copy = event;
										}
										break;
									case READ_ONLY:
										if (!warned) {
											LabyrinthProvider.getInstance().getLogger().warning("- Illegal asynchronous task call from plugin " + s.getUser().getName() + " for event " + event.getName());
											LabyrinthProvider.getInstance().getLogger().warning("- Recommended use is via Vent#complete()");
											this.warned = true;
										}
										((SubscriberCall<T>) s.getAction()).accept(copy, (Subscription<T>) s);
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

					if (!event.isAsynchronous()) throw new IllegalStateException("This event can only be run synchronously");

					if (event.getHost() == null) {
						event.setHost(plugin);
					}

					return CompletableFuture.supplyAsync(() -> {

						map.subscriptions.stream().sorted(Comparator.comparingInt(value -> value.getPriority().getLevel())).forEach(s -> {

							if (s.getEventType().isAssignableFrom(event.getType())) {
								switch (s.getPriority()) {
									case LOW:
									case MEDIUM:
									case HIGH:
									case HIGHEST:
										if (event.getState() == CancelState.ON) {
											if (!event.isCancelled()) {
												((SubscriberCall<T>) s.getAction()).accept(event, (Subscription<T>) s);
												this.copy = event;
											}
										} else {
											((SubscriberCall<T>) s.getAction()).accept(event, (Subscription<T>) s);
											this.copy = event;
										}
										break;
									case READ_ONLY:
										((SubscriberCall<T>) s.getAction()).accept(this.copy, (Subscription<T>) s);
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
	}

	private static VentMap getMap() {
		return LabyrinthProvider.getInstance().getEventMap();
	}
}
