package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.annotation.Ordinal;
import com.github.sanctum.labyrinth.formatting.string.RandomID;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class AsynchronousTaskChain extends TaskChain {

	protected final Timer timer;

	public AsynchronousTaskChain() {
		this.timer = new Timer(true);
	}

	@Override
	public AsynchronousTaskChain run(final @NotNull Task task) {
		task.parent = this;
		timer.schedule(task.setAsync(true), 0);
		return this;
	}

	@Override
	public AsynchronousTaskChain run(final @NotNull Runnable data) {
		Task task = new Task(new RandomID().generate(), Task.SINGULAR, this) {
			private static final long serialVersionUID = 1024238305361097135L;

			@Ordinal
			public void execute() {
				data.run();
			}
		};
		timer.schedule(task.setAsync(true), 0);
		return this;
	}

	@Override
	public AsynchronousTaskChain wait(final @NotNull Task task, long delay) {
		task.parent = this;
		map.put(task.getKey(), task);
		timer.schedule(task.setAsync(true), delay);
		return this;
	}

	@Override
	public AsynchronousTaskChain wait(final @NotNull Runnable data, String key, long milli) {
		Task task = new Task(key, Task.SINGULAR, this) {
			private static final long serialVersionUID = 5064153492626085962L;

			@Ordinal
			public void execute() {
				data.run();
			}
		};
		map.put(key, task);
		timer.schedule(task.setAsync(true), milli);
		return this;
	}

	@Override
	public AsynchronousTaskChain wait(final @NotNull Task task, @NotNull Date start) {
		task.parent = this;
		map.put(task.getKey(), task);
		timer.schedule(task.setAsync(true), start);
		return this;
	}

	@Override
	public AsynchronousTaskChain wait(final @NotNull Runnable data, String key, @NotNull Date start) {
		Task task = new Task(key, Task.SINGULAR, this) {
			private static final long serialVersionUID = -809723855257869160L;

			@Ordinal
			public void execute() {
				data.run();
			}
		};
		map.put(key, task);
		timer.schedule(task.setAsync(true), start);
		return this;
	}

	@Override
	public AsynchronousTaskChain repeat(final @NotNull Task task, long delay, long period) {
		if (!map.containsKey(task.getKey())) {
			map.put(task.getKey(), task);
			timer.scheduleAtFixedRate(task.setAsync(true), delay, period);
		}
		return this;
	}

	@Override
	public AsynchronousTaskChain repeat(final @NotNull Consumer<Task> consumer, @NotNull String key, long delay, long period) {
		if (!map.containsKey(key)) {
			Task task = new Task(key, Task.REPEATABLE, this) {
				private static final long serialVersionUID = -9049322776279344996L;

				@Ordinal
				public void execute() {
					consumer.accept(this);
				}
			};
			map.put(key, task);
			timer.scheduleAtFixedRate(task.setAsync(true), delay, period);
		}
		return this;
	}

	@Override
	public AsynchronousTaskChain repeat(final @NotNull Task task, @NotNull Date start, long period) {
		task.parent = this;
		if (!map.containsKey(task.getKey())) {
			map.put(task.getKey(), task);
			timer.scheduleAtFixedRate(task.setAsync(true), start, period);
		}
		return this;
	}

	@Override
	public AsynchronousTaskChain repeat(final @NotNull Consumer<Task> consumer, @NotNull String key, @NotNull Date start, long period) {
		if (!map.containsKey(key)) {
			Task task = new Task(key, Task.REPEATABLE, this) {
				private static final long serialVersionUID = 2087638843164641921L;

				@Ordinal
				public void execute() {
					consumer.accept(this);
				}
			};
			map.put(key, task);
			timer.scheduleAtFixedRate(task.setAsync(true), start, period);
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
