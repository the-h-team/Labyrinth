package com.github.sanctum.labyrinth.gui.basalt.internal.event.click;

import com.github.sanctum.labyrinth.gui.basalt.InventoryContainer;
import com.github.sanctum.labyrinth.gui.basalt.internal.event.action.ActionProcessor;
import com.github.sanctum.labyrinth.gui.basalt.internal.event.action.ActionType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ClickProcessor extends ActionProcessor {

    boolean click(@NotNull InventoryContainer inventory, @NotNull ItemStack item, @NotNull ActionType type);

    @Override
    default boolean interact(@NotNull InventoryContainer inventory, @NotNull ItemStack item, @NotNull ActionType type) {
        return click(inventory, item, type);
    }
}
