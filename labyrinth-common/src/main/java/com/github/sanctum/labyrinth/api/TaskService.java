package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.library.Applicable;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jetbrains.annotations.NotNull;

// TODO: description; elaborate on "most" in getTasks
public interface TaskService extends Service {
    /**
     * Get a queued list of running task ids
     *
     * @return a queue of most running task ids
     */
    @NotNull ConcurrentLinkedQueue<Integer> getTasks();

    /**
     * Schedule a data operation to run on next tick.
     *
     * @param applicable The data to schedule.
     */
    void scheduleNext(Applicable applicable);

    /**
     * Schedule a data operation to run after an allotted time.
     *
     * @param applicable The data to schedule.
     * @param ticks The amount of time to wait in ticks.
     */
    void scheduleLater(Applicable applicable, int ticks);

    /**
     * Schedule a data operation to run infinitely.
     *
     * @param applicable The data to schedule.
     * @param delay The amount of time to wait before firing.
     * @param period The amount of time to wait in-between firing before hitting the delay again.
     */
    void scheduleAlways(Applicable applicable, int delay, int period);

}
