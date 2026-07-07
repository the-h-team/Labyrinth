package com.github.sanctum.labyrinth.gui.basalt;

import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.util.Applicable;
import org.jetbrains.annotations.NotNull;

public abstract class InventoryModificationTask implements Applicable {

    private long delay;
    private final InventoryContainer container;

    public InventoryModificationTask(@NotNull InventoryContainer container) {
        this.container = container;
        this.delay = 0L;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public void run() {
        container.CHANGE_REQUESTS.forEach(r -> TaskScheduler.of(() -> {
            r.perform(this.container);
            if (r.temporary()) TaskScheduler.of(() -> container.CHANGE_REQUESTS.remove(r)).scheduleLater(1L);
        }).scheduleLater(this.delay));
    }


}
