package com.github.sanctum.labyrinth.task;

/**
 * An element describing custom cancellation instances within a task.
 */
@FunctionalInterface
public interface TaskCancellation {

	void execute(ScheduledTask cancellationElement);

}
