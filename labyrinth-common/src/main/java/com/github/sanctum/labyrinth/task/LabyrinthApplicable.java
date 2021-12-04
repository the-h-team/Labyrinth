package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.library.Applicable;
import org.jetbrains.annotations.NotNull;

public abstract class LabyrinthApplicable implements Applicable, TaskScheduler {

	private static final long serialVersionUID = -3823153649975921753L;
	private TaskScheduler parent;
	private final String key;
	private final TaskPredicate<?>[] predicates;

	public LabyrinthApplicable() {
		this.key = null;
		this.predicates = null;
	}

	public LabyrinthApplicable(String key) {
		this.key = key;
		this.predicates = null;
	}

	public LabyrinthApplicable(String key, TaskPredicate<?>... predicates) {
		this.key = key;
		this.predicates = predicates;
	}

	@Override
	public abstract void run();

	LabyrinthApplicable fix() {
		if (this.parent == null) {
			this.parent = TaskScheduler.of(this::schedule);
		}
		return this;
	}

	public final boolean isCancelled() {
		return isCancelled(key);
	}

	public final boolean cancel() {
		return cancel(key);
	}

	public final boolean isCancelled(String key) {
		return TaskMonitor.getLocalInstance().get(key) == null;
	}

	public final boolean cancel(String key) {
		Task test = TaskMonitor.getLocalInstance().get(key);
		if (test != null) {
			return test.cancel();
		}
		throw new IllegalStateException("Task already cancelled");
	}

	@Override
	public @NotNull RenderedTask schedule() {
		return fix().parent.schedule();
	}

	@Override
	public @NotNull RenderedTask scheduleAsync() {
		return fix().parent.scheduleAsync();
	}

	@Override
	public @NotNull RenderedTask scheduleLater(long delay) {
		if (key != null) {
			if (predicates != null) {
				return fix().parent.scheduleLater(key, delay, predicates);
			}
			return fix().parent.scheduleLater(key, delay);
		}
		return fix().parent.scheduleLater(delay);
	}

	@Override
	public @NotNull RenderedTask scheduleLater(String key, long delay) {
		return fix().parent.scheduleLater(key, delay);
	}

	@Override
	public @NotNull RenderedTask scheduleLaterAsync(long delay) {
		if (key != null) {
			if (predicates != null) {
				return fix().parent.scheduleLaterAsync(key, delay, predicates);
			}
			return fix().parent.scheduleLaterAsync(key, delay);
		}
		return fix().parent.scheduleLaterAsync(delay);
	}

	@Override
	public @NotNull RenderedTask scheduleLaterAsync(String key, long delay) {
		return fix().parent.scheduleLaterAsync(key, delay);
	}

	@Override
	public @NotNull RenderedTask scheduleTimer(String key, long delay, long period) {
		return fix().parent.scheduleTimer(key, delay, period);
	}

	@Override
	public @NotNull RenderedTask scheduleTimerAsync(String key, long delay, long period) {
		return fix().parent.scheduleTimerAsync(key, delay, period);
	}

	@Override
	public @NotNull RenderedTask scheduleLater(long delay, TaskPredicate<?>... flags) {
		if (key != null) {
			return fix().parent.scheduleLater(key, delay, flags);
		}
		return fix().parent.scheduleLater(delay, flags);
	}

	@Override
	public @NotNull RenderedTask scheduleLater(String key, long delay, TaskPredicate<?>... flags) {
		return fix().parent.scheduleLater(key, delay, flags);
	}

	@Override
	public @NotNull RenderedTask scheduleLaterAsync(long delay, TaskPredicate<?>... flags) {
		if (key != null) {
			return fix().parent.scheduleLaterAsync(key, delay, flags);
		}
		return fix().parent.scheduleLaterAsync(delay, flags);
	}

	@Override
	public @NotNull RenderedTask scheduleLaterAsync(String key, long delay, TaskPredicate<?>... flags) {
		return fix().parent.scheduleLaterAsync(key, delay, flags);
	}

	public @NotNull RenderedTask scheduleTimer(long delay, long period, TaskPredicate<?>... flags) {
		return fix().parent.scheduleTimer(key, delay, period, flags);
	}

	public @NotNull RenderedTask scheduleTimerAsync(long delay, long period, TaskPredicate<?>... flags) {
		return fix().parent.scheduleTimerAsync(key, delay, period, flags);
	}

	@Override
	public @NotNull RenderedTask scheduleTimer(String key, long delay, long period, TaskPredicate<?>... flags) {
		return fix().parent.scheduleTimer(key, delay, period, flags);
	}

	@Override
	public @NotNull RenderedTask scheduleTimerAsync(String key, long delay, long period, TaskPredicate<?>... flags) {
		return fix().parent.scheduleTimerAsync(key, delay, period, flags);
	}
}
