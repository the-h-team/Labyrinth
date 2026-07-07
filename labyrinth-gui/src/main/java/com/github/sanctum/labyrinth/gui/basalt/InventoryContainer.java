package com.github.sanctum.labyrinth.gui.basalt;

import com.github.sanctum.labyrinth.gui.basalt.internal.event.action.ActionProcessor;
import com.github.sanctum.labyrinth.gui.basalt.internal.InventoryModificationRequest;
import com.github.sanctum.labyrinth.interfacing.Duplication;
import com.github.sanctum.labyrinth.interfacing.Snapshot;
import com.github.sanctum.labyrinth.task.BukkitTaskPredicate;
import com.github.sanctum.labyrinth.task.RenderedTask;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.container.PantherEntryMap;
import com.github.sanctum.panther.container.PantherList;
import com.github.sanctum.panther.container.PantherMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

// TODO: find some how to compute inventory item building right now inventories are blank, async call that resyncs on inventory get? (non blocking)
public abstract class InventoryContainer implements InventoryHolder, Duplication<InventorySnapshot>, Snapshot {

    private static final PantherMap<UUID, Inventory> CONTAINERS = new PantherEntryMap<>();
    protected final PantherList<InventoryModificationRequest> CHANGE_REQUESTS = new PantherList<>();
    protected final PantherMap<ItemStack, ActionProcessor> ACTION_PROCESSORS = new PantherEntryMap<>();
    private ActionProcessor defaultActionProcessor = (inventory1, item, type) -> true;
    protected RenderedTask inventoryModificationTask;
    private final Inventory inventory;
    private InventorySnapshot localSnapshot;
    private InventoryProperties properties;

    // Build inventory in snapshot then pass it to container update, while building inventory and items use listener injection for click handling & action processing
    public InventoryContainer(InventoryProperties properties) {
        this.properties = properties.hold(this);
        if (properties.isShareable()) {
            this.inventory = properties.pass();
        } else this.inventory = null;
        InventoryModificationTask proc = this.properties.getInventoryModificationTask() != null ? this.getProperties().getInventoryModificationTask() : new InventoryModificationTask(this){};
       this.inventoryModificationTask = TaskScheduler.of(proc).scheduleTimerAsync("LABYRINTH;BASALT;PROCESSOR;" + properties.getId(), 0L, 1L, BukkitTaskPredicate.reduceEmpty());
    }

    public InventoryContainer(Inventory inventory, InventoryProperties properties) {
        this.inventory = inventory;
        this.properties = properties.hold(this);
    }

    public InventoryContainer listen(@NotNull ActionProcessor actionProcessor) {
        this.defaultActionProcessor = actionProcessor;
        return this;
    }

    public InventoryContainer listen(@NotNull ItemStack itemStack, @NotNull ActionProcessor actionProcessor) {
        ACTION_PROCESSORS.put(itemStack, actionProcessor);
        return this;
    }

    public InventoryContainer unlisten(@NotNull ItemStack itemStack) {
        ACTION_PROCESSORS.remove(itemStack);
        return this;
    }

    public InventoryContainer change(@NotNull InventoryModificationRequest changeRequest) {
        CHANGE_REQUESTS.add(changeRequest);
        return this;
    }

    public @NotNull Inventory getInventory(@NotNull UUID id) {
        return getProperties().isShareable() ? inventory : CONTAINERS.computeIfAbsent(id, properties.pass());
    }

    public @NotNull Inventory getInventory() {
        if (getProperties().isShareable()) return inventory;
        throw new IllegalStateException("Cannot access non-shared inventory with this method, wrong usage. Please use a target id.");
    }

    public InventoryPane[] getPanes() {
        return InventoryPane.values();
    }

    public @Nullable InventorySnapshot newDuplicate() {
        if (!getProperties().isShareable() || inventory == null) return null;
        return this.localSnapshot = new InventorySnapshot(inventory);
    }

    public @Nullable InventorySnapshot newDuplicate(@NotNull UUID id) {
        return this.localSnapshot = new InventorySnapshot(getInventory(id)).setLocalID(id);
    }

    @Override
    public void inform(@NotNull Snapshot snapshot) {
        if (snapshot instanceof InventorySnapshot) {
            this.localSnapshot = (InventorySnapshot) snapshot;
        }
    }

    @Override
    public boolean update() throws IllegalArgumentException {
        // update local snapshot
        if (this.localSnapshot != null) {
            return this.localSnapshot.update();
        }
        return false;
    }

    public void newProperties(@NotNull InventoryProperties properties) {
        this.properties = properties;
    }

    public InventoryProperties getProperties() {
        return properties;
    }
}
