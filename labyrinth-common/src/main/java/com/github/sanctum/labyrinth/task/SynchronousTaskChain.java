package com.github.sanctum.labyrinth.task;

import com.github.sanctum.panther.annotation.Ordinal;
import com.github.sanctum.panther.util.Task;
import com.github.sanctum.panther.util.TaskChain;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public final class SynchronousTaskChain extends TaskChain {

	final BukkitScheduler timer;
	final Task.Synchronizer synchronizer;
	final Plugin host;

	public SynchronousTaskChain(Plugin host) {
		this.host = host;
		this.timer = Bukkit.getScheduler();
		this.synchronizer = runnable -> timer.runTask(host, runnable);
	}

	@Override
	public SynchronousTaskChain run(final @NotNull Task task) {
		task.setFuture(defaultTimer.submit(task.setChain(this).setSynchronizer(synchronizer)));
		return this;
	}

	@Override
	public SynchronousTaskChain run(final @NotNull Runnable data) {
		Task task = new Task("dummy", Task.SINGULAR, this) {
			private static final long serialVersionUID = 8068952665686647490L;

			@Ordinal
			public void execute() {
				data.run();
			}
		};
		task.setFuture(defaultTimer.submit(task.setChain(this).setSynchronizer(synchronizer)));
		return this;
	}

	@Override
	public @NotNull SynchronousTaskChain wait(@NotNull Task task) {
		long time;
		if (task.getClass().isAnnotationPresent(Task.Delay.class)) {
			time = task.getClass().getAnnotation(Task.Delay.class).value();
		} else throw new IllegalStateException("Task Delay annotation missing!");
		return wait(task, time);
	}

	@Override
	public SynchronousTaskChain wait(final @NotNull Task task, long delay) {
		task.setFuture(defaultTimer.scheduleWithFixedDelay(task.setChain(this).setSynchronizer(synchronizer), delay, delay, TimeUnit.MILLISECONDS));
		map.put(task.getKey(), task);
		return this;
	}

	@Override
	public @NotNull SynchronousTaskChain wait(@NotNull Runnable data, long delay) {
		Task task = new Task(UUID.randomUUID().toString(), Task.SINGULAR, this) {
			private static final long serialVersionUID = 4057999550419202270L;

			@Ordinal
			public void execute() {
				data.run();
			}
		};
		return wait(task, delay);
	}

	@Override
	public SynchronousTaskChain wait(final @NotNull Runnable data, String key, long delay) {
		Task task = new Task(key, Task.SINGULAR, this) {
			private static final long serialVersionUID = 4057999550419202270L;

			@Ordinal
			public void execute() {
				data.run();
			}
		};
		map.put(key, task);
		task.setFuture(defaultTimer.scheduleWithFixedDelay(task.setChain(this).setSynchronizer(synchronizer), delay, delay, TimeUnit.MILLISECONDS));
		return this;
	}

	@Override
	public @NotNull SynchronousTaskChain repeat(@NotNull Task task) {
		long delay;
		long period;
		if (task.getClass().isAnnotationPresent(Task.Delay.class)) {
			delay = task.getClass().getAnnotation(Task.Delay.class).value();
		} else throw new IllegalStateException("Task Delay annotation missing!");
		if (task.getClass().isAnnotationPresent(Task.Period.class)) {
			period = task.getClass().getAnnotation(Task.Period.class).value();
		} else throw new IllegalStateException("Task Period annotation missing!");
		return repeat(task, delay, period);
	}

	@Override
	public SynchronousTaskChain repeat(final @NotNull Task task, long delay, long period) {
		if (!map.containsKey(task.getKey())) {
			map.put(task.getKey(), task);
			task.setFuture(defaultTimer.scheduleAtFixedRate(task.setChain(this).setSynchronizer(synchronizer), delay, period, TimeUnit.MILLISECONDS));
		}
		return this;
	}

	@Override
	public @NotNull SynchronousTaskChain repeat(@NotNull Runnable data, long delay, long period) {
		return repeat(data, UUID.randomUUID().toString(), delay, period);
	}

	@Override
	public @NotNull SynchronousTaskChain repeat(@NotNull Runnable data, @NotNull String key, long delay, long period) {
		Task task = new Task(key, Task.SINGULAR, this) {
			private static final long serialVersionUID = 4057999550419202270L;

			@Ordinal
			public void execute() {
				data.run();
			}
		};
		return repeat(task, delay, period);
	}

	@Override
	public @NotNull <T> Future<T> submit(@NotNull Callable<T> data) {
		return defaultTimer.submit(data);
	}

	@Override
	public @NotNull <T> Future<T> submit(@NotNull Callable<T> data, long delay) {
		return defaultTimer.schedule(data, delay, TimeUnit.MILLISECONDS);
	}

	@Override
	public @NotNull <T> List<Future<T>> submit(@NotNull Collection<Callable<T>> data, long delay) throws InterruptedException {
		return defaultTimer.invokeAll(data, delay, TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean shutdown() {
		if (!map.isEmpty()) {
			map.values().forEach(Task::cancel);
			map.clear();
			return true;
		}
		return false;
	}

	@Override
	public Task get(String key) {
		return map.get(key);
	}

}
