package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.library.Applicable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

@SuppressWarnings("UnusedReturnValue")
public class Asynchronous {

	private final LabyrinthAPI labyrinthAPI = LabyrinthProvider.getInstance();
	private final BukkitRunnable runnable;
	private Applicable apply = null;
	private String cancel = null;
	private boolean check;
	private boolean fallback;
	private boolean debug;
	private Player p = null;
	private Map<?, ?> map = null;
	private Object o = null;
	private TaskCancellation cancellation = null;

	protected Asynchronous(Applicable applicable) {
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
								this.cancel();
								return;
							}
						}
						if (map != null) {
							if (!map.containsKey(o)) {
								this.cancel();
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
								this.cancel();
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
								this.cancel();
								return;
							}
						}
						if (map != null) {
							if (!map.containsKey(o)) {
								this.cancel();
								if (debug) {
									labyrinthAPI.getLogger().info("Closing un-used task, target player in-activity.");
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
									this.cancel();
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
							this.cancel();
						}
					}
				} catch (Exception e) {
					if (debug) {
						labyrinthAPI.getLogger().severe("Closing task, an exception occurred and was stopped. ");
						e.printStackTrace();
					}
					this.cancel();
					labyrinthAPI.getLogger().severe(e::getMessage);
				}
			}
		};
	}

	public void cancelTask() {
		if (!this.runnable.isCancelled()) {
			this.runnable.cancel();
		}
	}

	/**
	 * Automatically cancel the task in the absence of a specified player.
	 *
	 * <p>If the task is dependent on a target player you can use this
	 * for easy automatic task cleanup.</p>
	 *
	 * @param player the player to query
	 * @return this asynchronous task builder
	 */
	public Asynchronous cancelAfter(Player player) {
		this.check = true;
		this.p = player;
		return this;
	}

	/**
	 * Automatically cancel the task from a specified precondition.
	 * <p>
	 * This will simply cancel the task of your own will @ precondition.
	 *
	 * @param condition the condition to fire the cancellation
	 * @return this asynchronous task builder
	 */
	public Asynchronous cancelAfter(boolean condition) {
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
	 * @return this asynchronous task builder
	 */
	public Asynchronous cancelAfter(int count) {
		this.cancel = String.valueOf(count + 1);
		return this;
	}

	/**
	 * Define your own running logic for cancellations.
	 *
	 * @param cancellation the cancellation objective
	 * @return this asynchronous task builder
	 */
	public Asynchronous cancelAfter(TaskCancellation cancellation) {
		this.cancellation = cancellation;
		return this;
	}

	/**
	 * Automatically cancel the task if a target object goes missing from a map.
	 *
	 * @param map the map to query keys from
	 * @param o   the object to check for removal
	 * @return this asynchronous task builder
	 */
	public Asynchronous cancelAbsence(Map<?, ?> map, Object o) {
		this.map = map;
		this.o = o;
		return this;
	}

	/**
	 * Automatically cancel the task upon the instance of an empty server.
	 *
	 * @return this asynchronous task builder
	 */
	public Asynchronous cancelEmpty() {
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
	 * @return this asynchronous task builder
	 */
	public Asynchronous debug() {
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
	 * @return this asynchronous task builder
	 */
	public Asynchronous applyAfter(Applicable applicable) {
		this.apply = applicable;
		return this;
	}

	/**
	 * Run this scheduled task on the very next tick.
	 */
	public void run() {
		try {
			runnable.runTaskAsynchronously(labyrinthAPI.getPluginInstance());
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
			runnable.runTaskLaterAsynchronously(labyrinthAPI.getPluginInstance(), interval);
		} catch (IllegalPluginAccessException ignored) {
		}
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
			runnable.runTaskTimerAsynchronously(labyrinthAPI.getPluginInstance(), delay, period);
		} catch (IllegalPluginAccessException ignored) {
		}
	}

}
