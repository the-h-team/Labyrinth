package com.github.sanctum.labyrinth.gui.basalt;

import com.github.sanctum.labyrinth.interfacing.Snapshot;
import com.github.sanctum.labyrinth.task.RenderedTask;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class InventorySnapshot implements Snapshot {

    private final ItemStack[] contents, unaltered;
    private Inventory parent;
    private ItemStack[] modifiedContents;
    protected UUID localID;

    public InventorySnapshot(Inventory inventory) {
        this.parent = inventory;
        this.contents = inventory.getContents();
        this.unaltered = new ItemStack[Math.max(0, inventory.getSize() - 1)];
        this.modifiedContents = new ItemStack[Math.max(0, inventory.getSize() - 1)];
        for (int i = 0; i < Math.max(0, inventory.getSize() - 1); i++) {
            ItemStack test = inventory.getContents()[i];
            modifiedContents[i] = test != null ? new ItemStack(test) : new ItemStack(Material.AIR);
            unaltered[i] = test != null ? new ItemStack(test) : new ItemStack(Material.AIR);
        }
    }

    public InventorySnapshot setLocalID(@NotNull UUID id) {
        this.localID = id;
        return this;
    }

    public void wipe() {
        this.parent.clear();
    }

    public void clear() {
        this.modifiedContents = new ItemStack[Math.max(0, this.parent.getSize() - 1)];
    }

    public void set(int index, @Nullable ItemStack itemStack) {
        this.modifiedContents[index] = itemStack;
    }

    public void set(@Nullable ItemStack[] items) {
        if (items == null) {
            this.modifiedContents = new ItemStack[this.contents.length];
        } else {
            this.modifiedContents = items;
        }
    }

    public @Nullable ItemStack get(int index) {
        return modifiedContents[index];
    }

    public @Nullable ItemStack[] get(InventoryPane pane) {
        ItemStack[] items = new ItemStack[this.contents.length];
        int index = 0;
        for (int i : pane.get(parent.getSize())) {
            items[index] = parent.getItem(i);
            index++;
        }
        return items;
    }

    // ensures you can undo changing an inventory to its original state.
    public RenderedTask reset() {
        return TaskScheduler.of(() -> {
            this.modifiedContents = new ItemStack[this.unaltered.length];
            for (int i = 0; i < this.unaltered.length + 1; i++) {
                modifiedContents[i] = new ItemStack(this.unaltered[i]);
            }
        }).schedule();
    }

    public boolean update() {
        return updateWith(this.parent).isRunning();
    }

    public RenderedTask updateWith(@NotNull Inventory inventory) {
        return TaskScheduler.of(() -> inventory.setContents(modifiedContents)).schedule();
    }

}
