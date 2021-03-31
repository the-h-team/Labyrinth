package com.github.sanctum.labyrinth.gui.shared;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.data.container.DataContainer;
import com.github.sanctum.labyrinth.data.container.DataStream;
import com.github.sanctum.labyrinth.library.HFEncoded;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.task.Schedule;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * An object that encapsulates a size and title for a share-able player inventory.
 */
public abstract class SharedMenu implements Listener {

	private final Plugin plugin;
	private final int id;
	private static final Map<Integer, SharedMenu> MENU_MAP = new HashMap<>();
	private static final Map<Plugin, Listener> LISTENER_MAP = new HashMap<>();
	private static final Map<HUID, Inventory> INVENTORY_MAP = new HashMap<>();
	private static final Map<UUID, Inventory> PLAYER_MAP = new HashMap<>();
	private final Map<ItemStack, SharedProcess> PROCESS_MAP = new HashMap<>();

	protected SharedMenu(Plugin plugin, int id) {
		this.plugin = plugin;
		this.id = id;
		LISTENER_MAP.computeIfAbsent(plugin, pl -> {
			Bukkit.getPluginManager().registerEvents(this, pl);
			return this;
		});
		MENU_MAP.put(id, this);
	}

	/**
	 * Check's if the parent file location exists and creates if it doesn't
	 *
	 * This method isn't necessary as its handled automatically with {@link SharedBuilder#create(Plugin, int, String, int)}
	 *
	 * @return The same menu w/ parent file location creation.
	 */
	public final synchronized SharedMenu inject() {
		try {
			DataStream meta = DataContainer.loadInstance(DataContainer.getHuid(plugin.getName() + ":" + id), true);
			if (meta == null) {
				DataContainer container = new DataContainer(plugin.getName() + ":" + id);
				container.setValue(new ItemStack[getSize()]);
				container.storeTemp();
				container.saveMeta();
			}
		} catch (NullPointerException ignored) {
		}
		return this;
	}

	/**
	 * Get the name of the menu.
	 *
	 * @return The menu's title.
	 */
	public abstract @NotNull String getName();

	/**
	 * Get the unique id of the menu.
	 *
	 * @return The menu's id.
	 */
	public abstract @NotNull HUID getId();

	/**
	 * Get the size of the menu.
	 *
	 * @return The menu's size.
	 */
	public abstract int getSize();

	/**
	 * Get the plugin that created the menu.
	 *
	 * @return The menu's creator.
	 */
	public final @NotNull Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Update the contents of the inventory
	 *
	 * @param newContents The new item's to fill in.
	 */
	public final synchronized void setContents(@NotNull ItemStack[] newContents) {
		Schedule.sync(() -> getInventory().setContents(newContents)).run();
	}

	/**
	 * For GUI purposes. Set an item with custom meta within the inventory.
	 *
	 * @param index The slot the item goes in
	 * @param item The item to place.
	 * @param process The process to run when the item is interacted with.
	 */
	public final synchronized void setItem(int index, Supplier<ItemStack> item, SharedProcess process) {
		this.PROCESS_MAP.put(item.get(), process);
		Schedule.sync(() -> getInventory().setItem(index, item.get())).run();
	}

	/**
	 * Get the menu's inventory contents.
	 *
	 * @return The inventory's contents
	 */
	public final @NotNull synchronized ItemStack[] getContents() {
		ItemStack[] content = new ItemStack[getSize()];
		try {
			DataStream meta = DataContainer.loadInstance(DataContainer.getHuid(plugin.getName() + ":" + id), true);
			if (meta != null) {
				try {
					ItemStack[] contentC = (ItemStack[]) new HFEncoded(meta.value()).deserialized();
					System.arraycopy(contentC, 0, content, 0, getSize());
				} catch (IOException | ClassNotFoundException ignored) {
				}
			}
		} catch (NullPointerException e) {
			DataContainer container = new DataContainer(plugin.getName() + ":" + id);
			container.setValue(content);
			container.storeTemp();
			container.saveMeta();
		}

		return content;
	}

	/**
	 * Get the menu's inventory.
	 *
	 * @return The menu's inventory.
	 */
	public final @NotNull synchronized Inventory getInventory() {
		return INVENTORY_MAP.computeIfAbsent(getId(), menu -> {
			Inventory inventory = Bukkit.createInventory(null, getSize(), getName());
			inventory.setContents(getContents());
			return inventory;
		});
	}

	/**
	 * Unregister the listener's for this menu.
	 */
	public final synchronized void unregister() {
		HandlerList.unregisterAll(LISTENER_MAP.get(plugin));
	}

	/**
	 * Re-register the listener's for this menu.
	 */
	public final synchronized void register() {
		LISTENER_MAP.computeIfAbsent(plugin, pl -> {
			Bukkit.getPluginManager().registerEvents(this, pl);
			return this;
		});
	}

	/**
	 * Remove this menu from cache entirely including its meta.
	 */
	public final synchronized void remove() {
		try {
			DataContainer.deleteInstance(DataContainer.getHuid(plugin.getName() + ":" + id));
		} catch (NullPointerException ignored) {
		}
		INVENTORY_MAP.remove(getId());
		HandlerList.unregisterAll(LISTENER_MAP.get(plugin));
		LISTENER_MAP.remove(plugin);
		MENU_MAP.remove(id);
	}

	/**
	 * Save the menu's meta applying new contents to save with.
	 *
	 * @param contents The item's to save.
	 */
	public final synchronized void save(ItemStack[] contents) {
		try {
			DataContainer.deleteInstance(DataContainer.getHuid(plugin.getName() + ":" + id));
			DataContainer container = new DataContainer(plugin.getName() + ":" + id);
			container.setValue(contents);
			container.storeTemp();
			container.saveMeta();
		} catch (NullPointerException e) {
			DataContainer container = new DataContainer(plugin.getName() + ":" + id);
			container.setValue(contents);
			container.storeTemp();
			container.saveMeta();
		}
	}

	@EventHandler
	public final void bukkitInventoryClick(InventoryClickEvent e) {
		final Inventory clickedInventory = e.getInventory();
		if (clickedInventory != getInventory()) {
			return;
		}
		if (e.getInventory().getSize() < getSize()) {
			return;
		}
		if (this.PROCESS_MAP.get(e.getCurrentItem()) != null) {
			this.PROCESS_MAP.get(e.getCurrentItem()).clickEvent(new SharedClick(e));
		}
		Schedule.sync(() -> save(clickedInventory.getContents())).run();
	}

	@EventHandler
	public final void bukkitInventoryClose(InventoryCloseEvent e) {
		Inventory inventory = e.getInventory();
		if (inventory != getInventory()) {
			return;
		}
		if (inventory.getSize() < getSize()) {
			return;
		}
		save(inventory.getContents());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SharedMenu)) return false;
		SharedMenu menu = (SharedMenu) o;
		return getId() == menu.getId() &&
				getPlugin().equals(menu.getPlugin());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPlugin(), getId());
	}

	/**
	 * Open an online player's inventory to interact with.
	 *
	 * @param target The player to target.
	 * @return The target's inventory.
	 */
	public static @NotNull synchronized Inventory open(Player target) {
		final UUID id = target.getUniqueId();
		Schedule.async(() -> {
		}).cancelAfter(task -> {
			if (!Bukkit.getOfflinePlayer(id).isOnline()) {
				PLAYER_MAP.remove(id);
			}
		}).repeat(0, 600);
		return PLAYER_MAP.computeIfAbsent(target.getUniqueId(), uid -> target.getInventory());
	}

	/**
	 * Get a cached menu by its delimiter. If the specified menu isn't found
	 * a new one with the desired id will be created using labyrinth but will not
	 * persist in saving any data.
	 *
	 * @param id The menu id to look for.
	 * @return A shared menu by id otherwise a labyrinth provided menu.
	 */
	public static @NotNull synchronized SharedMenu get(int id) {
		return MENU_MAP.computeIfAbsent(id, i -> new SharedMenu(Labyrinth.getInstance(), i) {
			private final HUID id;
			{
				id = HUID.randomID();
			}

			@Override
			public @NotNull String getName() {
				return "Labyrinth Provided";
			}

			@Override
			public @NotNull HUID getId() {
				return id;
			}

			@Override
			public int getSize() {
				return 54;
			}
		});
	}

}
