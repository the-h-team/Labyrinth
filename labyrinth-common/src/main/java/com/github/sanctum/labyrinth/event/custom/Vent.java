package com.github.sanctum.labyrinth.event.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.github.sanctum.labyrinth.LabyrinthProvider;

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

	/**
	 * @deprecated use {@link VentMap#register(Plugin, Object)} instead!
	 */
	@Deprecated
	public static void register(@NotNull Plugin host, @NotNull Object listener) {
		getMap().subscribe(host, listener);
	}

	public VentMap getVentMap() {
		return getMap();
	}

	public static class Link {

		private final Subscription<?> parent;

		protected final Set<Subscription<?>> subscriptions;

		public Link(Subscription<?> parent) {
			parent.p = true;
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

	//Check if #getClass also works, this method may be deprecated soon™
	@SuppressWarnings("unchecked")
	public <T extends Vent> Class<T> getType() {
		return (Class<T>) getClass();
	}

	public enum Runtime {
		Synchronous, Asynchronous;

		/**
		 * use this on a given runtime to validate that it is able to run the passed event
		 *
		 * @param vent the event that could be run
		 * @throws SubscriptionRuntimeException if the events runtime mismatches this runtime
		 */
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

	public enum CancelState {
		ON, OFF
	}

	/**
	 * Event handler priority.
	 * LOWER means that it will be run earlier.
	 */
	public enum Priority {

		LOW(1),

		MEDIUM(2),

		HIGH(3),

		HIGHEST(4),

		READ_ONLY(5);

		/**
		 * A list containing all priorities that have write-access to events.
		 */
		private static final List<Priority> writeAccessing =
				Collections.unmodifiableList(Stream.of(LOW, MEDIUM, HIGH, HIGHEST).collect(Collectors.toList()));

		private final int level;

		Priority(int level) {
			this.level = level;
		}

		public static List<Priority> getWriteAccessing() {
			return writeAccessing;
		}

		public int getLevel() {
			return level;
		}
	}

	public static class Subscription<T extends Vent> {

		private final Class<T> eventType;
		private final SubscriberCall<T> action;
		private final Priority priority;
		private final Plugin user;
		private String key;
		protected boolean p;
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
			getMap().unsubscribe(this);
		}

		public Subscription<?> getParent() {
			return parent;
		}

		protected void setParent(Subscription<?> parent) {
			this.p = false;
			this.parent = parent;
		}

		public boolean isParent() {
			return this.p;
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
			private SubscriberCall<T> subscriberCall;
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
				this.subscriberCall = call;
				return this;
			}

			/**
			 * Builds the subscription and registers at the VentMap service, if not done previously.
			 * Otherwise, it just returns the built subscription.
			 *
			 * @return the built subscription
			 */
			public Subscription<T> finish() throws IllegalStateException {
				boolean register = subscription == null;
				if (register) {
					validate();
					if (this.key != null) {
						this.subscription = new Subscription<>(tClass, key, plugin, priority, subscriberCall);
					} else {
						this.subscription = new Subscription<>(tClass, plugin, priority, subscriberCall);
					}
					getMap().subscribe(this.subscription);
				}
				return this.subscription;
			}

			private void validate() throws IllegalStateException {
				if (Stream.of(plugin, priority).anyMatch(Objects::isNull)) {
					throw new IllegalStateException("There are still unassigned builds needed " +
													"to build a Subscription!");
				}
			}

		}


	}

	public static class Call<T extends Vent> {

		private final T event;

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

		public Supplier<T> run(Runtime runtime) {
			runtime.validate(event);
			switch (runtime) {
				case Synchronous: {
					T result = run();
					return () -> result;
				}
				case Asynchronous: {
					final CompletableFuture<T> complete = complete();
					return () -> {
						try {
							return complete.get();
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
							return event;
						}
					};
				}
				default:
					throw new SubscriptionRuntimeException("An invalid runtime was provided!");

			}
		}

		public T run() {
			type.validate(event);
			JavaPlugin plugin = JavaPlugin.getProvidingPlugin(event.getClass());
			switch (type) {
				case Synchronous:
					if (event.getHost() == null) {
						event.setHost(plugin);
					}
					notifySubscribersAndListeners();
					return event;

				case Asynchronous:

					event.setHost(plugin);

					return CompletableFuture.supplyAsync(() -> {
						if (event.getHost() == null) {
							event.setHost(plugin);
						}
						LabyrinthProvider.getInstance().getLogger()
								.warning("- Illegal asynchronous event call from plugin " + plugin.getName()
										 + " for event " + event.getName());
						LabyrinthProvider.getInstance().getLogger()
								.warning("- Recommended use is via Vent#complete()");
						notifySubscribersAndListeners();
						return event;

					}).join();
				default:
					throw new SubscriptionRuntimeException("An invalid runtime was provided!");
			}
		}


		public CompletableFuture<T> complete() {
			if (type == Runtime.Synchronous)
				throw new SubscriptionRuntimeException("An invalid runtime was provided!");
			type.validate(event);
			JavaPlugin plugin = JavaPlugin.getProvidingPlugin(event.getClass());
			if (event.getHost() == null) {
				event.setHost(plugin);
			}
			return CompletableFuture.supplyAsync(() -> {
				notifySubscribersAndListeners();
				return event;

			});

		}

		@SuppressWarnings("unchecked")
		private void notifySubscribersAndListeners() {
			VentMap map = getMap();
			List<VentListener> listeners = map.getListeners();
			List<Class<? extends Vent>> assignableClasses = generateAssignableClasses(event.getClass());
			Priority.getWriteAccessing().forEach(p -> assignableClasses.forEach(c -> {
				map.getSubscriptions(c, p).map(s -> (Subscription<? super T>) s)
						.forEachOrdered(subscription -> runSubscription(subscription, event));
				listeners.forEach(l -> notifyListeners(l, c, p));
			}));
			assignableClasses.forEach(c -> {
				map.getSubscriptions(c, Priority.READ_ONLY)
						.map(s -> (Subscription<? super T>) s)
						.forEachOrdered(s -> runSubscriptionReadOnly(s, copy));
				listeners.forEach(listener -> runReadOnly(listener, c));
			});
		}

		private <E extends Vent> void notifyListeners(VentListener listener, Class<E> eventSuperClass,
													  Priority priority) {
			E vent = eventSuperClass.cast(event);
			listener.getHandlers(eventSuperClass, priority).forEachOrdered(e -> {
				if (vent.getState() == CancelState.ON && !e.handlesCancelled()) {
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
				boolean cancelled = copy.isCancelled();
				if (!cancelled || e.handlesCancelled()) {
					e.accept(eventSuperClass.cast(copy), null);
					if (copy.isCancelled()) {
						copy.setCancelled(cancelled);
					}
				}
			});
		}

		private void runSubscription(Subscription<? super T> subscription, T event) {
			if (event.getState() != CancelState.ON || !event.isCancelled()) {
				if (!event.isCancelled()) {
					runSub(subscription, event);
				}
			}
		}

		private void runSubscriptionReadOnly(Subscription<? super T> subscription, T event) {
			if (!event.isCancelled()) {
				runSub(subscription, event);
				if (event.isCancelled())
					event.setCancelled(false);
			}
		}

		private <S extends Vent> void runSub(Subscription<S> subscription, S event) {
			subscription.getAction().accept(event, subscription);
		}

		@SuppressWarnings("unchecked")
		private List<Class<? extends Vent>> generateAssignableClasses(Class<? extends Vent> ventClass) {
			List<Class<? extends Vent>> callingClasses = new ArrayList<>();
			Class<? extends Vent> temp = event.getType();
			do {
				callingClasses.add(temp);
				temp = (Class<? extends Vent>) temp.getSuperclass();
			} while (Vent.class.isAssignableFrom(temp));
			return callingClasses;
		}

	}

	private static VentMap getMap() {
		return LabyrinthProvider.getInstance().getEventMap();
	}
}
