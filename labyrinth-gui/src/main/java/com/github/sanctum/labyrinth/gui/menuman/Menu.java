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

import com.github.sanctum.labyrinth.gui.InventoryRows;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A class representing a created Menu.
 */
public final class Menu {
    private final JavaPlugin plugin;
    private final ItemStack[] initialContents;
    private final Map<Integer, ItemStack> contents;
    private final Map<Integer, ClickAction> actions;
    private final CloseAction closeAction;
    /**
     * Number of rows in the generated Inventory.
     */
    public final InventoryRows numberOfRows;
    /**
     * Title of generated Inventory.
     */
    public final String title;
    /**
     * Defines whether clicks on lower inventory are cancelled.
     */
    public final boolean cancelClickLower;
    /**
     * Defines default item pickup behavior for top inventory.
     */
    public final boolean allowPickupFromMenu;
    /**
     * Defines shift-click behavior on the lower inventory.
     */
    public final boolean allowShiftClickLower;
    private Inventory inventory;

    /**
     * Create a new Menu using the data from a builder and a plugin reference.
     *
     * @param menuBuilder a MenuBuilder
     * @param javaPlugin  your plugin
     */
    protected Menu(MenuBuilder menuBuilder, JavaPlugin javaPlugin) throws InstantiationException {
        this.plugin = javaPlugin;
        this.numberOfRows = menuBuilder.numberOfRows;
        this.title = (menuBuilder.title != null) ? menuBuilder.title : "Menu#" + hashCode();
        this.initialContents = (menuBuilder.initialContents == null) ? null : menuBuilder.initialContents;
        this.cancelClickLower = menuBuilder.cancelLowerInvClick;
        this.allowPickupFromMenu = menuBuilder.allowItemPickup;
        this.allowShiftClickLower = menuBuilder.allowLowerInvShiftClick;
        this.actions = new HashMap<>(menuBuilder.actions);
        this.closeAction = menuBuilder.closeAction;
        this.contents = new HashMap<>(numberOfRows.slotCount);
        menuBuilder.items.forEach((index, element) -> contents.put(index, element.generateComplete()));
        final ItemStack fillerItem = Optional
                .ofNullable(menuBuilder.fillerItem)
                .map(MenuElement::generateComplete)
                .orElse(null);
        if (fillerItem != null) {
            for (int i = 0; i < numberOfRows.slotCount; ++i) {
                contents.putIfAbsent(i, fillerItem);
            }
        }
        if (menuBuilder.fillerAction != null) {
            for (int i = 0; i < numberOfRows.slotCount; ++i) {
                actions.putIfAbsent(i, menuBuilder.fillerAction);
            }
        }

        if (this.plugin == null) throw new InstantiationException("The plugin instance provided is not valid");

        Bukkit.getPluginManager().registerEvents(new ClickListener(), plugin);
    }

    /**
     * Lazy initialization of inventory on first getMechanics.
     * <p>
     * Added bonus: listener returns faster before getInventory is called.
     *
     * @return generated Inventory
     */
    private Inventory getInventory() {
        if (inventory == null) {
            inventory = Bukkit.createInventory(null, numberOfRows.slotCount, title);
            if (initialContents != null) inventory.setContents(initialContents);
            for (Map.Entry<Integer, ItemStack> element : contents.entrySet()) {
                inventory.setItem(element.getKey(), element.getValue());
            }
        }
        return inventory;
    }

    /**
     * Open this Menu for the given player.
     *
     * @param player player to open menu for
     */
    public void open(Player player) {
        player.openInventory(getInventory());
    }

    /**
     * Gets the Inventory at this exact moment.
     * <p>
     * Expressed as an Optional (no viewers = no Inventory).
     * <p><b>Not</b> async safe.
     *
     * @return an Optional describing the currently generated Inventory
     */
    public Optional<Inventory> getCurrentInventory() {
        return Optional.ofNullable(inventory);
    }

    /**
     * Get all Players currently viewing this menu.
     * <p>
     * Returns an empty set if inventory == null
     *
     * @return a set of Players viewing this menu
     */
    public Set<Player> getViewers() {
        if (inventory == null) return Collections.emptySet();
        return inventory.getViewers().parallelStream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toSet());
    }

    public static class Paginated<T> {

        private final PaginatedBuilder<T> builder;

        protected Paginated(PaginatedBuilder<T> builder) {
            this.builder = builder;
        }

        /**
         * Open the menu for a specified player
         *
         * @param p The player to open the menu for.
         */
        public void open(Player p) {
            this.builder.INVENTORY = Bukkit.createInventory(null, this.builder.SIZE, this.builder.TITLE.replace("{PAGE}", "" + (this.builder.PAGE + 1)).replace("{MAX}", "" + this.builder.getMaxPages()));
            if (this.builder.LIVE) {
                this.builder.INVENTORY.setMaxStackSize(1);
                if (this.builder.TASK.containsKey(p)) {
                    this.builder.TASK.get(p).cancelTask();
                }

                this.builder.TASK.put(p, Schedule.async(() -> {
                    Schedule.sync(() -> {
                        this.builder.INVENTORY.clear();
                        this.builder.adjust();
                    }).run();
                }));
                this.builder.TASK.get(p).repeat(1, 5);
                Schedule.sync(() -> p.openInventory(this.builder.getInventory())).waitReal(2);
            } else {
                p.openInventory(this.builder.adjust(1).getInventory());
            }
        }

        /**
         * Open the menu @ a specified page for a specified player
         *
         * @param p    The player to open the menu for.
         * @param page The page to open the menu @.
         */
        public void open(Player p, int page) {
            this.builder.INVENTORY = Bukkit.createInventory(null, this.builder.SIZE, this.builder.TITLE.replace("{PAGE}", "" + (this.builder.PAGE + 1)).replace("{MAX}", "" + this.builder.getMaxPages()));
            if (this.builder.LIVE) {

                if (this.builder.TASK.containsKey(p)) {
                    this.builder.TASK.get(p).cancelTask();
                }

                this.builder.TASK.put(p, Schedule.async(() -> Schedule.sync(() -> {
                    this.builder.INVENTORY.clear();
                    this.builder.adjust(page);
                }).run()));
                this.builder.TASK.get(p).repeat(1, 5);
                Schedule.sync(() -> p.openInventory(this.builder.getInventory())).waitReal(2);
            } else {
                p.openInventory(this.builder.adjust(Math.min(page, this.builder.getMaxPages())).getInventory());
            }
        }

        public void liven(Player p, int delay, int period) {
            this.builder.INVENTORY = Bukkit.createInventory(null, this.builder.SIZE, this.builder.TITLE.replace("{PAGE}", "" + (this.builder.PAGE + 1)).replace("{MAX}", "" + this.builder.getMaxPages()));
            if (this.builder.LIVE) {

                if (this.builder.TASK.containsKey(p)) {
                    this.builder.TASK.get(p).cancelTask();
                }

                this.builder.TASK.put(p, Schedule.async(() -> {
                    Schedule.sync(() -> {
                        this.builder.INVENTORY.clear();
                        this.builder.adjust();
                    }).run();
                }));
                this.builder.TASK.get(p).repeat(delay, period);
                Schedule.sync(() -> p.openInventory(this.builder.getInventory())).waitReal(delay + 1);
            } else {
                p.openInventory(this.builder.adjust(1).getInventory());
            }
        }

        public void liven(Player p, int page, int delay, int period) {
            this.builder.INVENTORY = Bukkit.createInventory(null, this.builder.SIZE, this.builder.TITLE.replace("{PAGE}", "" + (this.builder.PAGE + 1)).replace("{MAX}", "" + this.builder.getMaxPages()));
            if (this.builder.LIVE) {

                if (this.builder.TASK.containsKey(p)) {
                    this.builder.TASK.get(p).cancelTask();
                }

                this.builder.TASK.put(p, Schedule.async(() -> Schedule.sync(() -> {
                    this.builder.INVENTORY.clear();
                    this.builder.adjust(page);
                }).run()));
                this.builder.TASK.get(p).repeat(delay, period);
                Schedule.sync(() -> p.openInventory(this.builder.getInventory())).waitReal(delay + 1);
            } else {
                p.openInventory(this.builder.adjust(Math.min(page, this.builder.getMaxPages())).getInventory());
            }
        }

        public Menu.Paginated<T> liven() {
            this.builder.LIVE = true;
            return this;
        }

        /**
         * Update the collection used within the menu.
         *
         * @param collection The string collection to update with.
         */
        public void recollect(Collection<T> collection) {
            this.builder.COLLECTION = new LinkedList<>(collection);
        }

        /**
         * Update the collection used within the menu and open it for a specified player.
         *
         * @param collection The string collection to update with.
         */
        public void recollect(Player p, Collection<T> collection) {
            this.builder.COLLECTION = new LinkedList<>(collection);
            open(p);
        }

        /**
         * Update the collection used within the menu and open it for a specified player.
         *
         * @param collection The string collection to update with.
         */
        public void recollect(Player p, int page, Collection<T> collection) {
            this.builder.COLLECTION = new LinkedList<>(collection);
            open(p, page);
        }

        /**
         * Clear cache, remove un-used handlers.
         */
        public void unregister() {
            HandlerList.unregisterAll(this.builder.getController());
        }

        /**
         * Get the unique ID of the menu object.
         *
         * @return The menu objects unique ID.
         */
        public UUID getId() {
            return this.builder.getId();
        }

        /**
         * {@inheritDoc}
         */
        public Inventory getInventory() {
            return this.builder.getInventory();
        }

        /**
         * {@inheritDoc}
         */
        public List<T> getCollection() {
            return this.builder.getCollection();
        }

        /**
         * {@inheritDoc}
         */
        public int getMaxPages() {
            return this.builder.getMaxPages();
        }

        /**
         * {@inheritDoc}
         */
        public PaginatedBuilder<T>.PaginatedListener getListener() {
            return this.builder.getController();
        }

        /**
         * {@inheritDoc}
         */
        public Plugin getPlugin() {
            return this.builder.getPlugin();
        }

    }

    /**
     * Registered listener which passes Inventory event
     * information to the menu's actions.
     */
    protected class ClickListener implements Listener {
        private BukkitRunnable pendingDelete;

        private ClickListener() {
        }

        /**
         * Process InventoryClickEvent and encapsulate to send
         * to MenuAction if so defined.
         *
         * @param e original click event
         */
        @EventHandler
        public void onMenuClick(InventoryClickEvent e) {
            // If the top inventory isn't ours, ignore it
            if (!e.getInventory().equals(inventory)) {
                return;
            }
            // If the bottom inventory was clicked...
            if (e.getView().getBottomInventory().equals(e.getClickedInventory())) {
                // and we want to cancel clicks for the bottom, cancel the event
                if (cancelClickLower) e.setCancelled(true);
                // if we are not allowing shift clicks
                if (e.isShiftClick() && !allowShiftClickLower) {
                    e.setCancelled(true);
                }
                return;
            }
            // If the player used hotkeys, cancel the event
            if (e.getHotbarButton() != -1) {
                e.setCancelled(true);
            }
            final HumanEntity whoClicked = e.getWhoClicked();
            // if for some reason the click isn't a player, ignore it
            if (!(whoClicked instanceof Player)) {
                return;
            }
            final Player player = (Player) whoClicked;
            final int slot = e.getSlot();
            // if this is a menu click (top inventory)
            if (e.getInventory().equals(e.getClickedInventory())) {
                // search the menu elements map for the slot
                if (contents.keySet().parallelStream().anyMatch(key -> key == slot)) {
                    // cancel the click
                    e.setCancelled(true);
                }
                // if we are not allowing ANY pickup from the top menu, cancel the event
                if (!allowPickupFromMenu) {
                    e.setCancelled(true);
                }
            }
            // if this slot has an associated action
            if (actions.containsKey(slot)) {
                // run action function
                actions.get(slot).onClick(new MenuClick(e, player));
            }
        }

        /**
         * Process {@link InventoryDragEvent}.
         * <p>
         * Cancel item drag events which include the top inventory.
         *
         * @param e original InventoryDragEvent
         */
        @EventHandler
        public void onMenuDrag(InventoryDragEvent e) {
            // If the top inventory isn't ours, ignore it
            if (!e.getInventory().equals(inventory)) {
                return;
            }
            // If the slots include the top inventory, cancel the event
            if (e.getRawSlots().parallelStream().anyMatch(slot -> slot < numberOfRows.slotCount)) {
                e.setCancelled(true);
            }
        }

        /**
         * Process {@link InventoryOpenEvent}.
         * <p>
         * If the currently made {@link Inventory} is opened again
         * by another player before the task timer has elapsed,
         * cancel the current destruction task.
         *
         * @param e original InventoryOpenEvent.
         */
        @EventHandler
        public void onMenuOpen(InventoryOpenEvent e) {
            if (!e.getInventory().equals(inventory)) {
                return;
            }
            if (pendingDelete != null) {
                pendingDelete.cancel();
                pendingDelete = null;
            }
        }

        /**
         * Perform close logic and schedule cleanup.
         * <p>These steps include running the CloseAction callback
         * (if present) and then the following:
         * <ul>
         *     <li>Setup a task to null the inventory after all
         *     viewers have closed it.
         *     <li>Sets this task to run in ten ticks, trying again every
         *     50 ticks until all viewers have left.
         *
         * @param e original InventoryCloseEvent
         */
        @EventHandler
        public void onMenuClose(InventoryCloseEvent e) {
            if (!e.getInventory().equals(inventory)) {
                return;
            }
            final HumanEntity closer = e.getPlayer();
            if (closeAction != null && closer instanceof Player) {
                closeAction.onClose(new MenuClose(e, (Player) closer));
            }
            pendingDelete = new BukkitRunnable() {
                @Override
                public void run() {
                    if (inventory == null) cancel();
                    if (inventory.getViewers().isEmpty()) {
                        inventory = null;
                        this.cancel();
                    }
                }
            };
            pendingDelete.runTaskTimer(plugin, 10L, 50L);
        }
    }
}
