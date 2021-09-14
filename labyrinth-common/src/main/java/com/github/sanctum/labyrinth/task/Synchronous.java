package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.library.Applicable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class Synchronous {

	private final LabyrinthAPI labyrinthAPI = LabyrinthProvider.getInstance();
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
				if (!labyrinthAPI.getConcurrentTaskIds().contains(getTaskId())) {
					labyrinthAPI.getConcurrentTaskIds().add(getTaskId());
				}
				try {
					if (cancellation != null) {
						cancellation.execute(new ScheduledTask(this));
						applicable.apply();
						if (apply != null) {
							apply.apply();
						}
						labyrinthAPI.getConcurrentTaskIds().remove(getTaskId());
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
									labyrinthAPI.getLogger().info("Closing un-used task, target player in-activity.");
								}
								return;
							}
						}
						if (check) {
							if (p == null || !p.isOnline()) {
								if (debug) {
									labyrinthAPI.getLogger().info("Closing un-used task, target player in-activity.");
								}
								cancelTask();
								return;
							}
						}
						applicable.apply();
						if (apply != null) {
							apply.apply();
						}
						labyrinthAPI.getConcurrentTaskIds().remove(getTaskId());
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
									labyrinthAPI.getLogger().info("Closing un-used task, target object in-activity.");
								}
								return;
							}
						}
						if (count > 0) {
							if (check) {
								if (p == null || !p.isOnline()) {
									if (debug) {
										labyrinthAPI.getLogger().info("Closing un-used task, target player in-activity.");
									}
									cancelTask();
									return;
								}
							}
							applicable.apply();
							if (apply != null) {
								apply.apply();
							}
							labyrinthAPI.getConcurrentTaskIds().remove(getTaskId());
						} else {
							if (debug) {
								labyrinthAPI.getLogger().info("Closing task, max usage counter achieved.");
							}
							cancelTask();
						}
					}
				} catch (Exception e) {
					if (debug) {
						labyrinthAPI.getLogger().severe("Closing task, an exception occurred and was stopped.");
						e.printStackTrace();
					}
					cancelTask();
					labyrinthAPI.getLogger().severe(e::getMessage);
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
	 * <p>
	 * If the task is dependent on a target player, you can use this
	 * for easy automatic task cleanup.
	 *
	 * @param p the player to query
	 * @return this synchronous task builder
	 */
	public Synchronous cancelAfter(Player p) {
		this.check = true;
		this.p = p;
		return this;
	}

	/**
	 * Automatically cancel the task from a specified precondition.
	 * <p>
	 * This will simply cancel the task of your own will @ precondition.
	 *
	 * @param condition the condition to fire the cancellation
	 * @return this synchronous task builder
	 */
	public Synchronous cancelAfter(boolean condition) {
		this.check = condition;
		return this;
	}

	/**
	 * Automatically cancel the task after a specified amount of executions
	 *
	 * <p>Will work in tandem with other cancellation preconditions.
	 * Use for easy automatic task cleanup.</p>
	 *
	 * @param count the amount of executions to run
	 * @return this synchronous task builder
	 */
	public Synchronous cancelAfter(int count) {
		this.cancel = String.valueOf(count + 1);
		return this;
	}

	/**
	 * Define your own running logic for cancellations.
	 *
	 * @param cancellation the cancellation objective
	 * @return this synchronous task builder
	 */
	public Synchronous cancelAfter(TaskCancellation cancellation) {
		this.cancellation = cancellation;
		return this;
	}

	/**
	 * Automatically cancel the task if a target object goes missing from a map.
	 *
	 * @param map the map to query keys from
	 * @param o   the object to check for removal
	 * @return this synchronous task builder
	 */
	public Synchronous cancelAbsence(Map<?, ?> map, Object o) {
		this.map = map;
		this.o = o;
		return this;
	}

	/**
	 * Automatically cancel the task upon the instance of an empty server.
	 *
	 * @return this synchronous task builder
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
	 * @return this synchronous task builder
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
	 * @param applicable the information to pass be it via Void or lambda reference
	 * @return this synchronous task builder
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
			runnable.runTask(labyrinthAPI.getPluginInstance());
		} catch (IllegalPluginAccessException ignored) {
		}
	}

	/**
	 * Run this scheduled task after a specified amount of ticks.
	 *
	 * @param interval the amount of in-game ticks to wait
	 */
	public void wait(int interval) {
		try {
			runnable.runTaskLater(labyrinthAPI.getPluginInstance(), interval);
		} catch (IllegalPluginAccessException ignored) {
		}
	}

	/**
	 * Wait an interval of time before running this sync task.
	 * <p>
	 * This method uses 20 ticks = 1 second exactly, so if your
	 * delay logic is based on real time, use this method.
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
	 * Therefore running the delay &gt; task &gt; period , all over again.</p>
	 *
	 * @param delay  the amount of time to wait before executing the task
	 * @param period the amount of time to wait to cycle the task
	 */
	public void repeat(int delay, int period) {
		try {
			runnable.runTaskTimer(labyrinthAPI.getPluginInstance(), delay, period);
		} catch (IllegalPluginAccessException ignored) {
		}
	}

	/**
	 * Wait a delay of real time before running this sync task and
	 * repeat it every period elapsed until cancelled.
	 * <p>
	 * This method uses 20 ticks = 1 second exactly, so if your
	 * delay+repeat logic is based on real time, use this method.
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
