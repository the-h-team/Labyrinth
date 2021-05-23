package com.github.sanctum.labyrinth.gui.menuman;

import com.github.sanctum.labyrinth.gui.InventoryRows;
import com.github.sanctum.labyrinth.gui.animated.AnimatedElement;
import com.github.sanctum.labyrinth.gui.animated.FinalAnimatedElement;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;

public final class AnimatedMenu {
    private final JavaPlugin plugin;
    private final LinkedList<FinalAnimatedElement> elements = new LinkedList<>();
    private final AnimatedElement fillerElement;
    private final CloseAction closeAction;
    /**
     * Number of rows in the generated Inventory.
     */
    public final InventoryRows numberOfRows;
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

    public AnimatedMenu(AnimatedMenuBuilder animatedMenuBuilder, JavaPlugin javaPlugin) {
        this.plugin = javaPlugin;
        this.numberOfRows = animatedMenuBuilder.numberOfRows;
//        this.title = (menuBuilder.title != null) ? menuBuilder.title : "Menu#"+hashCode();
        this.cancelClickLower = animatedMenuBuilder.cancelLowerInvClick;
        this.allowPickupFromMenu = animatedMenuBuilder.allowItemPickup;
        this.allowShiftClickLower = animatedMenuBuilder.allowLowerInvShiftClick;
        this.closeAction = animatedMenuBuilder.closeAction;
        animatedMenuBuilder.elements.forEach((slot, element) -> elements.add(new FinalAnimatedElement(element, slot)));
        this.fillerElement = animatedMenuBuilder.fillerElement;
    }

    protected class ClickListener implements Listener {
        private BukkitRunnable pendingDelete;

        private ClickListener() {}

        /**
         * Process InventoryClickEvent and encapsulate to send
         * to actions if so defined.
         *
         * @param e original click event
         */
        @EventHandler
        public void onMenuClick(InventoryClickEvent e) {
            // If the event's top inventory isn't ours, ignore it
            if (e.getInventory() != inventory) {
                return;
            }
            // If the bottom inventory was clicked...
            if (e.getClickedInventory() == e.getView().getBottomInventory()) {
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
            if (e.getClickedInventory() == e.getInventory()) {
                // search the elements for the slot
                elements.stream()
                        .filter(element -> element.slot == slot)
                        .findAny()
                        .ifPresent(element -> {
                            e.setCancelled(true);
                            // Perform action as needed
                            if (element.animatedElement instanceof ClickAction) {
                                ((ClickAction) element.animatedElement).onClick(new MenuClick(e, player));
                            }
                        });
                // if we are not allowing ANY pickup from the top menu, cancel the event
                if (!allowPickupFromMenu) {
                    e.setCancelled(true);
                }
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
            if (e.getInventory() != inventory) {
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
            if (e.getInventory() != inventory) {
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
            if (e.getInventory() != inventory) {
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
