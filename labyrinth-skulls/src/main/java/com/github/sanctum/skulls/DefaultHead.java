package com.github.sanctum.skulls;

import com.github.sanctum.labyrinth.library.Items;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("SameParameterValue")
final class DefaultHead extends CustomHead {

    private final String name;

    private final String category;

    private final ItemStack item;

    /**
     * For internal use only.
     */
    @Deprecated
    DefaultHead() {
        this.name = "ABC";
        this.category = "123";
        this.item = Items.edit().setType(Items.findMaterial("EGG")).build();
    }

    DefaultHead(@NotNull String name, @NotNull String category, @NotNull ItemStack item) {
        this.name = name;
        this.category = category;
        this.item = item;
    }

    DefaultHead(@NotNull String name, @NotNull String category, @NotNull ItemStack item, @NotNull UUID owner) {
        super(owner);
        this.category = category;
        this.name = name;
        this.item = item;
    }

    @Override
    public @NotNull ItemStack getItem() {
        return this.item;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getCategory() {
        return this.category;
    }
}
