package com.github.sanctum.afk;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.task.Synchronous;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class AFK {

	private static final Set<AFK> HISTORY = new HashSet<>();

	private Position location;

	private Plugin holder;

	private Synchronous task;

	private long time = 0L;

	private Status status = Status.ACTIVE;

	private static Handler handler;

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
				Initializer.next()
						.handle(Labyrinth.getInstance(), Handler::standard);
				return Optional.of(new AFK(player).stage(a -> TimeUnit.SECONDS.toMinutes(a.getWatch().interval(Instant.now()).getSeconds()) >= away, b -> TimeUnit.SECONDS.toMinutes(b.getWatch().interval(Instant.now()).getSeconds()) >= kick));
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

	/**
	 * Setup the pre-requisites to be met each stage of listening.
	 *
	 * @param away The trigger for changing the status to {@link Status#AWAY}
	 * @param kick The trigger for changing the status to {@link Status#REMOVABLE}
	 * @return The initialized & cached object reference
	 * @throws InstantiationException If some how the task is already scheduled.
	 */
	public AFK stage(final StatusTrigger<Boolean> away, final StatusTrigger<Boolean> kick) throws InstantiationException {

		if (this.task != null)
			throw new InstantiationException("A scheduled query operation is already under effect!");

		this.task = Schedule.sync(() -> {
			if (this.time == 0) {
				this.time = System.currentTimeMillis();
				this.location = new Position(this.getPlayer().getLocation().getBlockX(), this.getPlayer().getLocation().getBlockY(), this.getPlayer().getLocation().getBlockZ(), this.getPlayer().getLocation().getYaw(), this.getPlayer().getLocation().getPitch());
			} else {
				if (Position.matches(this.getLocation(), this.getPlayer().getLocation())) {
					if (away.accept(this)) {
						if (this.status == Status.ACTIVE) {
							this.status = Status.AWAY;
							StatusChange event = new StatusChange(this, Status.AWAY);
							Bukkit.getPluginManager().callEvent(event);
						}
						if (kick.accept(this)) {
							this.status = Status.REMOVABLE;
							StatusChange event = new StatusChange(this, Status.REMOVABLE);
							Bukkit.getPluginManager().callEvent(event);
						}
					}
				} else {
					if (this.status == Status.AWAY) {
						this.location = null;
						this.time = 0;
						this.status = Status.RETURNING;
						StatusChange event = new StatusChange(this, Status.RETURNING);
						Bukkit.getPluginManager().callEvent(event);
					} else {
						this.time = 0;
					}
				}
			}

		}).cancelAfter(getPlayer());
		this.task.repeatReal(0, 12);
		return this;
	}

	public void saturate() {
		this.status = Status.ACTIVE;
	}

	public void cancel() {
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
			return new AFK.Handler() {
				@Override
				@EventHandler
				public void execute(AFK.StatusChange e) {
					Player p = e.getAfk().getPlayer();
					switch (e.getStatus()) {
						case AWAY:
							Bukkit.broadcastMessage(StringUtils.use("&r[&2Labyrinth&r] &7Player &b" + p.getName() + " &7is now AFK").translate());
							p.setDisplayName(StringUtils.use("&7*AFK&r " + p.getDisplayName()).translate());
							break;
						case RETURNING:
							p.setDisplayName(p.getName());
							Bukkit.broadcastMessage(StringUtils.use("&r[&2Labyrinth&r] &7Player &b" + p.getName() + " &7is no longer AFK").translate());
							e.getAfk().saturate();
							break;
						case REMOVABLE:
							Bukkit.broadcastMessage(StringUtils.use("&r[&2Labyrinth&r] &c&oPlayer &b" + p.getName() + " &c&owas kicked for being AFK too long.").translate());
							p.kickPlayer(StringUtils.use("&r[&2Labyrinth&r]" + "\n" + "&c&oAFK too long.\n&c&oKicking to ensure safety :)").translate());
							e.getAfk().cancel();
							break;
					}
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
		 * The user is coming back from being AFK.
		 */
		RETURNING,
		/**
		 * The user is safe to remove.
		 */
		REMOVABLE

	}

	/**
	 * Initialization object for building a plugin reference to a player's time-out request.
	 */
	public static class Initializer {

		protected Initializer() {
		}

		public static Initializer next() {
			return new Initializer();
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
			if (AFK.handler != null)
				throw new InstantiationException("Handler is already registered. Cannot override current installation");

			if (plugin == null)
				throw new IllegalArgumentException("Plugin cannot be null!");

			if (StatusChange.getHandlerList().getRegisteredListeners().length == 0) {
				AFK.handler = handler.get();
				Bukkit.getPluginManager().registerEvents(AFK.handler, plugin);
			}
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
			if (AFK.handler != null)
				throw new InstantiationException("Handler is already registered. Cannot override current installation");

			if (plugin == null)
				throw new IllegalArgumentException("Plugin cannot be null!");

			if (StatusChange.getHandlerList().getRegisteredListeners().length == 0) {
				AFK.handler = handler;
				Bukkit.getPluginManager().registerEvents(AFK.handler, plugin);
			}
			return this;
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
