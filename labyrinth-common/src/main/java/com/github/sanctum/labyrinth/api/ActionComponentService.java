package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.formatting.component.WrappedComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ActionComponentService {
    /**
     * Get all action wrapped text components.
     *
     * @return A list of all cached text components.
     */
    @NotNull List<WrappedComponent> getComponents();
}
