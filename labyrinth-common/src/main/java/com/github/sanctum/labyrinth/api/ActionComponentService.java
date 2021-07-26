package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.formatting.component.WrappedComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Exposes cached action-wrapped components.
 */
public interface ActionComponentService {
    /**
     * Get a list of all action-wrapped text components.
     *
     * @return a list of all cached action-wrapped text components
     */
    @NotNull List<WrappedComponent> getComponents();
}
