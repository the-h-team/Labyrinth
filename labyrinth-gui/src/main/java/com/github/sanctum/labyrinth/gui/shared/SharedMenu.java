package com.github.sanctum.labyrinth.gui.shared;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.task.Schedule;
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
@Deprecated
public abstract class SharedMenu implements Listener {

	protected final Plugin plugin;
	protected final String id;
	protected final HUID huid;
	protected static final Map<String, SharedMenu> MENU_MAP = new HashMap<>();
	protected static final Map<HUID, Listener> LISTENER_MAP = new HashMap<>();
	protected static final Map<HUID, Inventory> INVENTORY_MAP = new HashMap<>();
	protected static final Map<UUID, Inventory> PLAYER_MAP = new HashMap<>();
	protected final LinkedList<Option> menuOptions = new LinkedList<>();
	protected final Map<ItemStack, SharedProcess> processMap = new HashMap<>();

	protected SharedMenu(Plugin plugin, String id) {
		this.plugin = plugin;
		this.id = id;
		this.huid = HUID.randomID();
		LISTENER_MAP.computeIfAbsent(huid, h -> {
			Bukkit.getPluginManager().registerEvents(this, plugin);
			return this;
		});
		MENU_MAP.put(id, this);
	}

	// TODO: how does this involve parent file? Storage of items to meta (just confused by explanation)
	/**
	 * Resolves parent file location and creates it as needed.
	 * <p>
	 * This method isn't necessary as it's handled automatically with {@link SharedBuilder#create(Plugin, String, String, int)}
	 *
	 * @return the same menu w/ parent file location creation
	 */
	public final synchronized SharedMenu inject() {
		PersistentContainer container = LabyrinthProvider.getInstance().getContainer(new NamespacedKey(plugin, "SharedMenus"));
		if (container.get(ItemStack[].class, id) == null) {
			container.attach(id, new ItemStack[getSize()]);
		}
		return this;
	}

	/**
	 * Get the actual saved id of this menu.
	 *
	 * @return the meta-id of this menu instance
	 */
	public @NotNull String getPath() {
		return this.id;
	}

	/**
	 * Get the title of the menu.
	 *
	 * @return the title of the menu
	 */
	public abstract @NotNull String getName();

	/**
	 * Get the unique id of the menu.
	 *
	 * @return the unique id of the menu
	 */
	public @NotNull HUID getId() {
		return this.huid;
	}

	/**
	 * Get the size of the menu.
	 *
	 * @return the size of the menu
	 */
	public abstract int getSize();

	/**
	 * Get the plugin that created the menu.
	 *
	 * @return the plugin that created the menu
	 */
	public final @NotNull Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Get the configured settings for this menu.
	 *
	 * @return the configured settings for this menu
	 */
	public LinkedList<Option> getOptions() {
		return menuOptions;
	}

	/**
	 * Remove a configured option from this menu's cache.
	 *
	 * @param option the option to remove
	 * @return false if the option isn't configured
	 */
	public final synchronized boolean removeOption(final @NotNull Option option) {
		for (Option o : menuOptions) {
			if (o == option) {
				Schedule.sync(() -> menuOptions.remove(option)).run();
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove configured options from this menu's cache.
	 *
	 * @param option the option(s) to remove
	 * @return false if an element that can't be removed is encountered
	 */
	public final synchronized boolean removeOption(final @NotNull Option... option) {
		for (Option o : option) {
			return removeOption(o);
		}
		return false;
	}

	/**
	 * Add a configured option to this menu's cache.
	 *
	 * @param option an option to add
	 * @return false if the option already found
	 */
	public final synchronized boolean addOption(final @NotNull Option option) {
		if (!menuOptions.contains(option)) {
			menuOptions.add(option);
			return true;
		}
		return false;
	}

	/**
	 * Add configured options to this menu's cache.
	 *
	 * @param option the option(s) to add
	 * @return false if an element that can't be added was provided
	 */
	public final synchronized boolean addOption(final @NotNull Option... option) {
		for (Option o : option) {
			return addOption(o);
		}
		return false;
	}

	/**
	 * Update the contents of the inventory
	 *
	 * @param newContents the new items to fill in
	 */
	public final synchronized void setContents(@NotNull ItemStack[] newContents) {
		Schedule.sync(() -> getInventory().setContents(newContents)).run();
	}

	/**
	 * For GUI purposes. Set an item with custom meta within the inventory.
	 *
	 * @param index   the slot the item goes in
	 * @param item    the item to place
	 * @param process the process to run when the item is interacted with
	 */
	public final synchronized void setItem(int index, Supplier<ItemStack> item, SharedProcess process) {
		this.processMap.put(item.get(), process);
		Schedule.sync(() -> getInventory().setItem(index, item.get())).run();
	}

	/**
	 * Get the menu's inventory contents.
	 *
	 * @return the inventory's contents
	 */
	public final @NotNull
	synchronized ItemStack[] getContents() {
		PersistentContainer container = LabyrinthProvider.getInstance().getContainer(new NamespacedKey(plugin, "SharedMenus"));
		ItemStack[] content = new ItemStack[getSize()];
		ItemStack[] contentC = container.get(ItemStack[].class, id);
		if (contentC != null) {
			System.arraycopy(contentC, 0, content, 0, getSize());
		}
		return content;
	}

	/**
	 * Get the menu's inventory.
	 *
	 * @return the menu's inventory
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
	 * Unregister the listeners for this menu.
	 */
	public final synchronized void unregister() {
		HandlerList.unregisterAll(LISTENER_MAP.get(getId()));
	}

	/**
	 * Re-register the listeners for this menu.
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
		PersistentContainer container = LabyrinthProvider.getInstance().getContainer(new NamespacedKey(plugin, "SharedMenus"));
		container.delete(id);
		INVENTORY_MAP.remove(getId());
		HandlerList.unregisterAll(LISTENER_MAP.get(this.huid));
		LISTENER_MAP.remove(this.huid);
		MENU_MAP.remove(id);
	}

	/**
	 * Save the menu's meta applying new contents to save with.
	 *
	 * @param contents the items to save
	 */
	public final synchronized void save(ItemStack[] contents) {
		PersistentContainer container = LabyrinthProvider.getInstance().getContainer(new NamespacedKey(plugin, "SharedMenus"));
		container.attach(id, contents);
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
			if (menuOptions.contains(Option.CANCEL_HOTBAR)) {
				e.setCancelled(true);
				return;
			}
		}
		if (e.getClickedInventory() == getInventory()) {
			if (menuOptions.contains(Option.CANCEL_UPPER)) {
				e.setCancelled(true);
				return;
			}
		}
		if (e.getClickedInventory() == e.getView().getBottomInventory()) {
			if (menuOptions.contains(Option.CANCEL_LOWER)) {
				e.setCancelled(true);
				return;
			}
		}
		if (this.processMap.keySet().stream().anyMatch(it -> it.isSimilar(e.getCurrentItem()))) {
			ItemStack item = this.processMap.keySet().stream().filter(it -> it.isSimilar(e.getCurrentItem())).findFirst().orElse(null);
			this.processMap.get(item).clickEvent(new SharedClick(e));
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
	 * @param target the player to target
	 * @return the target's inventory
	 */
	public static @NotNull
	synchronized Inventory open(Player target) {
		final UUID id = target.getUniqueId();
		return PLAYER_MAP.computeIfAbsent(target.getUniqueId(), uid -> {
			Schedule.async(() -> {
			}).cancelAfter(task -> {
				if (!Bukkit.getOfflinePlayer(id).isOnline()) {
					PLAYER_MAP.remove(id);
					task.cancel();
				}
			}).repeat(0, 600);
			return target.getInventory();
		});
	}

	/**
	 * Look for a match of the provided menu id.
	 *
	 * @param id the menu id to look for
	 * @return true if the provided id belongs to a cached menu instance
	 */
	public static synchronized boolean exists(String id) {
		return MENU_MAP.containsKey(id);
	}

	/**
	 * Get a cached menu by its delimiter. If the specified menu isn't found
	 * a new one with the desired id will be created using labyrinth but will not
	 * persist in saving any data.
	 *
	 * @param id the menu id to look for
	 * @return a shared menu by id; otherwise, a labyrinth provided menu
	 */
	public static @NotNull
	synchronized SharedMenu get(String id) {
		return MENU_MAP.computeIfAbsent(id, i -> new SharedMenu(LabyrinthProvider.getInstance().getPluginInstance(), i) {
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

	public static @NotNull
	synchronized List<SharedMenu> collect(Plugin plugin) {
		return MENU_MAP.values().stream().filter(m -> m.getPlugin().equals(plugin)).collect(Collectors.toList());
	}

	public enum Option {

		/**
		 * Tells the menu that lower inventory clicks need to be cancelled.
		 */
		CANCEL_LOWER,
		/**
		 * Tells the menu that the upper inventory clicks need to be cancelled.
		 */
		CANCEL_UPPER,
		/**
		 * Tells the menu that the hotbar-swap transactions need to be cancelled.
		 */
		CANCEL_HOTBAR

	}


}
