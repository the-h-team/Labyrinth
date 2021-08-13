package com.github.sanctum.labyrinth.gui.printer;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.service.AnvilMechanics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The final output of an {@link AnvilBuilder}
 *
 * @author Hempfest
 */
@Deprecated
public class AnvilMenu {

    private Player holder;
    private final AnvilMechanics nms;
    private final String title;
    private final AnvilListener listener;
    private int containerId;
    private Inventory inventory;
    protected ItemBuilder leftItem; // TODO: decide finality
    protected ItemBuilder rightItem; // TODO: decide finality
    protected AnvilCloseEvent event;
    private boolean visible;
    private boolean empty;


    public AnvilMenu(Player holder, String title, ItemBuilder left, ItemBuilder right) throws InstantiationException {
        this.holder = holder;
        this.leftItem = left;
        this.title = title;
        this.rightItem = right;
        this.listener = new AnvilListener();
        AnvilMechanics mechanics = Bukkit.getServicesManager().load(AnvilMechanics.class);

        if (mechanics != null) {
            this.nms = mechanics;
        } else throw new InstantiationException("No anvil mechanic service found!!");

    }

    public AnvilMenu(String title, ItemBuilder left, ItemBuilder right) throws InstantiationException {
        this.leftItem = left;
        this.title = title;
        this.rightItem = right;
        this.listener = new AnvilListener();
        AnvilMechanics mechanics = Bukkit.getServicesManager().load(AnvilMechanics.class);

        if (mechanics != null) {
            this.nms = mechanics;
        } else throw new InstantiationException("No anvil mechanic service found!!");
    }

    /**
     * Set who is going to be viewing this inventory.
     *
     * @param player the viewer of the gui
     * @return this AnvilMenu
     */
    public AnvilMenu setViewer(Player player) {
        this.holder = player;
        return this;
    }

    /**
     * Apply normal running logic for this anvil (Behaves like a normal anvil).
     *
     * @return this AnvilMenu
     */
    public AnvilMenu isEmpty() {
        this.empty = true;
        return this;
    }

    /**
     * Apply running logic to the closing event of this menu.
     *
     * @param event the expression to run
     * @return this AnvilMenu
     */
    public AnvilMenu applyClosingLogic(AnvilCloseEvent event) {
        this.event = event;
        return this;
    }

    /**
     * Open the menu for the provided player.
     */
    public void open() {

        if (this.holder == null) {
            throw new IllegalStateException("No craft player was found to source the menu to.");
        }

        nms.handleInventoryCloseEvent(holder);
        nms.setActiveContainerDefault(holder);

        Bukkit.getPluginManager().registerEvents(listener, LabyrinthProvider.getInstance().getPluginInstance());

        final Object container = nms.newContainerAnvil(holder, title);

        inventory = nms.toBukkitInventory(container);

        inventory.setItem(Slot.INPUT_LEFT.get(), leftItem.item);

        if (rightItem != null) {
            inventory.setItem(Slot.INPUT_RIGHT.get(), rightItem.item);
        }

        containerId = nms.getNextContainerId(holder, container);
        nms.sendPacketOpenWindow(holder, containerId, title);
        nms.setActiveContainer(holder, container);
        nms.setActiveContainerId(container, containerId);
        nms.addActiveContainerSlotListener(container, holder);

        visible = true;
    }

    /**
     * Closes the inventory if it's open.
     *
     * @param sendClosePacket if the inventory is a natural event-based close
     * @throws IllegalArgumentException if the inventory isn't visible
     */
    public void closeInventory(boolean sendClosePacket) {
        if (!visible)
            throw new IllegalArgumentException("You can't close an inventory that isn't open!");
        visible = false;

        if (!sendClosePacket)
            nms.handleInventoryCloseEvent(holder);
        nms.setActiveContainerDefault(holder);
        nms.sendPacketCloseWindow(holder, containerId);

        HandlerList.unregisterAll(listener);
    }

    /**
     * Returns the inventory for this anvil menu.
     *
     * @return the inventory for this menu
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Get the title of this menu.
     *
     * @return the title of this menu
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the {@link Listener} for this menu.
     *
     * @return the listener for this menu
     */
    public AnvilListener getListener() {
        return listener;
    }

    /**
     * Get the left item builder.
     *
     * @return the left item builder
     */
    public ItemBuilder getLeft() {
        return leftItem;
    }

    /**
     * Get the right item builder.
     *
     * @return the right item builder
     */
    public ItemBuilder getRight() {
        return rightItem;
    }

    /**
     * Check if the menu is open for anyone.
     *
     * @return false if the menu is not currently open
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Get who is viewing the menu.
     *
     * @return the viewer of the inventory
     */
    public Player getViewer() {
        return holder;
    }

    /**
     * Contains the EventHandlers for the GUI.
     */
    private class AnvilListener implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (e.getInventory().equals(inventory)) {
                if (!empty) {
                    e.setCancelled(true);
                }
                final Player clicker = (Player) e.getWhoClicked();
                if (e.getRawSlot() == Slot.OUTPUT.get() || e.getRawSlot() == Slot.OUTPUT.get()) {
                    final ItemStack clicked = inventory.getItem(e.getRawSlot());
                    if (clicked == null || clicked.getType() == Material.AIR) return;
                    if (getLeft() != null && getLeft().click != null) {
                        if (clicked.getType() == getLeft().item.getType()) {
                            final ItemMeta meta = clicked.getItemMeta();
                            //noinspection ConstantConditions
                            getLeft().click.execute(clicker, meta.getDisplayName(), meta.getDisplayName().split(" "));
                        }
                    } else closeInventory(false);
                } else {
                    final ItemStack clicked = inventory.getItem(e.getRawSlot());
                    if (clicked == null || clicked.getType() == Material.AIR) return;
                    if (getRight() != null && getRight().click != null) {
                        if (clicked.getType() == getRight().item.getType()) {
                            final ItemMeta meta = clicked.getItemMeta();
                            //noinspection ConstantConditions
                            getRight().click.execute(clicker, meta.getDisplayName(), meta.getDisplayName().split(" "));
                        }
                    }
                }
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {

            if (!(e.getPlayer() instanceof Player))
                return;

            if (e.getInventory().equals(inventory)) {
                if (visible) {
                    if (event != null) {
                        event.execute((Player) e.getPlayer(), e.getView(), AnvilMenu.this);
                    }
                    closeInventory(true);
                }
                e.getInventory().clear();
            }
        }
    }


    /**
     * Encapsulates {@link Integer} data needed to provide slot values to the {@link AnvilMenu} GUI.
     */
    public enum Slot {

        INPUT_LEFT(0), INPUT_RIGHT(1), OUTPUT(2);

        private final int slot;

        Slot(int slot) {
            this.slot = slot;
        }

        public int get() {
            return slot;
        }

    }

}
