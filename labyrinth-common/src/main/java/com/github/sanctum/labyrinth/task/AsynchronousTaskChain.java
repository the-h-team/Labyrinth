package com.github.sanctum.labyrinth.task;

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
		timer.schedule(task, TimeUnit.SECONDS.toMillis(1));
		return this;
	}

	@Override
	public AsynchronousTaskChain run(final @NotNull Runnable data) {
		Task task = new Task("key", Task.SINGULAR, this) {
			@Override
			public void execute() {
				data.run();
			}
		};
		timer.schedule(task, TimeUnit.SECONDS.toMillis(1));
		return this;
	}

	@Override
	public AsynchronousTaskChain wait(final @NotNull Task task, long delay) {
		task.parent = this;
		map.put(task.getKey(), task);
		timer.schedule(task, delay);
		return this;
	}

	@Override
	public AsynchronousTaskChain wait(final @NotNull Runnable data, String key, long milli) {
		Task task = new Task(key, Task.SINGULAR, this) {
			@Override
			public void execute() {
				data.run();
			}
		};
		map.put(key, task);
		timer.schedule(task, milli);
		return this;
	}

	@Override
	public AsynchronousTaskChain wait(final @NotNull Task task, @NotNull Date start) {
		task.parent = this;
		map.put(task.getKey(), task);
		timer.schedule(task, start);
		return this;
	}

	@Override
	public AsynchronousTaskChain wait(final @NotNull Runnable data, String key, @NotNull Date start) {
		Task task = new Task(key, Task.SINGULAR, this) {
			@Override
			public void execute() {
				data.run();
			}
		};
		map.put(key, task);
		timer.schedule(task, start);
		return this;
	}

	@Override
	public AsynchronousTaskChain repeat(final @NotNull Task task, long delay, long period) {
		if (!map.containsKey(task.getKey())) {
			map.put(task.getKey(), task);
			timer.scheduleAtFixedRate(task, delay, period);
		}
		return this;
	}

	@Override
	public AsynchronousTaskChain repeat(final @NotNull Consumer<Task> consumer, @NotNull String key, long delay, long period) {
		if (!map.containsKey(key)) {
			Task task = new Task(key, Task.REPEATABLE, this) {
				@Override
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
	public AsynchronousTaskChain repeat(final @NotNull Task task, @NotNull Date start, long period) {
		task.parent = this;
		if (!map.containsKey(task.getKey())) {
			map.put(task.getKey(), task);
			timer.scheduleAtFixedRate(task, start, period);
		}
		return this;
	}

	@Override
	public AsynchronousTaskChain repeat(final @NotNull Consumer<Task> consumer, @NotNull String key, @NotNull Date start, long period) {
		if (!map.containsKey(key)) {
			Task task = new Task(key, Task.REPEATABLE, this) {
				@Override
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
