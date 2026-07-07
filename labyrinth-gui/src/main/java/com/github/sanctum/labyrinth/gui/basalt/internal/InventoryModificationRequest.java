package com.github.sanctum.labyrinth.gui.basalt.internal;

import com.github.sanctum.labyrinth.gui.basalt.InventoryContainer;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface InventoryModificationRequest {

    void perform(@NotNull InventoryContainer container);

    default boolean temporary() {
        return false;
    }

}
