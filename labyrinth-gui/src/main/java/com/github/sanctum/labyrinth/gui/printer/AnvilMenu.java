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
public class AnvilMenu {

    private Player holder;
    private final AnvilMechanics nms;
    private final String title;
    private final AnvilListener listener;
    private int containerId;
    private Inventory inventory;
    protected ItemBuilder LEFT_ITEM;
    protected ItemBuilder RIGHT_ITEM;
    protected AnvilCloseEvent event;
    private boolean visible;
    private boolean empty;


    public AnvilMenu(Player holder, String title, ItemBuilder left, ItemBuilder right) throws InstantiationException {
        this.holder = holder;
        this.LEFT_ITEM = left;
        this.title = title;
        this.RIGHT_ITEM = right;
        this.listener = new AnvilListener();
        AnvilMechanics mechanics = Bukkit.getServicesManager().load(AnvilMechanics.class);

        if (mechanics != null) {
            this.nms = mechanics;
        } else throw new InstantiationException("No anvil mechanic service found!!");

    }

    public AnvilMenu(String title, ItemBuilder left, ItemBuilder right) throws InstantiationException {
        this.LEFT_ITEM = left;
        this.title = title;
        this.RIGHT_ITEM = right;
        this.listener = new AnvilListener();
        AnvilMechanics mechanics = Bukkit.getServicesManager().load(AnvilMechanics.class);

        if (mechanics != null) {
            this.nms = mechanics;
        } else throw new InstantiationException("No anvil mechanic service found!!");
    }

    /**
     * Set who is going to be viewing this inventory.
     *
     * @param player The viewer of the gui.
     * @return The same AnvilMenu.
     */
    public AnvilMenu setViewer(Player player) {
        this.holder = player;
        return this;
    }

    /**
     * Apply normal running logic for this anvil (Behaves like a normal anvil).
     *
     * @return The same AnvilMenu.
     */
    public AnvilMenu isEmpty() {
        this.empty = true;
        return this;
    }

    /**
     * Apply running logic to the closing event of this menu.
     *
     * @param event The expression to run
     * @return The same AnvilMenu.
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

        inventory.setItem(Slot.INPUT_LEFT.get(), LEFT_ITEM.item);

        if (RIGHT_ITEM != null) {
            inventory.setItem(Slot.INPUT_RIGHT.get(), RIGHT_ITEM.item);
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
     * @param sendClosePacket If the inventory is a natural event based close.
     * @throws IllegalArgumentException If the inventory isn't visible
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
     * @return The inventory for this menu.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Get the title of this menu.
     *
     * @return The menu title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the {@link Listener} for this menu.
     *
     * @return The listener for this menu.
     */
    public AnvilListener getListener() {
        return listener;
    }

    /**
     * @return The left item builder.
     */
    public ItemBuilder getLeft() {
        return LEFT_ITEM;
    }

    /**
     * @return The right item builder.
     */
    public ItemBuilder getRight() {
        return RIGHT_ITEM;
    }

    /**
     * Check if the menu is open for anyone.
     *
     * @return false if the menu isnt currently open
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Get whos viewing the menu.
     *
     * @return Get the viewer of the inventory.
     */
    public Player getViewer() {
        return holder;
    }

    /**
     * Simply holds the listeners for the GUI
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
                            getLeft().click.execute(clicker, meta.getDisplayName(), meta.getDisplayName().split(" "));
                        }
                    } else closeInventory(false);
                } else {
                    final ItemStack clicked = inventory.getItem(e.getRawSlot());
                    if (clicked == null || clicked.getType() == Material.AIR) return;
                    if (getRight() != null && getRight().click != null) {
                        if (clicked.getType() == getRight().item.getType()) {
                            final ItemMeta meta = clicked.getItemMeta();
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
