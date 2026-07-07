package com.github.sanctum.labyrinth.gui.basalt;

import org.jetbrains.annotations.NotNull;

public class InventoryFrame {

    protected final InventorySnapshot snapshot;
    private long delay;

    public InventoryFrame(@NotNull InventorySnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public InventoryFrame setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    public long getDelay() {
        return delay;
    }
}
