package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.Labyrinth;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Synchronous {

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
								this.cancel();
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
									this.cancel();
								}
							}
							applicable.apply();
						} else {
							if (debug) {
								Labyrinth.getInstance().getLogger().info("Closing task, max usage counter achieved.");
							}
							this.cancel();
						}
					}
				} catch (Exception e) {
					if (debug) {
						Labyrinth.getInstance().getLogger().severe("Closing task, an exception occurred and was stopped. ");
					}
					this.cancel();
					Labyrinth.getInstance().getLogger().severe(e.getMessage());
				}
			}
		};
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

	public void repeat(int delay, int period) {
		runnable.runTaskTimer(Labyrinth.getInstance(), delay, period);
	}

}
