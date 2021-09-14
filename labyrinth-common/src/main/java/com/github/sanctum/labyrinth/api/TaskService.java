package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.task.TaskChain;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

// TODO: description; elaborate on "most" in getConcurrentTaskIds
public interface TaskService extends Service {

    int SYNCHRONOUS = 0;
    int ASYNCHRONOUS = 1;

    /**
     * Get a queued list of running task ids
     *
     * @return a queue of most running task ids, if a task query attempt is blocked the sole purpose of this collection type is to avoid blocks and continue indexing.
     */
    @NotNull ConcurrentLinkedQueue<Integer> getConcurrentTaskIds();

    /**
     * The labyrinth default chain for async/synchronously bound tasks.
     * 
     * @return A task scheduler / map of running/scheduled tasks.
     */
    TaskChain getScheduler(@MagicConstant(intValues = {SYNCHRONOUS, ASYNCHRONOUS}) int runtime);

}
