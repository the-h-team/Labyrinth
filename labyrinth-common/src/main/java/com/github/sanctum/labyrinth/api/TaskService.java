package com.github.sanctum.labyrinth.api;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentLinkedQueue;

public interface TaskService {
    /**
     * Get a queued list of running task id's
     *
     * @return A list of most running task id's
     */
    @NotNull ConcurrentLinkedQueue<Integer> getTasks();
}
