package com.github.sanctum.labyrinth.task;

import org.jetbrains.annotations.NotNull;

public interface TaskScheduler {

	@NotNull RenderedTask schedule();

	@NotNull RenderedTask scheduleAsync();

	@NotNull RenderedTask scheduleLater(long delay);

	@NotNull RenderedTask scheduleLater(String key, long delay);

	@NotNull RenderedTask scheduleLater(long delay, TaskPredicate<?>... flags);

	@NotNull RenderedTask scheduleLater(String key, long delay, TaskPredicate<?>... flags);

	@NotNull RenderedTask scheduleLaterAsync(long delay);

	@NotNull RenderedTask scheduleLaterAsync(String key, long delay);

	@NotNull RenderedTask scheduleLaterAsync(long delay, TaskPredicate<?>... flags);

	@NotNull RenderedTask scheduleLaterAsync(String key, long delay, TaskPredicate<?>... flags);

	@NotNull RenderedTask scheduleTimer(String key, long delay, long period);

	@NotNull RenderedTask scheduleTimer(String key, long delay, long period, TaskPredicate<?>... flags);

	@NotNull RenderedTask scheduleTimerAsync(String key, long delay, long period);

	@NotNull RenderedTask scheduleTimerAsync(String key, long delay, long period, TaskPredicate<?>... flags);

	static @NotNull TaskScheduler of(@NotNull Runnable runnable) {
		return new LabyrinthTaskScheduler(runnable::run);
	}


}
