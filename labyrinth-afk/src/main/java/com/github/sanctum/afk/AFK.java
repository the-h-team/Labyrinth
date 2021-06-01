package com.github.sanctum.afk;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.task.Synchronous;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

public class AFK {

	private static final Set<AFK> HISTORY = new HashSet<>();

	private Position location;

	private Plugin holder;

	private Synchronous task;

	private long time = 0L;

	private Status status = Status.ACTIVE;

	private Handler handler;

	private final Player player;

	protected AFK(Player player) {
		this.player = player;
		HISTORY.add(this);
	}

	public static boolean found(Player player) {
		return from(player) != null;
	}

	public static Optional<AFK> supply(Player player, int away, int kick) {
		if (found(player)) {
			return Optional.ofNullable(from(player));
		} else {
			try {
				return Optional.of(Initializer.initialize(player)
						.handle(Labyrinth.getInstance(), Handler::standard)
						.stage(a -> TimeUnit.SECONDS.toMinutes(a.getWatch().interval(Instant.now()).getSeconds()) >= away, b -> TimeUnit.SECONDS.toMinutes(b.getWatch().interval(Instant.now()).getSeconds()) >= kick));
			} catch (InstantiationException ex) {
				return Optional.empty();
			}
		}
	}

	public static AFK from(Player player) {
		for (AFK afk : HISTORY) {
			if (afk.getPlayer().getUniqueId().equals(player.getUniqueId())) {
				return afk;
			}
		}
		return null;
	}

	public void unregister() {
		List<Listener> list = HandlerList.getRegisteredListeners(getHolder()).stream().filter(r -> r.getListener().equals(this.handler)).map(RegisteredListener::getListener).collect(Collectors.toList());
		for (Listener l : list) {
			HandlerList.unregisterAll(l);
		}
		this.handler = null;
	}

	public void cancel() {
		unregister();
		Schedule.sync(() -> HISTORY.removeIf(a -> a.getPlayer().equals(getPlayer()))).run();
	}

	public TimeWatch getWatch() {
		return TimeWatch.start(this.time);
	}

	public TimeWatch.Recording getRecording() {
		return TimeWatch.Recording.subtract(this.time);
	}

	public Position getLocation() {
		return location;
	}

	public Plugin getHolder() {
		return holder;
	}

	public Player getPlayer() {
		return player;
	}

	public interface Handler extends Listener {

		void execute(StatusChange e);

		static Handler standard() {
			return e -> {
				Player p = e.getAfk().getPlayer();
				switch (e.getStatus()) {
					case AWAY:
						Bukkit.broadcastMessage(StringUtils.use("&r[&2Labyrinth&r] &7Player &b" + p.getName() + " &7is now AFK").translate());
						p.setDisplayName(StringUtils.use("&7*AFK&r " + p.getDisplayName()).translate());
						break;
					case ACTIVE:
						p.setDisplayName(p.getName());
						Bukkit.broadcastMessage(StringUtils.use("&r[&2Labyrinth&r] &7Player &b" + p.getName() + " &7is no longer AFK").translate());
						break;
					case REMOVABLE:
						Bukkit.broadcastMessage(StringUtils.use("&r[&2Labyrinth&r] &c&oPlayer &b" + p.getName() + " &c&owas kicked for being AFK too long.").translate());
						p.kickPlayer(StringUtils.use("&r[&2Labyrinth&r]" + "\n" + "&c&oAFK too long.\n&c&oKicking to ensure safety :)").translate());
						e.getAfk().cancel();
						break;
				}
			};
		}

	}

	public enum Status {

		/**
		 * The user is doing stuff and not afk.
		 */
		ACTIVE,
		/**
		 * The user has only recently been marked gone.
		 */
		AWAY,
		/**
		 * The user is safe to remove.
		 */
		REMOVABLE

	}

	/**
	 * Initialization object for building a plugin reference to a player's time-out request.
	 */
	public static class Initializer {

		private final AFK afk;

		protected Initializer(Player instance) {
			this.afk = new AFK(instance);
		}

		public static Initializer initialize(Player player) {
			return new Initializer(player);
		}

		/**
		 * Apply the reading logic for the status changing events.
		 * <p>
		 * param plugin The plugin to register the handler and instance under.
		 *
		 * @param handler The handler to use for message broadcasting logic.
		 * @return The same afk initialization builder.
		 * @throws InstantiationException   If the event handler is already registered.
		 * @throws IllegalArgumentException If the plugin is null.
		 */
		public Initializer handle(Plugin plugin, Supplier<Handler> handler) throws InstantiationException, IllegalArgumentException {
			if (this.afk.handler != null)
				throw new InstantiationException("Handler is already registered. Cannot override current installation");

			if (plugin == null)
				throw new IllegalArgumentException("Plugin cannot be null!");

			this.afk.handler = handler.get();
			this.afk.holder = plugin;
			Bukkit.getPluginManager().registerEvents(this.afk.handler, plugin);
			return this;
		}

		/**
		 * Apply the reading logic for the status changing events.
		 * <p>
		 * param plugin The plugin to register the handler and instance under.
		 *
		 * @param handler The handler to use for message broadcasting logic.
		 * @return The same afk initialization builder.
		 * @throws InstantiationException   If the event handler is already registered.
		 * @throws IllegalArgumentException If the plugin is null.
		 */
		public Initializer handle(Plugin plugin, Handler handler) throws InstantiationException, IllegalArgumentException {
			if (this.afk.handler != null)
				throw new InstantiationException("Handler is already registered. Cannot override current installation");

			if (plugin == null)
				throw new IllegalArgumentException("Plugin cannot be null!");

			this.afk.handler = handler;
			this.afk.holder = plugin;
			Bukkit.getPluginManager().registerEvents(this.afk.handler, plugin);
			return this;
		}

		/**
		 * Setup the pre-requisites to be met each stage of listening.
		 *
		 * @param away The trigger for changing the status to {@link Status#AWAY}
		 * @param kick The trigger for changing the status to {@link Status#REMOVABLE}
		 * @return The initialized & cached object reference
		 * @throws InstantiationException If some how the task is already scheduled.
		 */
		public AFK stage(final StatusTrigger<Boolean> away, final StatusTrigger<Boolean> kick) throws InstantiationException {

			if (this.afk.task != null)
				throw new InstantiationException("A scheduled query operation is already under effect!");

			this.afk.task = Schedule.sync(() -> {
				if (this.afk.location == null) {
					this.afk.time = System.currentTimeMillis();
					this.afk.location = new Position(this.afk.getPlayer().getLocation().getBlockX(), this.afk.getPlayer().getLocation().getBlockY(), this.afk.getPlayer().getLocation().getBlockZ(), this.afk.getPlayer().getLocation().getYaw(), this.afk.getPlayer().getLocation().getPitch());
				} else {
					if (Position.matches(this.afk.getLocation(), this.afk.getPlayer().getLocation())) {
						if (away.accept(this.afk)) {
							if (this.afk.status == Status.ACTIVE) {
								this.afk.status = Status.AWAY;
								StatusChange event = new StatusChange(this.afk, this.afk.status);
								Bukkit.getPluginManager().callEvent(event);
							}
							if (kick.accept(this.afk)) {
								this.afk.status = Status.REMOVABLE;
								StatusChange event = new StatusChange(this.afk, this.afk.status);
								Bukkit.getPluginManager().callEvent(event);
							}
						}
					} else {
						this.afk.location = null;
						this.afk.status = Status.ACTIVE;
						StatusChange event = new StatusChange(this.afk, this.afk.status);
						Bukkit.getPluginManager().callEvent(event);
					}
				}

			}).cancelAfter(this.afk.getPlayer());
			this.afk.task.repeatReal(0, 20);
			return this.afk;
		}

	}

	public static class StatusChange extends Event {

		private final AFK afk;

		private final Status status;

		private static final HandlerList list = new HandlerList();

		public StatusChange(AFK afk, Status status) {
			this.afk = afk;
			this.status = status;
		}

		@Override
		public @NotNull HandlerList getHandlers() {
			return list;
		}

		public static HandlerList getHandlerList() {
			return list;
		}

		public AFK getAfk() {
			return afk;
		}

		public Status getStatus() {
			return status;
		}
	}

	public static class Position {

		private final long x;

		private final long y;

		private final long z;

		private final float yaw;

		private final float pitch;

		public Position(long x, long y, long z, float yaw, float pitch) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.yaw = yaw;
			this.pitch = pitch;
		}

		public float getPitch() {
			return pitch;
		}

		public float getYaw() {
			return yaw;
		}

		public long getX() {
			return x;
		}

		public long getY() {
			return y;
		}

		public long getZ() {
			return z;
		}

		public static boolean matches(Position c, Location l) {

			if (c == null || l == null) return false;

			return l.getBlockX() == c.getX() && l.getBlockY() == c.getY() && l.getBlockZ() == c.getZ() && l.getYaw() == c.getYaw() && l.getPitch() == c.getPitch();
		}

	}

	@FunctionalInterface
	public interface StatusTrigger<T> {
		T accept(AFK afk);

	}
}
