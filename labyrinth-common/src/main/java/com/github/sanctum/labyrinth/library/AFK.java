package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.event.custom.DefaultEvent;
import com.github.sanctum.labyrinth.event.custom.SubscriberCall;
import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.event.custom.VentMap;
import com.github.sanctum.labyrinth.task.Task;
import com.github.sanctum.labyrinth.task.TaskMonitor;
import com.github.sanctum.labyrinth.task.TaskPredicate;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.labyrinth.task.RenderedTask;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A class entirely responsible for handling away from keyboard users.
 */
public class AFK {

	private static final Set<AFK> HISTORY = new HashSet<>();

	private Position location;

	private long time = 0L;

	private Status status = Status.ACTIVE;

	private final Player player;

	protected AFK(Player player) {
		this.player = player;
	}

	public static AFK supply(Player player) {
		if (player == null) return null;
		return HISTORY.stream().filter(a -> a.player.equals(player)).findFirst().orElseGet(() -> Initializer.use(player)
				.next(LabyrinthProvider.getInstance().getPluginInstance())
				.next((e, subscription) -> {
					Player p = e.getAfk().getPlayer();
					switch (e.getStatus()) {
						case AWAY:
							TimeWatch.Recording recording = e.getAfk().getRecording();
							long minutes = recording.getMinutes();
							long seconds = recording.getSeconds();
							String format = "&cYou will be kicked in &4{0} &cseconds.";
							if (minutes == 14) {
								if (seconds == 50) {
									p.sendTitle(StringUtils.use("&eHey AFK person!").translate(), StringUtils.use(MessageFormat.format(format, 10)).translate(), 0, 12, 5);
								}
								if (seconds == 51) {
									p.sendTitle(StringUtils.use("&eHey AFK person!").translate(), StringUtils.use(MessageFormat.format(format, 9)).translate(), 0, 12, 5);
								}
								if (seconds == 52) {
									p.sendTitle(StringUtils.use("&eHey AFK person!").translate(), StringUtils.use(MessageFormat.format(format, 8)).translate(), 0, 12, 5);
								}
								if (seconds == 53) {
									p.sendTitle(StringUtils.use("&eHey AFK person!").translate(), StringUtils.use(MessageFormat.format(format, 7)).translate(), 0, 12, 5);
								}
								if (seconds == 54) {
									p.sendTitle(StringUtils.use("&eHey AFK person!").translate(), StringUtils.use(MessageFormat.format(format, 6)).translate(), 0, 12, 5);
								}
								if (seconds == 55) {
									p.sendTitle(StringUtils.use("&eHey AFK person!").translate(), StringUtils.use(MessageFormat.format(format, 5)).translate(), 0, 12, 5);
								}
								if (seconds == 56) {
									p.sendTitle(StringUtils.use("&eHey AFK person!").translate(), StringUtils.use(MessageFormat.format(format, 4)).translate(), 0, 12, 5);
								}
								if (seconds == 57) {
									p.sendTitle(StringUtils.use("&eHey AFK person!").translate(), StringUtils.use(MessageFormat.format(format, 3)).translate(), 0, 12, 5);
								}
								if (seconds == 58) {
									p.sendTitle(StringUtils.use("&eHey AFK person!").translate(), StringUtils.use(MessageFormat.format(format, 2)).translate(), 0, 12, 5);
								}
								if (seconds == 59) {
									p.sendTitle(StringUtils.use("&eHey AFK person!").translate(), StringUtils.use(MessageFormat.format(format, 1)).translate(), 0, 12, 5);
								}
							}
							break;
						case PENDING:
							Bukkit.broadcastMessage(StringUtils.use("&2&l[&fLabyrinth&2&l]" + " &7Player &b" + p.getName() + " &7is now AFK").translate());
							p.setDisplayName(StringUtils.use("&7*AFK&r " + p.getDisplayName()).translate());
							e.getAfk().set(Status.AWAY);
							break;
						case RETURNING:
							p.setDisplayName(p.getName());
							Bukkit.broadcastMessage(StringUtils.use("&2&l[&fLabyrinth&2&l]" + " &7Player &b" + p.getName() + " &7is no longer AFK").translate());
							e.getAfk().reset(Status.ACTIVE);
							break;
						case REMOVABLE:
							e.getAfk().remove();
							Bukkit.broadcastMessage(StringUtils.use("&2&l[&fLabyrinth&2&l]" + " &c&oPlayer &b" + p.getName() + " &c&owas kicked for being AFK too long.").translate());
							p.kickPlayer(StringUtils.use("&2&l[&fLabyrinth&2&l]" + "\n" + "&c&oAFK too long.\n&c&oKicking to ensure safety :)").translate());
							break;
						case CHATTING:
						case EXECUTING:
							e.getAfk().set(Status.RETURNING);
							break;
						case LEAVING:
							e.getAfk().remove();
							p.setDisplayName(p.getName());
							Bukkit.broadcastMessage(StringUtils.use("&2&l[&fLabyrinth&2&l]" + " &7Player &b" + p.getName() + " &7is no longer AFK").translate());
							break;
					}
				})
				.finish());
	}

	public static AFK get(Player player) {
		return HISTORY.stream().filter(a -> a.player.equals(player)).findFirst().orElse(null);
	}

	/**
	 * Override the default status change events
	 *
	 * @param subscription The vent subscription to use.
	 */
	public static void override(Vent.Subscription<StatusChange> subscription) {
		VentMap api = LabyrinthProvider.getInstance().getEventMap();
		api.unsubscribeAll(StatusChange.class, "afk-default");
		api.subscribe(subscription);
	}

	/**
	 * Override the subscription link.
	 * Unsubscribes all factory subscriptions besides the status change events.
	 *
	 * @param link The subscription link to override with
	 */
	public static void override(Vent.Link link) {
		VentMap ventMap = LabyrinthProvider.getInstance().getEventMap();
		ventMap.unsubscribeAll("afk-default");
		ventMap.chain(link);
	}

	/**
	 * Re-set the status of this user.
	 */
	public void reset(Status status) {
		this.status = status;
		this.time = System.currentTimeMillis();
	}

	/**
	 * Set the status of this user.
	 */
	public void set(Status status) {
		this.status = status;
	}

	/**
	 * Completely remove this user's AFK trace from cache.
	 */
	public void remove() {
		Task task = TaskMonitor.getLocalInstance().get(player.getName() + player.getUniqueId() + "-afk");
		if (task != null) {
			task.cancel();
			TaskScheduler.of(() -> HISTORY.remove(this)).schedule();
		}
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
		PENDING,

		/**
		 * The user is currently typing in chat from recently being AFK.
		 */
		CHATTING,

		/**
		 * The user is currently typing in a command from recently being AFK.
		 */
		EXECUTING,

		/**
		 * The user is currently leaving from recently being AFK.
		 */
		LEAVING,

		/**
		 * The user is currently away
		 */
		AWAY,

		/**
		 * The user is safe to remove.
		 */
		REMOVABLE,

		/**
		 * The user is coming back from being AFK.
		 */
		RETURNING


	}

	/**
	 * Initialization object for building a plugin reference to a player's timeout request.
	 */
	public static class Initializer {

		private final Player player;

		private Plugin plugin;

		private StatusTrigger<Boolean> away = a -> a.getRecording().getMinutes() >= 5;

		private StatusTrigger<Boolean> kick = b -> b.getRecording().getMinutes() >= 15;

		protected Initializer(Player player) {
			this.player = player;
		}

		/**
		 * Initialize an AFK instance for a player.
		 *
		 * @param player The player to use.
		 * @return An AFK user initializer.
		 */
		public static Initializer use(Player player) {

			return new Initializer(player);
		}

		public Initializer next(Plugin plugin) {
			this.plugin = plugin;
			return this;
		}

		/**
		 * Apply the reading logic for the status changing event.
		 *
		 * @param subscriberCall The event & subscription to modify.
		 * @return this builder
		 */
		public Initializer next(SubscriberCall<StatusChange> subscriberCall) {
			VentMap ventMap = LabyrinthProvider.getInstance().getEventMap();
			if (ventMap.get(DefaultEvent.Communication.class, "afk-default") == null) {
				ventMap.chain(new Vent.Link(new Vent.Subscription<>(StatusChange.class, "afk-default", plugin, Vent.Priority.MEDIUM, subscriberCall))
						.next(new Vent.Subscription<>(DefaultEvent.Leave.class, "afk-default", plugin, Vent.Priority.HIGH, (e, subscription) -> {

							final AFK afk = supply(e.getPlayer());

							if (afk != null) {
								if (afk.getStatus() == Status.AWAY) {
									e.getPlayer().setDisplayName(e.getPlayer().getName());
									afk.set(Status.LEAVING);
								}
							}

						})).next(new Vent.Subscription<>(DefaultEvent.Communication.class, "afk-default", plugin, Vent.Priority.HIGH, (e, subscription) -> {

							switch (e.getCommunicationType()) {
								case COMMAND:
									if (e.getCommand().orElse(null) != null) {
										Player p = e.getPlayer();
										AFK afk = AFK.get(p);
										if (afk != null) {
											if (afk.getStatus() == Status.AWAY) {
												afk.set(Status.EXECUTING);
											}
										}
									}
									break;
								case CHAT:
									if (e.getMessage().orElse(null) != null) {
										Player p = e.getPlayer();
										AFK afk = AFK.get(p);
										if (afk != null) {
											if (afk.getStatus() == Status.AWAY) {
												afk.set(Status.CHATTING);
											}
										}
									}
									break;
							}

						})));
			}
			return this;
		}

		/**
		 * @param away the trigger for changing the status to {@link Status#PENDING}
		 * @return this builder
		 */
		public Initializer wait(final StatusTrigger<Boolean> away) {
			this.away = away;
			return this;
		}

		/**
		 * @param kick the trigger for changing the status to {@link Status#REMOVABLE}
		 * @return this builder
		 */
		public Initializer remove(final StatusTrigger<Boolean> kick) {
			this.kick = kick;
			return this;
		}

		/**
		 * Start the task for this individual, when they stagnate too long they will be removed based on your configurations.
		 *
		 * @return the initialized and cached object reference
		 */
		public AFK finish() {
			final AFK afk = new AFK(this.player);
			TaskScheduler.of(() -> {
				if (afk.time == 0) {
					afk.time = System.currentTimeMillis();
					afk.location = new Position(afk.getPlayer().getLocation().getBlockX(), afk.getPlayer().getLocation().getBlockY(), afk.getPlayer().getLocation().getBlockZ(), afk.getPlayer().getLocation().getYaw(), afk.getPlayer().getLocation().getPitch());
				} else {
					if (Position.matches(afk.getLocation(), afk.getPlayer().getLocation())) {
						if (away.accept(afk)) {
							if (afk.status == Status.ACTIVE) {
								afk.status = Status.PENDING;
								new Vent.Call<>(new StatusChange(afk, Status.PENDING)).run();
							}

							if (afk.status == Status.RETURNING) {
								new Vent.Call<>(new StatusChange(afk, Status.RETURNING)).run();
							}

							if (afk.status == Status.CHATTING) {
								new Vent.Call<>(new StatusChange(afk, Status.CHATTING)).run();
							}

							if (afk.status == Status.EXECUTING) {
								new Vent.Call<>(new StatusChange(afk, Status.EXECUTING)).run();
							}

							if (afk.status == Status.LEAVING) {
								new Vent.Call<>(new StatusChange(afk, Status.LEAVING)).run();
							}
							if (afk.status == Status.AWAY) {
								new Vent.Call<>(new StatusChange(afk, Status.AWAY)).run();
							}

							if (kick.accept(afk)) {
								afk.status = Status.REMOVABLE;
								new Vent.Call<>(new StatusChange(afk, Status.REMOVABLE)).run();
							}
						}
					} else {
						if (afk.status == Status.AWAY || afk.status == Status.PENDING) {
							afk.location = null;
							afk.time = 0;
							afk.status = Status.RETURNING;
							new Vent.Call<>(new StatusChange(afk, Status.RETURNING)).run();
						} else {
							afk.time = 0;
						}
					}
				}

			}).scheduleTimer(afk.player.getName() + afk.player.getUniqueId() + "-afk", 0, 12);
			HISTORY.add(afk);
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
