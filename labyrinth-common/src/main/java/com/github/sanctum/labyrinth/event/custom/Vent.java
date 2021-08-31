package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.task.Schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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

	public final Runtime getRuntime() {
		return async ? Runtime.Asynchronous : Runtime.Synchronous;
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

	public VentMap getVentMap() {
		return getMap();
	}

	public static class Link {

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

		public Link next(Subscription<?>... subscription) {
			for (Subscription<?> sub : subscription) {
				sub.setParent(parent);
				subscriptions.add(sub);
			}
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

		//FIXME
		public void remove() {
			if (key != null) {
				getMap().unsubscribe(eventType, key);

				getMap().getSubscriptions().forEach(s -> {

					if (s.getParent().equals(this)) {
						Schedule.sync(() -> getMap().getSubscriptions().remove(this)).waitReal(1);
					}

				});

			} else {
				getMap().getSubscriptions().forEach(s -> {

					if (s.getParent().equals(this)) {
						Schedule.sync(() -> getMap().getSubscriptions().remove(this)).waitReal(1);
					}

				});
				getMap().getSubscriptions().forEach(s -> {
					if (s.equals(this)) {
						Schedule.sync(() -> getMap().getSubscriptions().remove(this)).waitReal(1);
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
				getMap().getSubscriptions().add(this.subscription);
				return this.subscription;
			}

		}


	}

	/**
	 * @deprecated use {@link VentMap#register(Plugin, Object)} instead!
	 */
	@Deprecated
	public static void register(@NotNull Plugin host, @NotNull Object listener) {
		getMap().register(host, listener);
	}

	/**
	 * @deprecated use {@link VentMap#registerAll(Plugin, Object...)} instead!
	 */
	@Deprecated
	public static void registerAll(@NotNull Plugin host, @NotNull Object... listeners) {
		getMap().registerAll(host, listeners);

	}

	/**
	 * @deprecated use {@link VentMap#subscribe(Subscription)} instead!
	 */
	@Deprecated
	public static <T extends Vent> void subscribe(Subscription<T> subscription) {
		getMap().subscribe(subscription);
	}

	/**
	 * @deprecated use {@link VentMap#chain(Link)} instead!
	 */
	@Deprecated
	public static void chain(Link link) {
		getMap().chain(link);
	}

	public enum CancelState {
		ON, OFF
	}

	public enum Runtime {
		Synchronous, Asynchronous;

		public void validate(Vent vent) throws SubscriptionRuntimeException {
			if (vent.getRuntime() != this) {
				throw new SubscriptionRuntimeException("Vent was tried to run " + this +
													   " but only can be run " + vent.getRuntime());
			}
		}

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

	public enum Priority {

		LOW(1),

		MEDIUM(2),

		HIGH(3),

		HIGHEST(4),

		READ_ONLY(5);

		private static final List<Priority> writeAccessing =
				Collections.unmodifiableList(Stream.of(LOW, MEDIUM, HIGH, HIGHEST).collect(Collectors.toList()));

		private final int level;

		Priority(int level) {
			this.level = level;
		}

		public int getLevel() {
			return level;
		}

		public static List<Priority> getWriteAccessing() {
			return writeAccessing;
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

			type.validate(event);

			JavaPlugin plugin = JavaPlugin.getProvidingPlugin(event.getClass());
			VentMap map = getMap();
			List<VentListener> listeners = map.getListeners();

			switch (type) {
				case Synchronous:
					if (event.getHost() == null) {
						event.setHost(plugin);
					}

					map.getSubscriptions().stream().sorted(Comparator.comparingInt(value -> value.getPriority().getLevel())).forEach(s -> {

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
									if (!copy.isCancelled()) {
										((SubscriberCall<T>) s.getAction()).accept(copy, (Subscription<T>) s);
										if (copy.isCancelled()) {
											copy.setCancelled(false);
										}
									}
									break;
							}
						}

					});

					runListenerCalls(listeners);

					return event;

				case Asynchronous:

					event.setHost(plugin);

					return CompletableFuture.supplyAsync(() -> {

						if (event.getHost() == null) {
							event.setHost(plugin);
						}

						map.getSubscriptions().stream().sorted(Comparator.comparingInt(value -> value.getPriority().getLevel())).forEach(s -> {

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
										if (!copy.isCancelled()) {
											((SubscriberCall<T>) s.getAction()).accept(copy, (Subscription<T>) s);
											if (copy.isCancelled()) {
												copy.setCancelled(false);
											}
										}
										break;
								}
							}

						});

						runListenerCalls(listeners);

						return event;

					}).join();
				default:
					throw new SubscriptionRuntimeException("An invalid runtime was provided!");
			}
		}


		public CompletableFuture<T> complete() {

			JavaPlugin plugin = JavaPlugin.getProvidingPlugin(event.getClass());
			VentMap map = getMap();
			List<VentListener> listeners = map.getListeners();
			switch (this.type) {
				case Asynchronous:

					if (!event.isAsynchronous())
						throw new IllegalStateException("This event can only be run synchronously");

					if (event.getHost() == null) {
						event.setHost(plugin);
					}

					return CompletableFuture.supplyAsync(() -> {

						map.getSubscriptions().stream().sorted(Comparator.comparing(Subscription::getPriority, Priority::compareTo)).forEach(s -> {

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
										if (!copy.isCancelled()) {
											((SubscriberCall<T>) s.getAction()).accept(this.copy, (Subscription<T>) s);
											if (copy.isCancelled()) {
												copy.setCancelled(false);
											}
										}
										break;
								}
							}

						});

						runListenerCalls(listeners);
						return event;

					});

				case Synchronous:
				default:
					throw new SubscriptionRuntimeException("An invalid runtime was provided!");
			}
		}

		@SuppressWarnings("unchecked")
		private void runListenerCalls(List<VentListener> listeners) {
			List<Class<? extends Vent>> callingClasses = new ArrayList<>();
			Class<? extends Vent> temp = event.getType();
			do {
				callingClasses.add(temp);
				temp = (Class<? extends Vent>) temp.getSuperclass();
			} while (Vent.class.isAssignableFrom(temp));

			if (!listeners.isEmpty()) {
				for (Priority priority : Priority.getWriteAccessing()) {
					for (VentListener o : listeners) {
						callingClasses.forEach(ventClass -> runEvents(o, ventClass, priority));
					}
				}
			}
			callingClasses.forEach(aClass -> listeners.forEach(l -> runReadOnly(l, aClass)));
		}

		private <E extends Vent> void runEvents(VentListener listener, Class<E> eventSuperClass, Priority priority) {
			E vent = eventSuperClass.cast(event);
			listener.getHandlers(eventSuperClass, priority).forEachOrdered(e -> {
				if (vent.getState() == CancelState.ON) {
					if (!vent.isCancelled()) {
						e.accept(vent, null);
						this.copy = event;
					}
				} else {
					e.accept(vent, null);
					this.copy = event;
				}
			});

		}

		private <E extends Vent> void runReadOnly(VentListener listener, Class<E> eventSuperClass) {
			listener.getHandlers(eventSuperClass, Priority.READ_ONLY).forEachOrdered(e -> {
				if (!copy.isCancelled()) {
					e.accept(eventSuperClass.cast(copy), null);
					if (copy.isCancelled()) {
						copy.setCancelled(false);
					}
				}
			});
		}

	}

	private static VentMap getMap() {
		return LabyrinthProvider.getInstance().getEventMap();
	}
}
