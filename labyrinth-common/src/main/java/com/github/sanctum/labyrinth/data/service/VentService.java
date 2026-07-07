package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.panther.event.VentMap;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to the VentMap.
 */
public interface VentService extends Service {
    /**
     * Get the main VentMap instance. {@link VentMap}
     *
     * @return main VentMap instance
     */
    @NotNull VentMap getVentMap();
}
