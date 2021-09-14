package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.formatting.component.ActionComponent;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Exposes cached action-wrapped components.
 */
public interface ActionComponentService extends Service {
    /**
     * Get a list of all action-wrapped text components.
     *
     * @return a list of all cached action-wrapped text components
     */
    @NotNull List<ActionComponent> getComponents();

    void addComponent(ActionComponent component);

    void removeComponent(ActionComponent component);

}
