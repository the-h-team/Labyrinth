package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.library.Applicable;
import org.jetbrains.annotations.NotNull;

final class LabyrinthTaskScheduler implements TaskScheduler {

	private static final TaskMonitor manager = TaskMonitor.getLocalInstance();
	private final Applicable data;

	LabyrinthTaskScheduler(@NotNull Applicable data) {
		this.data = data;
	}

	public @NotNull RenderedTask schedule() {
		return manager.schedule(data);
	}

	public @NotNull RenderedTask scheduleAsync() {
		return manager.scheduleAsync(data);
	}

	public @NotNull RenderedTask scheduleLater(long delay) {
		return manager.scheduleLater(data, delay);
	}

	public @NotNull RenderedTask scheduleLater(String key, long delay) {
		return manager.scheduleLater(data, key, delay);
	}

	public @NotNull RenderedTask scheduleLaterAsync(long delay) {
		return manager.scheduleLaterAsync(data, delay);
	}

	public @NotNull RenderedTask scheduleLaterAsync(String key, long delay) {
		return manager.scheduleLaterAsync(data, key, delay);
	}

	public @NotNull RenderedTask scheduleTimer(String key, long delay, long period) {
		return manager.scheduleTimer(data, key, delay, period);
	}

	public @NotNull RenderedTask scheduleTimerAsync(String key, long delay, long period) {
		return manager.scheduleTimerAsync(data, key, delay, period);
	}

	public @NotNull RenderedTask scheduleLater(long delay, TaskPredicate<?>... flags) {
		return manager.scheduleLater(data, delay, flags);
	}

	public @NotNull RenderedTask scheduleLater(String key, long delay, TaskPredicate<?>... flags) {
		return manager.scheduleLater(data, key, delay, flags);
	}

	public @NotNull RenderedTask scheduleLaterAsync(long delay, TaskPredicate<?>... flags) {
		return manager.scheduleLaterAsync(data, delay, flags);
	}

	public @NotNull RenderedTask scheduleLaterAsync(String key, long delay, TaskPredicate<?>... flags) {
		return manager.scheduleLaterAsync(data, key, delay, flags);
	}

	public @NotNull RenderedTask scheduleTimer(String key, long delay, long period, TaskPredicate<?>... flags) {
		return manager.scheduleTimer(data, key, delay, period, flags);
	}

	public @NotNull RenderedTask scheduleTimerAsync(String key, long delay, long period, TaskPredicate<?>... flags) {
		return manager.scheduleTimerAsync(data, key, delay, period, flags);
	}

}
