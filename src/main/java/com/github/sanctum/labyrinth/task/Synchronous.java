package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.library.Applicable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Synchronous {

	private BukkitRunnable outer;
	private final BukkitRunnable runnable;
	private boolean check;
	private boolean debug;
	private boolean fallback;
	private String cancel = null;
	private Player p = null;

	protected Synchronous(Applicable applicable) {
		this.runnable = new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if (cancel == null) {
						if (fallback) {
							if (Bukkit.getOnlinePlayers().size() == 0) {
								cancelTask();
							}
						}
						if (check) {
							if (p == null || !p.isOnline()) {
								if (debug) {
									Labyrinth.getInstance().getLogger().info("Closing un-used task, target player left server.");
								}
								cancelTask();
							}
						}
						applicable.apply();
					} else {
						int count = Integer.parseInt(cancel);
						count--;
						cancel = String.valueOf(count);
						if (fallback) {
							if (Bukkit.getOnlinePlayers().size() == 0) {
								cancelTask();
							}
						}
						if (count > 0) {
							if (check) {
								if (p == null || !p.isOnline()) {
									if (debug) {
										Labyrinth.getInstance().getLogger().info("Closing un-used task, target player left server.");
									}
									cancelTask();
								}
							}
							applicable.apply();
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

	private void cancelTask() {
		if (outer != null) {
			outer.cancel();
			outer = null;
			return;
		}
		try {
			runnable.cancel();
		} catch (IllegalStateException ignored) {
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
	 * @return The same synchronous task builder.
	 */
	public Synchronous debug() {
		this.debug = true;
		return this;
	}

	/**
	 * Run this scheduled task on the very next tick.
	 */
	public void run() {
		runnable.runTask(Labyrinth.getInstance());
	}

	/**
	 * Run this scheduled task after a specified amount of ticks.
	 *
	 * @param interval The amount of in game ticks to wait.
	 */
	public void wait(int interval) {
		runnable.runTaskLater(Labyrinth.getInstance(), interval);
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
		outer = new BukkitRunnable() {
			@Override
			public void run() {
				Synchronous.this.run();
			}
		};
		outer.runTaskLaterAsynchronously(Labyrinth.getInstance(), interval);
	}

	/**
	 * Run this scheduled task repeatedly unless otherwise cancelled.
	 *
	 * <p>The interval at which this task repeats is based off the delay you specify.
	 * Immediately after the delay finishes the task will run once and wait the specified "period" until re-cycling.
	 * Therefore running the delay > task > period , all over again.</p>
	 *
	 * @param delay The amount of time to wait before executing the task.
	 * @param period The amount of time to wait to cycle the task.
	 */
	public void repeat(int delay, int period) {
		runnable.runTaskTimer(Labyrinth.getInstance(), delay, period);
	}

	/**
	 * Wait a delay of real time before running this sync task and
	 * repeat it every period elapsed until cancelled.
	 * <p>
	 * This method uses 20 ticks = 1 second exactly, so if your
	 * delay+repeat logic is based on real time, use this method.</p>
	 *
	 * @param delay real-time delay where 20 ticks = 1 second
	 * @param period real-time period where 20 ticks = 1 second
	 */
	public void repeatReal(int delay, int period) {
		outer = new BukkitRunnable() {
			@Override
			public void run() {
				Synchronous.this.run();
			}
		};
		outer.runTaskTimerAsynchronously(Labyrinth.getInstance(), delay, period);
	}

}
