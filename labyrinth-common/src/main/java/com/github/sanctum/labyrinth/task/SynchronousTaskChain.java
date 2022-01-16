package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.annotation.Ordinal;
import com.github.sanctum.labyrinth.interfacing.OrdinalProcedure;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SynchronousTaskChain extends TaskChain {

	protected final Timer timer;
	protected final Plugin host;

	public SynchronousTaskChain(Plugin host) {
		this.host = host;
		this.timer = new Timer(true);
	}

	@Override
	public SynchronousTaskChain run(final @NotNull Task task) {
		task.parent = this;
		timer.schedule(new Task("dummy-" + task.getKey()) {
			@Ordinal
			public void execute() {
				if (task.isCancelled()) {
					cancel();
					return;
				}
				Bukkit.getScheduler().runTask(host, () -> OrdinalProcedure.process(task, 0));
			}
		}, 0);
		return this;
	}

	@Override
	public SynchronousTaskChain run(final @NotNull Runnable data) {
		Task task = new Task("dummy", Task.SINGULAR, this) {
			@Ordinal
			public void execute() {
				Bukkit.getScheduler().runTask(host, data);
			}
		};
		timer.schedule(task, 0);
		return this;
	}

	@Override
	public SynchronousTaskChain wait(final @NotNull Task task, long delay) {
		task.parent = this;
		Task t = new Task(task.getKey(), Task.SINGULAR, this) {
			@Ordinal
			public void execute() {
				if (task.isCancelled()) {
					cancel();
					return;
				}
				Bukkit.getScheduler().runTask(host, () -> OrdinalProcedure.process(task, 0));
			}
		};
		timer.schedule(t, delay);
		map.put(task.getKey(), t);
		return this;
	}

	@Override
	public SynchronousTaskChain wait(final @NotNull Runnable data, String key, long milli) {
		Task task = new Task(key, Task.SINGULAR, this) {
			@Ordinal
			public void execute() {
				Bukkit.getScheduler().runTask(host, data);
			}
		};
		map.put(key, task);
		timer.schedule(task, milli);
		return this;
	}

	@Override
	public SynchronousTaskChain wait(final @NotNull Task task, @NotNull Date start) {
		task.parent = this;
		Task t = new Task(task.getKey(), Task.SINGULAR, this) {
			@Ordinal
			public void execute() {
				if (task.isCancelled()) {
					cancel();
					return;
				}
				Bukkit.getScheduler().runTask(host, () -> OrdinalProcedure.process(task, 0));
			}
		};
		map.put(task.getKey(), t);
		timer.schedule(t, start);
		return this;
	}

	@Override
	public SynchronousTaskChain wait(final @NotNull Runnable data, String key, @NotNull Date start) {
		Task task = new Task(key, Task.SINGULAR, this) {
			@Ordinal
			public void execute() {
				Bukkit.getScheduler().runTask(host, data);
			}
		};
		map.put(key, task);
		timer.schedule(task, start);
		return this;
	}

	@Override
	public SynchronousTaskChain repeat(final @NotNull Task task, long delay, long period) {
		if (!map.containsKey(task.getKey())) {
			Task t = new Task(task.getKey(), Task.REPEATABLE, this) {
				@Ordinal
				public void execute() {

					if (!host.isEnabled()) {
						this.cancel();
						return;
					}

					if (task.isCancelled()) {
						cancel();
						return;
					}

					Bukkit.getScheduler().runTask(host, () -> OrdinalProcedure.process(task, 0));
				}
			};
			map.put(task.getKey(), t);
			timer.scheduleAtFixedRate(t, delay, period);
		}
		return this;
	}

	@Override
	public SynchronousTaskChain repeat(final @NotNull Consumer<Task> consumer, @NotNull String key, long delay, long period) {
		if (!map.containsKey(key)) {
			Task task = new Task(key, Task.REPEATABLE, this) {
				@Ordinal
				public void execute() {
					if (!host.isEnabled()) {
						this.cancel();
						return;
					}

					Bukkit.getScheduler().runTask(host, () -> consumer.accept(this));
				}
			};
			map.put(key, task);
			timer.scheduleAtFixedRate(task, delay, period);
		}
		return this;
	}

	@Override
	public SynchronousTaskChain repeat(final @NotNull Task task, @NotNull Date start, long period) {
		task.parent = this;
		if (!map.containsKey(task.getKey())) {
			Task t = new Task(task.getKey(), Task.REPEATABLE, this) {
				@Ordinal
				public void execute() {
					if (!host.isEnabled()) {
						this.cancel();
						return;
					}

					if (task.isCancelled()) {
						cancel();
						return;
					}

					Bukkit.getScheduler().runTask(host, () -> OrdinalProcedure.process(task, 0));
				}
			};
			map.put(task.getKey(), t);
			timer.scheduleAtFixedRate(t, start, period);
		}
		return this;
	}

	@Override
	public SynchronousTaskChain repeat(final @NotNull Consumer<Task> consumer, @NotNull String key, @NotNull Date start, long period) {
		if (!map.containsKey(key)) {
			Task task = new Task(key, Task.REPEATABLE, this) {
				@Ordinal
				public void execute() {
					if (!host.isEnabled()) {
						this.cancel();
						return;
					}
					Bukkit.getScheduler().runTask(host, () -> consumer.accept(this));
				}
			};
			map.put(key, task);
			timer.scheduleAtFixedRate(task, start, period);
		}
		return this;
	}

	@Override
	public int purge() {
		return timer.purge();
	}

	@Override
	public boolean shutdown() {
		if (!map.isEmpty()) {
			timer.cancel();
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
