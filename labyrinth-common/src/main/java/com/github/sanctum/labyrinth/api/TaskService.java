package com.github.sanctum.labyrinth.api;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentLinkedQueue;

// TODO: description; elaborate on "most" in getTasks
public interface TaskService {
    /**
     * Get a queued list of running task ids
     *
     * @return a queue of most running task ids
     */
    @NotNull ConcurrentLinkedQueue<Integer> getTasks();
}
