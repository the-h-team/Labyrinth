package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.event.custom.VentMap;
import org.jetbrains.annotations.NotNull;

public interface VentService {
    /**
     * Get the main VentMap instance.
     *
     * @return main VentMap instance
     */
    @NotNull VentMap getEventMap();
}
