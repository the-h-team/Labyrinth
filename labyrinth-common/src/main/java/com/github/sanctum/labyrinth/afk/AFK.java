package com.github.sanctum.labyrinth.afk;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.event.custom.DefaultEvent;
import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.task.Synchronous;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A class entirely responsible for handling away from keyboard users.
 */
public class AFK {

	private static final Set<AFK> HISTORY = new HashSet<>();

	private Position location;

	private Synchronous task;

	private long time = 0L;

	private Status status = Status.ACTIVE;

	private static Vent.Subscription<StatusChange> handler;

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
					.handle(new Vent.Subscription<>(StatusChange.class, LabyrinthProvider.getInstance().getPluginInstance(), Vent.Priority.HIGH, (e, subscription) -> {

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
								Bukkit.broadcastMessage(StringUtils.use("&r[&2Labyrinth&r] &c&oPlayer &b" + p.getName() + " &c&o" + "was kicked for being AFK too long.").translate());
								p.kickPlayer(StringUtils.use("&r[&2Labyrinth&r]" + "\n" + "&c&oAFK too long.\n&c&oKicking to ensure safety :)").translate());
								e.getAfk().cancel();
								break;
						}

					}))
					.stage(a -> a.getRecording().getMinutes() >= away,
							b -> b.getRecording().getMinutes() >= kick));
		}
	}

	public static Set<AFK> getHistory() {
		return HISTORY;
	}

	public static AFK from(Player player) {
		for (AFK afk : HISTORY) {
			if (afk.getPlayer().getUniqueId().equals(player.getUniqueId())) {
				return afk;
			}
		}
		return null;
	}

	public static Vent.Subscription<StatusChange> getHandler() {
		return handler;
	}

	/**
	 * Set the activity of this user to Active.
	 */
	public void saturate() {
		this.status = Status.ACTIVE;
		this.time = System.currentTimeMillis();
	}

	/**
	 * Completely remove this user's AFK trace from cache.
	 */
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

	public enum Status {

		/**
		 * The user is doing stuff and not afk.
		 */
		ACTIVE,
		/**
		 * The user has only recently been determined absent.
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
	 * Initialization object for building a plugin reference to a player's timeout request.
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
		 *
		 * @param handler the handler to use for message broadcasting logic
		 * @return this builder
		 */
		public Initializer handle(Vent.Subscription<StatusChange> handler) {
			if (AFK.handler == null) {
				AFK.handler = handler;
				Vent.subscribe(handler);
			}
			return this;
		}

		/**
		 * Setup the prerequisites to be met for each stage of listening.
		 *
		 * @param away the trigger for changing the status to {@link Status#AWAY}
		 * @param kick the trigger for changing the status to {@link Status#REMOVABLE}
		 * @return the initialized and cached object reference
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
								new Vent.Call<>(new StatusChange(afk, Status.AWAY)).run();
							}
							if (kick.accept(afk)) {
								afk.status = Status.REMOVABLE;
								new Vent.Call<>(new StatusChange(afk, Status.REMOVABLE)).run();
							}
						}
					} else {
						if (afk.status == Status.AWAY) {
							afk.location = null;
							afk.time = 0;
							afk.status = Status.RETURNING;
							new Vent.Call<>(new StatusChange(afk, Status.RETURNING)).run();
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

	public static class StatusChange extends DefaultEvent.Player {

		private final AFK afk;

		private final Status status;

		public StatusChange(AFK afk, Status status) {
			super(afk.player, false);
			this.afk = afk;
			this.status = status;
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

	/**
	 * A status trigger, define afk stages.
	 *
	 * @param <T> the event this trigger is for
	 */
	@FunctionalInterface
	public interface StatusTrigger<T> {
		T accept(AFK afk);
	}
}
