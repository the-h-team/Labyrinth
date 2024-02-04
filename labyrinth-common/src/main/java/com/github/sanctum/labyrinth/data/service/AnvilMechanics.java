package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.panther.annotation.Note;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Wraps versions to be able to easily use different NMS server versions
 *
 * @author Wesley Smith
 * @since 1.0
 */
public interface AnvilMechanics extends Service {

	/**
	 * @return The current anvil mechanics for this version.
	 */
	static @Note @NotNull AnvilMechanics getInstance() {
		return Objects.requireNonNull(Bukkit.getServicesManager().load(AnvilMechanics.class));
	}

	/**
	 * Gets the next available NMS container id for the player
	 *
	 * @param player    The player to getMechanics the next container id of
	 * @param container The container that a new id is being generated for
	 * @return The next available NMS container id
	 */
	@Deprecated()
	default int getNextContainerId(Player player, Object container) {
		return -1;
	}

	/**
	 * Closes the current inventory for the player
	 *
	 * @param player The player that needs their current inventory closed
	 */
	@Deprecated
	default void handleInventoryCloseEvent(Player player) {}

	/**
	 * Sends PacketPlayOutOpenWindow to the player with the container id and window title
	 *
	 * @param player         The player to send the packet to
	 * @param containerId    The container id to open
	 * @param inventoryTitle The title of the inventory to be opened (only works in Minecraft 1.14 and above)
	 */
	@Deprecated
	default void sendPacketOpenWindow(Player player, int containerId, String inventoryTitle) {}

	/**
	 * Sends PacketPlayOutCloseWindow to the player with the container id
	 *
	 * @param player      The player to send the packet to
	 * @param containerId The container id to close
	 */
	@Deprecated
	default void sendPacketCloseWindow(Player player, int containerId) {}

	/**
	 * Sets the NMS player's active container to the default one
	 *
	 * @param player The player to set the active container of
	 */
	@Deprecated
	default void setActiveContainerDefault(Player player) {}

	/**
	 * Sets the NMS player's active container to the one supplied
	 *
	 * @param player    The player to set the active container of
	 * @param container The container to set as active
	 */
	@Deprecated
	default void setActiveContainer(Player player, Object container) {}

	/**
	 * Sets the supplied windowId of the supplied Container
	 *
	 * @param container   The container to set the windowId of
	 * @param containerId The new windowId
	 */
	@Deprecated
	default void setActiveContainerId(Object container, int containerId) {}

	/**
	 * Adds a slot listener to the supplied container for the player
	 *
	 * @param container The container to add the slot listener to
	 * @param player    The player to have as a listener
	 */
	@Deprecated
	default void addActiveContainerSlotListener(Object container, Player player){}

	/**
	 * Gets the {@link Inventory} wrapper of the supplied NMS container
	 *
	 * @param container The NMS container to getMechanics the {@link Inventory} of
	 * @return The inventory of the NMS container
	 */
	@Deprecated
	default Inventory toBukkitInventory(Object container) {
		return null;
	}

	/**
	 * Creates a new ContainerAnvil
	 *
	 * @param player The player to getMechanics the container of
	 * @param title  The title of the anvil inventory
	 * @return The Container instance
	 */
	@Deprecated
	default Object newContainerAnvil(Player player, String title) {
		return null;
	}

	/**
	 * Creates a new ContainerAnvil
	 *
	 * @param player The player to get the container of
	 * @param title  The title of the anvil inventory
	 * @return The Container instance
	 */
	Container newContainer(@NotNull Player player, @NotNull String title, boolean override);

	/**
	 * Get the players current active container.
	 *
	 * @param player the player to use.
	 * @return the specified player's container or null if none active.
	 */
	Container getContainer(@NotNull Player player);

	/**
	 * Checks if the current Minecraft version actually supports custom titles
	 *
	 * @return The current supported state
	 */
	default boolean isCustomAllowed() {
		return true;
	}

	interface Container {

		/**
		 * @return the item context within the itemstack on the left-hand side of the anvil container.
		 */
		String getLeftInput();

		/**
		 * Change to input text of the itemstack to the left-hand side of the anvil container.
		 *
		 * @param text the text to use
		 */
		void setLeftInput(String text);

		Inventory getBukkitInventory();

		void open(@NotNull Player player);

		void close(@NotNull Player player);

		void reset(@NotNull Player player);

	}

}