/*
 *  Copyright 2021 ms5984 (Matt) <https://github.com/ms5984>
 *
 *  This file is part of MenuMan, a module of Labyrinth.
 *
 *  MenuMan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either getServerVersion 3 of the
 *  License, or (at your option) any later getServerVersion.
 *
 *  MenuMan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.sanctum.labyrinth.gui.menuman;

import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Class which encapsulates relevant data of InventoryClickEvent
 */
public final class MenuClick {
    /**
     * The player who clicked the Menu.
     */
    protected final Player player;
    private final InventoryClickEvent inventoryClickEvent;

    /**
     * Create a {@link MenuClick} to pass to a {@link ClickAction}.
     *
     * @param e      original event
     * @param player a player
     */
    protected MenuClick(InventoryClickEvent e, Player player) {
        this.player = player;
        inventoryClickEvent = e;
    }

    /**
     * Get the player that clicked the Menu.
     *
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get the full InventoryView of the transaction.
     *
     * @return the InventoryView for the transaction
     */
    public InventoryView getInventoryView() {
        return inventoryClickEvent.getView();
    }

    /**
     * Get the {@link ClickType} of this event.
     * <p>
     * Feel free to explore the enum, it looks powerful.
     *
     * @return {@link InventoryClickEvent#getClick()}
     */
    public ClickType getClickType() {
        return inventoryClickEvent.getClick();
    }

    /**
     * Get an Optional describing the slot clicked, if applicable.
     *
     * @return an Optional of the clicked InventorySlot
     */
    public Optional<InventorySlot> getSlotClicked() {
        return Optional.ofNullable(inventoryClickEvent.getClickedInventory())
                .map(inv -> new InventorySlot(inv, inventoryClickEvent.getSlot()));
    }

    /**
     * Check for the item currently on the cursor.
     * <p>
     * This is usually null, so instead of using the Nullable
     * annotation here I chose to return an {@link Optional}.
     *
     * @return an Optional describing an ItemStack or an empty Optional
     */
    public Optional<ItemStack> getItemOnMouseCursor() {
        return Optional.ofNullable(inventoryClickEvent.getCursor());
    }

    /**
     * Uncancel the event and allow the click to follow through.
     */
    public void allowClick() {
        inventoryClickEvent.setCancelled(false);
    }

    /**
     * Cancel the click event, preventing pickup of the item.
     */
    public void disallowClick() {
        inventoryClickEvent.setCancelled(true);
    }

    /**
     * An encapsulation of inventory slot interactions.
     */
    public static class InventorySlot {
        /**
         * The clicked inventory.
         */
        protected final Inventory inventory;
        /**
         * The index of the slot clicked.
         */
        protected final int slot;

        private InventorySlot(Inventory inventory, int slot) {
            this.inventory = inventory;
            this.slot = slot;
        }

        /**
         * Get the Inventory that was clicked.
         *
         * @return clicked inventory
         */
        public Inventory getClickedInventory() {
            return inventory;
        }

        /**
         * Get the index of the slot.
         *
         * @return {@link InventoryClickEvent#getSlot()}
         * (value in context of clicked inventory)
         */
        public int getIndex() {
            return slot;
        }

        /**
         * Get the item in the slot.
         *
         * @return the current item or null
         */
        @Nullable
        public ItemStack getItem() {
            return inventory.getItem(slot);
        }

        /**
         * Set a new item in the slot.
         *
         * @param itemStack a new item
         */
        public void setItem(ItemStack itemStack) {
            inventory.setItem(slot, itemStack);
        }
    }
}
