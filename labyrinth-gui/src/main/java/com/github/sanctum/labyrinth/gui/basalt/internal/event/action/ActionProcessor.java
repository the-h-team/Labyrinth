package com.github.sanctum.labyrinth.gui.basalt.internal.event.action;

import com.github.sanctum.labyrinth.gui.basalt.InventoryContainer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ActionProcessor {

    boolean interact(@NotNull InventoryContainer inventory, @NotNull ItemStack item, @NotNull ActionType type);
}
