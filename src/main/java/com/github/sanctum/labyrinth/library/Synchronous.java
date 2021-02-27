package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.Labyrinth;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Synchronous {

	private BukkitRunnable outer;
	private final BukkitRunnable runnable;

	private String cancel = null;

	private boolean check;

	private boolean debug;

	private Player p = null;

	protected Synchronous(Applicable applicable) {
		this.runnable = new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if (cancel == null) {
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
		} catch (IllegalStateException e) {
			// runnable wasn't scheduled yet, ignore
		}
	}

	public Synchronous cancelAfter(Player p) {
		this.check = true;
		this.p = p;
		return this;
	}

	public Synchronous cancelAfter(int count) {
		this.cancel = String.valueOf(count + 1);
		return this;
	}

	public Synchronous debug() {
		this.debug = true;
		return this;
	}

	public void run() {
		runnable.runTask(Labyrinth.getInstance());
	}

	public void wait(int interval) {
		runnable.runTaskLater(Labyrinth.getInstance(), interval);
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
		outer = new BukkitRunnable() {
			@Override
			public void run() {
				Synchronous.this.run();
			}
		};
		outer.runTaskLaterAsynchronously(Labyrinth.getInstance(), interval);
	}

	public void repeat(int delay, int period) {
		runnable.runTaskTimer(Labyrinth.getInstance(), delay, period);
	}

	/**
	 * Wait a delay of real time before running this sync task and
	 * repeat it every period elapsed until cancelled.
	 * <p>
	 * This method uses 20 ticks = 1 second exactly, so if your
	 * delay+repeat logic is based on real time, use this method.
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
