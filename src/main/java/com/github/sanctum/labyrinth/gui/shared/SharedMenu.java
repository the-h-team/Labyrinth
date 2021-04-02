package com.github.sanctum.labyrinth.gui.shared;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.data.container.DataContainer;
import com.github.sanctum.labyrinth.data.container.DataStream;
import com.github.sanctum.labyrinth.library.HFEncoded;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.task.Schedule;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
	private final HUID huid;
	private static final Map<Integer, SharedMenu> MENU_MAP = new HashMap<>();
	private static final Map<HUID, Listener> LISTENER_MAP = new HashMap<>();
	private static final Map<HUID, Inventory> INVENTORY_MAP = new HashMap<>();
	private static final Map<UUID, Inventory> PLAYER_MAP = new HashMap<>();
	private final LinkedList<Option> MENU_OPTIONS = new LinkedList<>();
	private final Map<ItemStack, SharedProcess> PROCESS_MAP = new HashMap<>();

	protected SharedMenu(Plugin plugin, int id) {
		this.plugin = plugin;
		this.id = id;
		this.huid = HUID.randomID();
		LISTENER_MAP.computeIfAbsent(huid, h -> {
			Bukkit.getPluginManager().registerEvents(this, plugin);
			return this;
		});
		MENU_MAP.put(id, this);
	}

	/**
	 * Check's if the parent file location exists and creates if it doesn't
	 * <p>
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
	public @NotNull HUID getId() {
		return this.huid;
	}

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
	 * @return The configured settings for this menu.
	 */
	public LinkedList<Option> getOptions() {
		return MENU_OPTIONS;
	}

	/**
	 * Remove a configured option from this menu's cache.
	 *
	 * @param option The option to remove.
	 * @return false if the option isn't configured.
	 */
	public final synchronized boolean removeOption(final @NotNull Option option) {
		for (Option o : MENU_OPTIONS) {
			if (o == option) {
				Schedule.sync(() -> MENU_OPTIONS.remove(option)).run();
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a configured option to this menu's cache.
	 *
	 * @param option The option to add.
	 * @return false if the option already exists.
	 */
	public final synchronized boolean addOption(final @NotNull Option option) {
		for (Option o : MENU_OPTIONS) {
			if (!o.name().contains(option.name().split("_")[1])) {
				Schedule.sync(() -> MENU_OPTIONS.add(option)).run();
				return true;
			}
		}
		return false;
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
	 * @param index   The slot the item goes in
	 * @param item    The item to place.
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
	public final @NotNull
	synchronized ItemStack[] getContents() {
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
	public final @NotNull
	synchronized Inventory getInventory() {
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
		LISTENER_MAP.computeIfAbsent(huid, h -> {
			Bukkit.getPluginManager().registerEvents(this, this.plugin);
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
		HandlerList.unregisterAll(LISTENER_MAP.get(this.huid));
		LISTENER_MAP.remove(this.huid);
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
		if (e.getHotbarButton() != -1) {
			if (MENU_OPTIONS.contains(Option.CANCEL_HOTBAR)) {
				e.setCancelled(true);
				return;
			}
		}
		if (e.getClickedInventory() == getInventory()) {
			if (MENU_OPTIONS.contains(Option.CANCEL_UPPER)) {
				e.setCancelled(true);
				return;
			}
		}
		if (e.getClickedInventory() == e.getView().getBottomInventory()) {
			if (MENU_OPTIONS.contains(Option.CANCEL_LOWER)) {
				e.setCancelled(true);
				return;
			}
		}
		if (this.PROCESS_MAP.keySet().stream().anyMatch(it -> it.isSimilar(e.getCurrentItem()))) {
			ItemStack item = this.PROCESS_MAP.keySet().stream().filter(it -> it.isSimilar(e.getCurrentItem())).findFirst().orElse(null);
			this.PROCESS_MAP.get(item).clickEvent(new SharedClick(e));
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
	public static @NotNull
	synchronized Inventory open(Player target) {
		final UUID id = target.getUniqueId();
		Schedule.async(() -> {
		}).cancelAfter(task -> {
			if (!Bukkit.getOfflinePlayer(id).isOnline()) {
				PLAYER_MAP.remove(id);
				task.cancel();
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
	public static @NotNull
	synchronized SharedMenu get(int id) {
		return MENU_MAP.computeIfAbsent(id, i -> new SharedMenu(Labyrinth.getInstance(), i) {
			@Override
			public @NotNull String getName() {
				return "Labyrinth Provided";
			}

			@Override
			public int getSize() {
				return 54;
			}
		});
	}

	public static @NotNull synchronized List<SharedMenu> collect(Plugin plugin) {
		return MENU_MAP.values().stream().filter(m -> m.getPlugin().equals(plugin)).collect(Collectors.toList());
	}

	public enum Option {

		CANCEL_LOWER, CANCEL_UPPER, CANCEL_HOTBAR

	}


}
