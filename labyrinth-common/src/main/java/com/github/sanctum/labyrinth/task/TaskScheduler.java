package com.github.sanctum.labyrinth.task;

import com.github.sanctum.panther.util.Task;
import org.jetbrains.annotations.NotNull;

public interface TaskScheduler {

	@NotNull RenderedTask schedule();

	@NotNull RenderedTask scheduleAsync();

	@NotNull RenderedTask scheduleLater(long delay);

	@NotNull RenderedTask scheduleLater(String key, long delay);

	@NotNull RenderedTask scheduleLater(long delay, BukkitTaskPredicate<?>... flags);

	@NotNull RenderedTask scheduleLater(String key, long delay, BukkitTaskPredicate<?>... flags);

	@NotNull RenderedTask scheduleLaterAsync(long delay);

	@NotNull RenderedTask scheduleLaterAsync(String key, long delay);

	@NotNull RenderedTask scheduleLaterAsync(long delay, BukkitTaskPredicate<?>... flags);

	@NotNull RenderedTask scheduleLaterAsync(String key, long delay, BukkitTaskPredicate<?>... flags);

	@NotNull RenderedTask scheduleTimer(String key, long delay, long period);

	@NotNull RenderedTask scheduleTimer(String key, long delay, long period, BukkitTaskPredicate<?>... flags);

	@NotNull RenderedTask scheduleTimerAsync(String key, long delay, long period);

	@NotNull RenderedTask scheduleTimerAsync(String key, long delay, long period, BukkitTaskPredicate<?>... flags);

	static @NotNull TaskScheduler of(@NotNull Runnable runnable) {
		return new NormalTaskScheduler(runnable);
	}

	static @NotNull TaskScheduler of(@NotNull Task task) {
		return new CustomTaskScheduler(task);
	}

}
