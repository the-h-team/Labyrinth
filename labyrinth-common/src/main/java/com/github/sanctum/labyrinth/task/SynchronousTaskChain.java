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
		timer.schedule(task, 0);
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
		timer.schedule(task, 0);
		return this;
	}

	@Override
	public SynchronousTaskChain wait(final @NotNull Task task, long delay) {
		task.parent = this;
		timer.schedule(task, delay);
		map.put(task.getKey(), task);
		return this;
	}

	@Override
	public SynchronousTaskChain wait(final @NotNull Runnable data, String key, long milli) {
		Task task = new Task(key, Task.SINGULAR, this) {
			private static final long serialVersionUID = 4057999550419202270L;

			@Ordinal
			public void execute() {
				data.run();
			}
		};
		map.put(key, task);
		timer.schedule(task, milli);
		return this;
	}

	@Override
	public SynchronousTaskChain wait(final @NotNull Task task, @NotNull Date start) {
		task.parent = this;
		map.put(task.getKey(), task);
		timer.schedule(task, start);
		return this;
	}

	@Override
	public SynchronousTaskChain wait(final @NotNull Runnable data, String key, @NotNull Date start) {
		Task task = new Task(key, Task.SINGULAR, this) {
			private static final long serialVersionUID = 3310911037523100957L;

			@Ordinal
			public void execute() {
				data.run();
			}
		};
		map.put(key, task);
		timer.schedule(task, start);
		return this;
	}

	@Override
	public SynchronousTaskChain repeat(final @NotNull Task task, long delay, long period) {
		if (!map.containsKey(task.getKey())) {
			map.put(task.getKey(), task);
			timer.scheduleAtFixedRate(task, delay, period);
		}
		return this;
	}

	@Override
	public SynchronousTaskChain repeat(final @NotNull Consumer<Task> consumer, @NotNull String key, long delay, long period) {
		if (!map.containsKey(key)) {
			Task task = new Task(key, Task.REPEATABLE, this) {
				private static final long serialVersionUID = 6685875459466218624L;

				@Ordinal
				public void execute() {
					consumer.accept(this);
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
			map.put(task.getKey(), task);
			timer.scheduleAtFixedRate(task, start, period);
		}
		return this;
	}

	@Override
	public SynchronousTaskChain repeat(final @NotNull Consumer<Task> consumer, @NotNull String key, @NotNull Date start, long period) {
		if (!map.containsKey(key)) {
			Task task = new Task(key, Task.REPEATABLE, this) {
				private static final long serialVersionUID = 4614422706913899876L;

				@Ordinal
				public void execute() {
					consumer.accept(this);
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
