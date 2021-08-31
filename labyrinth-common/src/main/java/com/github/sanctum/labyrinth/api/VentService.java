package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.event.custom.VentMap;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to the VentMap.
 */
public interface VentService extends Service {
    /**
     * Get the main VentMap instance.
     *
     * @return main VentMap instance
     */
    @NotNull VentMap getEventMap();
}
