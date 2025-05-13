package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.formatting.component.ActionComponent;
import com.github.sanctum.panther.util.Deployable;
import java.util.List;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Exposes cached action-wrapped components.
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
public interface ActionComponentService extends Service {
    /**
     * Get a list of all action-wrapped text components.
     *
     * @return a list of all cached action-wrapped text components
     */
    @NotNull List<ActionComponent> getComponents();

    /**
     * Register an action component for live use.
     *
     * @param component The component to use.
     * @return A deployable operation.
     */
    Deployable<Void> registerComponent(ActionComponent component);

    /**
     * Un-register an action component from cache.
     *
     * @param component The component to remove.
     * @return A deployable operation.
     */
    Deployable<Void> removeComponent(ActionComponent component);

}
