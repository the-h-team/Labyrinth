package com.github.sanctum.labyrinth.gui.printer;

import com.github.sanctum.labyrinth.Labyrinth;
import net.wesjd.anvilgui.version.VersionMatcher;
import net.wesjd.anvilgui.version.VersionWrapper;
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
 * An anvil gui, used for gathering a user's input
 *
 * @author Wesley Smith
 * @since 1.0
 */
public class AnvilMenu {

    /**
     * The player who has the GUI open
     */
    private final Player holder;

    /**
     * The {@link VersionWrapper} for this server
     */
    private final VersionWrapper nms;

    private final String title;

    /**
     * The container id of the inventory, used for NMS methods
     */
    private int containerId;
    /**
     * The inventory that is used on the Bukkit side of things
     */
    private Inventory inventory;
    /**
     * The listener holder class
     */
    private final AnvilListener listener;

    protected ItemBuilder LEFT_ITEM;

    protected ItemBuilder RIGHT_ITEM;

    /**
     * Represents the state of the inventory being open
     */
    private boolean visible;


    public AnvilMenu(Player holder, String title, ItemBuilder left, ItemBuilder right) {
        this.holder = holder;
        this.LEFT_ITEM = left;
        this.title = title;
        this.RIGHT_ITEM = right;
        this.listener = new AnvilListener();
        this.nms = new VersionMatcher().match();
    }

    public void open() {
        nms.handleInventoryCloseEvent(holder);
        nms.setActiveContainerDefault(holder);

        Bukkit.getPluginManager().registerEvents(listener, Labyrinth.getInstance());

        final Object container = nms.newContainerAnvil(holder, title);

        inventory = nms.toBukkitInventory(container);

        inventory.setItem(Slot.INPUT_LEFT, LEFT_ITEM.item);

        if (RIGHT_ITEM != null) {
            inventory.setItem(Slot.INPUT_RIGHT, RIGHT_ITEM.item);
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
     * @throws IllegalArgumentException If the inventory isn't open
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

    public Inventory getInventory() {
        return inventory;
    }

    public String getTitle() {
        return title;
    }

    public AnvilListener getListener() {
        return listener;
    }

    public ItemBuilder getLeft() {
        return LEFT_ITEM;
    }

    public ItemBuilder getRight() {
        return RIGHT_ITEM;
    }

    public boolean isVisible() {
        return visible;
    }

    public Player getHolder() {
        return holder;
    }

    /**
     * Simply holds the listeners for the GUI
     */
    private class AnvilListener implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (e.getInventory().equals(inventory)) {
                e.setCancelled(true);
                final Player clicker = (Player) e.getWhoClicked();
                if (e.getRawSlot() == Slot.OUTPUT) {
                    final ItemStack clicked = inventory.getItem(e.getRawSlot());
                    if (clicked == null || clicked.getType() == Material.AIR) return;
                    if (getLeft() != null && getLeft().click != null) {
                        if (getRight() != null && getRight().click != null) {
                            if (clicked.getType() == getRight().item.getType()) {
                                final ItemMeta meta = clicked.getItemMeta();
                                getRight().click.execute(clicker, meta.getDisplayName(), meta.getDisplayName().split(" "));
                                return;
                            }
                        }
                        if (clicked.getType() == getLeft().item.getType()) {
                            final ItemMeta meta = clicked.getItemMeta();
                            getLeft().click.execute(clicker, meta.getDisplayName(), meta.getDisplayName().split(" "));
                        }
                    } else closeInventory(false);
                }
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            if (e.getInventory().equals(inventory)) {
                if (visible)
                    closeInventory(true);
                e.getInventory().clear();
            }
        }
    }


    /**
     * Class wrapping the magic constants of slot numbers in an anvil GUI
     */
    public static class Slot {

        /**
         * The slot on the far left, where the first input is inserted. An {@link ItemStack} is always inserted
         * here to be renamed
         */
        public static final int INPUT_LEFT = 0;
        /**
         * Not used, but in a real anvil you are able to put the second item you want to combine here
         */
        public static final int INPUT_RIGHT = 1;
        /**
         * The output slot, where an item is put when two items are combined from {@link #INPUT_LEFT} and
         * {@link #INPUT_RIGHT} or {@link #INPUT_LEFT} is renamed
         */
        public static final int OUTPUT = 2;

    }

}
