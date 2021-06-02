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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AFK {

	private static final Set<AFK> HISTORY = new HashSet<>();

	private Position location;

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
			return Optional.of(Initializer.next(player)
					.handle(Labyrinth.getInstance(), Handler::standard)
					.stage(a -> TimeUnit.SECONDS.toMinutes(a.getWatch().interval(Instant.now()).getSeconds()) >= away, b -> TimeUnit.SECONDS.toMinutes(b.getWatch().interval(Instant.now()).getSeconds()) >= kick));
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

	public static Handler getHandler() {
		return handler;
	}

	public void saturate() {
		this.status = Status.ACTIVE;
		this.time = System.currentTimeMillis();
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

	public Status getStatus() {
		return status;
	}

	public Player getPlayer() {
		return player;
	}

	public interface Handler extends Listener {

		void execute(StatusChange e);

		default Plugin getPlugin() {
			return JavaPlugin.getProvidingPlugin(getClass());
		}

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

		private final Player player;

		protected Initializer(Player player) {
			this.player = player;
		}

		public static Initializer next(Player player) {
			return new Initializer(player);
		}

		/**
		 * Apply the reading logic for the status changing events.
		 * <p>
		 * param plugin The plugin to register the handler and instance under.
		 *
		 * @param handler The handler to use for message broadcasting logic.
		 * @return The same afk initialization builder.
		 */
		public Initializer handle(Plugin plugin, Supplier<Handler> handler) {
			if (AFK.handler == null) {

				if (plugin == null)
					throw new IllegalArgumentException("Plugin cannot be null!");

				if (StatusChange.getHandlerList().getRegisteredListeners().length == 0) {
					AFK.handler = handler.get();
					Bukkit.getPluginManager().registerEvents(AFK.handler, plugin);
				}
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
		 */
		public Initializer handle(Plugin plugin, Handler handler) {
			if (AFK.handler == null) {

				if (plugin == null)
					throw new IllegalArgumentException("Plugin cannot be null!");

				if (StatusChange.getHandlerList().getRegisteredListeners().length == 0) {
					AFK.handler = handler;
					Bukkit.getPluginManager().registerEvents(AFK.handler, plugin);
				}
			}
			return this;
		}

		/**
		 * Setup the pre-requisites to be met each stage of listening.
		 *
		 * @param away The trigger for changing the status to {@link Status#AWAY}
		 * @param kick The trigger for changing the status to {@link Status#REMOVABLE}
		 * @return The initialized & cached object reference
		 */
		public AFK stage(final StatusTrigger<Boolean> away, final StatusTrigger<Boolean> kick) {
			final AFK afk = new AFK(this.player);

			afk.task = Schedule.sync(() -> {
				if (afk.time == 0) {
					afk.time = System.currentTimeMillis();
					afk.location = new Position(afk.getPlayer().getLocation().getBlockX(), afk.getPlayer().getLocation().getBlockY(), afk.getPlayer().getLocation().getBlockZ(), afk.getPlayer().getLocation().getYaw(), afk.getPlayer().getLocation().getPitch());
				} else {
					if (Position.matches(afk.getLocation(), afk.getPlayer().getLocation())) {
						if (away.accept(afk)) {
							if (afk.status == Status.ACTIVE) {
								afk.status = Status.AWAY;
								StatusChange event = new StatusChange(afk, Status.AWAY);
								Bukkit.getPluginManager().callEvent(event);
							}
							if (kick.accept(afk)) {
								afk.status = Status.REMOVABLE;
								StatusChange event = new StatusChange(afk, Status.REMOVABLE);
								Bukkit.getPluginManager().callEvent(event);
							}
						}
					} else {
						if (afk.status == Status.AWAY) {
							afk.location = null;
							afk.time = 0;
							afk.status = Status.RETURNING;
							StatusChange event = new StatusChange(afk, Status.RETURNING);
							Bukkit.getPluginManager().callEvent(event);
						} else {
							afk.time = 0;
						}
					}
				}

			}).cancelAfter(afk.getPlayer());
			afk.task.repeatReal(0, 12);
			return afk;
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
