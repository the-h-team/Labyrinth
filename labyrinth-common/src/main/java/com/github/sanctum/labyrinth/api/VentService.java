package com.github.sanctum.labyrinth.api;

import com.github.sanctum.panther.event.VentMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to the VentMap.
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
public interface VentService extends Service {
    /**
     * Get the main VentMap instance.
     *
     * @return main VentMap instance
     */
    @NotNull VentMap getEventMap();
}
