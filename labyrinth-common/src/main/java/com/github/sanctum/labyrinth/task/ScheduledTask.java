package com.github.sanctum.labyrinth.task;

import org.bukkit.scheduler.BukkitRunnable;

@Deprecated
public class ScheduledTask {

	protected final BukkitRunnable runnable;

	protected ScheduledTask(BukkitRunnable runnable) {
		this.runnable = runnable;
	}

	public synchronized void cancel() {
		this.runnable.cancel();
	}

	public synchronized boolean isCancelled() {
		return this.runnable.isCancelled();
	}

	public synchronized int getId() {
		return this.runnable.getTaskId();
	}

}
