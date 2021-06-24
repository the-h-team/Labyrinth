package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.library.Applicable;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;

public class Synchronous {

	private final BukkitRunnable runnable;
	private final Applicable initial;
	private Applicable apply = null;
	private Map<?, ?> map = null;
	private Object o = null;
	private boolean check;
	private boolean debug;
	private boolean fallback;
	private String cancel = null;
	private Player p = null;
	private TaskCancellation cancellation = null;

	protected Synchronous(Applicable applicable) {
		this.initial = applicable;
		this.runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (!Labyrinth.getTasks().contains(getTaskId())) {
					Labyrinth.getTasks().add(getTaskId());
				}
				try {
					if (cancellation != null) {
						cancellation.execute(new ScheduledTask(this));
						applicable.apply();
						if (apply != null) {
							apply.apply();
						}
						Labyrinth.getTasks().remove(getTaskId());
						return;
					}
					if (cancel == null) {
						if (fallback) {
							if (Bukkit.getOnlinePlayers().size() == 0) {
								cancelTask();
								return;
							}
						}
						if (map != null) {
							if (!map.containsKey(o)) {
								cancelTask();
								if (debug) {
									Labyrinth.getInstance().getLogger().info("Closing un-used task, target player in-activity.");
								}
								return;
							}
						}
						if (check) {
							if (p == null || !p.isOnline()) {
								if (debug) {
									Labyrinth.getInstance().getLogger().info("Closing un-used task, target player in-activity.");
								}
								cancelTask();
								return;
							}
						}
						applicable.apply();
						if (apply != null) {
							apply.apply();
						}
						Labyrinth.getTasks().remove(getTaskId());
					} else {
						int count = Integer.parseInt(cancel);
						count--;
						cancel = String.valueOf(count);
						if (fallback) {
							if (Bukkit.getOnlinePlayers().size() == 0) {
								cancelTask();
								return;
							}
						}
						if (map != null) {
							if (!map.containsKey(o)) {
								cancelTask();
								if (debug) {
									Labyrinth.getInstance().getLogger().info("Closing un-used task, target object in-activity.");
								}
								return;
							}
						}
						if (count > 0) {
							if (check) {
								if (p == null || !p.isOnline()) {
									if (debug) {
										Labyrinth.getInstance().getLogger().info("Closing un-used task, target player in-activity.");
									}
									cancelTask();
									return;
								}
							}
							applicable.apply();
							if (apply != null) {
								apply.apply();
							}
							Labyrinth.getTasks().remove(getTaskId());
						} else {
							if (debug) {
								Labyrinth.getInstance().getLogger().info("Closing task, max usage counter achieved.");
							}
							cancelTask();
						}
					}
				} catch (Exception e) {
					if (debug) {
						Labyrinth.getInstance().getLogger().severe("Closing task, an exception occurred and was stopped.");
						e.printStackTrace();
					}
					cancelTask();
					Labyrinth.getInstance().getLogger().severe(e::getMessage);
				}
			}
		};
	}

	public boolean isRunning() {
		return !this.runnable.isCancelled();
	}

	public void cancelTask() {
		if (!this.runnable.isCancelled()) {
			runnable.cancel();
		}
	}

	/**
	 * Automatically cancel the task in the absence of a specified player.
	 *
	 * <p>If the task is dependant on a target player you can use this
	 * for easy automatic task cleanup.</p>
	 *
	 * @param p The player to query.
	 * @return The same synchronous task builder.
	 */
	public Synchronous cancelAfter(Player p) {
		this.check = true;
		this.p = p;
		return this;
	}

	/**
	 * Automatically cancel the task from a specified pre-condition.
	 *
	 * <p>This will simply cancel the task of your own will @ pre-condition.</p>
	 *
	 * @param condition The condition to fire the cancellation
	 * @return The same synchronous task builder.
	 */
	public Synchronous cancelAfter(boolean condition) {
		this.check = condition;
		return this;
	}

	/**
	 * Automatically cancel the task after a specified amount of executions
	 *
	 * <p>Will work in tandem with other cancellation pre-conditions.
	 * Use for easy automatic task cleanup.</p>
	 *
	 * @param count The amount of executions to run.
	 * @return The same synchronous task builder.
	 */
	public Synchronous cancelAfter(int count) {
		this.cancel = String.valueOf(count + 1);
		return this;
	}

	/**
	 * Define your own running logic for cancellations.
	 *
	 * @param cancellation The cancellation objective.
	 * @return The same synchronous task builder.
	 */
	public Synchronous cancelAfter(TaskCancellation cancellation) {
		this.cancellation = cancellation;
		return this;
	}

	/**
	 * Automatically cancel the task if a target object goes missing from a map.
	 *
	 * @param map The map to query keys from
	 * @param o   The object to check for removal
	 * @return The same synchronous task builder.
	 */
	public Synchronous cancelAbsence(Map<?, ?> map, Object o) {
		this.map = map;
		this.o = o;
		return this;
	}

	/**
	 * Automatically cancel the task upon the instance of an empty server.
	 *
	 * @return The same synchronous task builder.
	 */
	public Synchronous cancelEmpty() {
		this.fallback = true;
		return this;
	}

	/**
	 * Use this to print helpful exception/console information upon automatic
	 * task cancellations.
	 *
	 * <p>If the task throws an un-caught exception it is recommended this is used to display
	 * any possible source to possible problems that occur.</p>
	 *
	 * @return The same synchronous task builder.
	 */
	public Synchronous debug() {
		this.debug = true;
		return this;
	}

	/**
	 * Use this to apply defined logic to the task as soon as it finishes.
	 *
	 * <p>The information passed here will have secondary importance and will be called
	 * directly after the initial task has executed one time for each time ran.</p>
	 *
	 * @param applicable The information to pass be it via Void or lambda reference.
	 * @return The same synchronous task builder.
	 */
	public Synchronous applyAfter(Applicable applicable) {
		this.apply = applicable;
		return this;
	}

	/**
	 * Run this scheduled task on the very next tick.
	 */
	public void run() {
		try {
			runnable.runTask(Labyrinth.getInstance());
		} catch (IllegalPluginAccessException ignored) {
		}
	}

	/**
	 * Run this scheduled task after a specified amount of ticks.
	 *
	 * @param interval The amount of in game ticks to wait.
	 */
	public void wait(int interval) {
		try {
			runnable.runTaskLater(Labyrinth.getInstance(), interval);
		} catch (IllegalPluginAccessException ignored) {
		}
	}

	/**
	 * Wait an interval of time before running this sync task.
	 * <p>
	 * This method uses 20 ticks = 1 second exactly, so if your
	 * delay logic is based on real time, use this method.</p>
	 *
	 * @param interval real-time delay where 20 ticks = 1 second
	 */
	public void waitReal(int interval) {
		Schedule.async(() -> Schedule.sync(initial).run()).debug().wait(interval);
	}

	/**
	 * Run this scheduled task repeatedly unless otherwise cancelled.
	 *
	 * <p>The interval at which this task repeats is based off the delay you specify.
	 * Immediately after the delay finishes the task will run once and wait the specified "period" until re-cycling.
	 * Therefore running the delay > task > period , all over again.</p>
	 *
	 * @param delay  The amount of time to wait before executing the task.
	 * @param period The amount of time to wait to cycle the task.
	 */
	public void repeat(int delay, int period) {
		try {
			runnable.runTaskTimer(Labyrinth.getInstance(), delay, period);
		} catch (IllegalPluginAccessException ignored) {
		}
	}

	/**
	 * Wait a delay of real time before running this sync task and
	 * repeat it every period elapsed until cancelled.
	 * <p>
	 * This method uses 20 ticks = 1 second exactly, so if your
	 * delay+repeat logic is based on real time, use this method.</p>
	 *
	 * @param delay  real-time delay where 20 ticks = 1 second
	 * @param period real-time period where 20 ticks = 1 second
	 */
	public void repeatReal(int delay, int period) {
		Asynchronous schedule = Schedule.async(() -> Schedule.sync(initial).debug().wait(1)).debug();
		if (map != null) {
			schedule.cancelAbsence(map, o);
		}
		if (this.cancel != null) {
			if (check) {
				schedule.cancelAfter(Integer.parseInt(this.cancel));
			}
		}
		if (this.cancellation != null) {
			schedule.cancelAfter(this.cancellation);
		}
		if (this.p != null) {
			schedule.cancelAfter(this.p);
		}
		if (this.check) {
			schedule.cancelAfter(true);
		}
		if (apply != null) {
			schedule.applyAfter(apply);
		}

		schedule.repeat(delay, period);
	}

}
