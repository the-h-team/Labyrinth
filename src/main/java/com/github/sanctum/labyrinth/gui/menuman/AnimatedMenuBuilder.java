package com.github.sanctum.labyrinth.gui.menuman;

import com.github.sanctum.labyrinth.gui.InventoryRows;
import com.github.sanctum.labyrinth.gui.animated.AnimatedElement;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class AnimatedMenuBuilder {
    protected final HashMap<Integer, AnimatedElement> elements = new HashMap<>();
    /**
     * Describes the number of rows and slots in the final Menu.
     */
    public final InventoryRows numberOfRows;
    /**
     * Describes filler element.
     */
    protected AnimatedElement fillerElement;
    /**
     * Callback to run on menu close. Defaults to null.
     */
    protected CloseAction closeAction;
    /**
     * Determine whether clicks on the lower inventory should be cancelled.
     */
    protected boolean cancelLowerInvClick;
    /**
     * Allow items to be removed from the menu inventory.
     */
    protected boolean allowItemPickup;
    /**
     * Allow shift-clicking of items from the lower inventory.
     */
    protected boolean allowLowerInvShiftClick;

    public AnimatedMenuBuilder(@NotNull InventoryRows numberOfRows) {
        this.numberOfRows = numberOfRows;
    }
}
