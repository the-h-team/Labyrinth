package com.github.sanctum.labyrinth.gui.menuman;

import com.github.sanctum.labyrinth.formatting.PaginatedList;
import com.github.sanctum.labyrinth.gui.InventoryRows;
import com.github.sanctum.labyrinth.task.Asynchronous;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

/**
 * The primary object used to start a {@link Menu.Paginated} object.
 * Use this to customize everything from click,close events to element specifications.
 */
public final class PaginatedBuilder<T> {

	protected Inventory INVENTORY;
	protected Plugin PLUGIN;
	protected Map<Player, Asynchronous> TASK = new HashMap<>();
	protected boolean LIVE;
	protected int LIMIT = 28;
	protected int INDEX = 1;
	protected int PAGE = 1;
	protected int SIZE = 54;
	protected final UUID ID;
	protected String TITLE;
	protected String FIRST_PAGE_MESSAGE;
	protected String LAST_PAGE_MESSAGE;
	protected List<T> COLLECTION;
	protected ItemStack BORDER_ITEM;
	protected ItemStack FILLER_ITEM;
	protected PaginatedMenuClose<T> MENU_CLOSE;
	protected PaginatedMenuProcess<T> MENU_PROCESS;
	protected final Map<ItemStack, Integer> NAVIGATION_LEFT;
	protected final Map<ItemStack, Integer> NAVIGATION_RIGHT;
	protected final Map<ItemStack, Integer> NAVIGATION_BACK;
	protected final Map<ItemStack, Integer> INITIAL_CONTENTS;
	protected PaginatedListener CONTROLLER;
	protected NamespacedKey NAMESPACE;
	protected final LinkedList<ItemStack> PROCESS_LIST;
	protected final Map<ItemStack, PaginatedMenuClick<T>> ITEM_ACTIONS;

	public PaginatedBuilder(Plugin plugin) {
		this.ITEM_ACTIONS = new HashMap<>();
		this.PROCESS_LIST = new LinkedList<>();
		this.NAVIGATION_LEFT = new HashMap<>();
		this.NAVIGATION_RIGHT = new HashMap<>();
		this.NAVIGATION_BACK = new HashMap<>();
		this.INITIAL_CONTENTS = new HashMap<>();
		this.PLUGIN = plugin;
		this.ID = UUID.randomUUID();
		this.NAMESPACE = new NamespacedKey(plugin, "paginated_utility_manager");
		this.CONTROLLER = new PaginatedListener();
		Bukkit.getPluginManager().registerEvents(CONTROLLER, plugin);
	}

	public PaginatedBuilder(List<T> list) {
		this.ITEM_ACTIONS = new HashMap<>();
		this.PROCESS_LIST = new LinkedList<>();
		this.NAVIGATION_LEFT = new HashMap<>();
		this.NAVIGATION_RIGHT = new HashMap<>();
		this.NAVIGATION_BACK = new HashMap<>();
		this.INITIAL_CONTENTS = new HashMap<>();
		this.COLLECTION = new LinkedList<>(list);
		this.ID = UUID.randomUUID();
	}

	public PaginatedBuilder(Plugin plugin, List<T> list) {
		this.ITEM_ACTIONS = new HashMap<>();
		this.COLLECTION = new LinkedList<>(list);
		this.PROCESS_LIST = new LinkedList<>();
		this.NAVIGATION_LEFT = new HashMap<>();
		this.NAVIGATION_RIGHT = new HashMap<>();
		this.NAVIGATION_BACK = new HashMap<>();
		this.INITIAL_CONTENTS = new HashMap<>();
		this.PLUGIN = plugin;
		this.ID = UUID.randomUUID();
		NAMESPACE = new NamespacedKey(plugin, "paginated_utility_manager");
		CONTROLLER = new PaginatedListener();
		Bukkit.getPluginManager().registerEvents(CONTROLLER, plugin);
	}

	public PaginatedBuilder(Plugin plugin, String title) {
		this.TITLE = title;
		this.PLUGIN = plugin;
		this.ITEM_ACTIONS = new HashMap<>();
		this.PROCESS_LIST = new LinkedList<>();
		this.NAVIGATION_LEFT = new HashMap<>();
		this.NAVIGATION_RIGHT = new HashMap<>();
		this.NAVIGATION_BACK = new HashMap<>();
		this.INITIAL_CONTENTS = new HashMap<>();
		this.ID = UUID.randomUUID();
		NAMESPACE = new NamespacedKey(plugin, "paginated_utility_manager");
		CONTROLLER = new PaginatedListener();
		Bukkit.getPluginManager().registerEvents(CONTROLLER, plugin);
	}

	/**
	 * A crucial step to initializing your menu.
	 *
	 * @param plugin The plugin to register the menu under.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> forPlugin(Plugin plugin) {
		NAMESPACE = new NamespacedKey(plugin, "paginated_utility_manager");
		CONTROLLER = new PaginatedListener();
		Bukkit.getPluginManager().registerEvents(CONTROLLER, plugin);
		return this;
	}

	/**
	 * Sort the given elements by your own comparisons.
	 *
	 * @param comparable The ordering to apply for this listing.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> sort(Comparator<? super T> comparable) {
		this.COLLECTION.sort(comparable);
		return this;
	}

	/**
	 * Set the title to be viewed when players open this menu.
	 *
	 * @param title The title of the GUI.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setTitle(String title) {
		this.TITLE = title.replace("{PAGE}", "" + PAGE);
		return this;
	}

	public PaginatedBuilder<T> isLive() {
		this.LIVE = true;
		return this;
	}

	/**
	 * Store a specified collection to be converted to customized elements
	 *
	 * @param collection The collection of elements to use
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> collect(List<T> collection) {
		this.COLLECTION = new LinkedList<>(collection);
		return this;
	}

	/**
	 * Store a specified collection to be converted to customized elements
	 *
	 * @param collection The collection of elements to use
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> collect(LinkedList<T> collection) {
		this.COLLECTION = new LinkedList<>(collection);
		return this;
	}

	/**
	 * Limit the amount of items to be displayed per page.
	 *
	 * @param amountPer The amount of items per page.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> limit(int amountPer) {
		this.LIMIT = amountPer;
		return this;
	}

	/**
	 * Define how large the inventory will be
	 *
	 * @param size The size of the inventory
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setSize(int size) {
		this.SIZE = size;
		return this;
	}

	/**
	 * Define how large the inventory will be
	 *
	 * @param rows The size of the inventory
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setSize(InventoryRows rows) {
		this.SIZE = rows.slotCount;
		return this;
	}

	/**
	 * Set the message to be displayed when a player attempts to switch to a previous page on the initial page.
	 *
	 * @param context The message to be displayed otherwise empty.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setAlreadyFirst(String context) {
		this.FIRST_PAGE_MESSAGE = context.replace("{PAGE}", "" + PAGE);
		return this;
	}

	/**
	 * Set the message to be displayed when a player attempts to switch to the next page on the last page.
	 *
	 * @param context The message to be displayed otherwise empty.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setAlreadyLast(String context) {
		this.LAST_PAGE_MESSAGE = context.replace("{PAGE}", "" + PAGE);
		return this;
	}

	/**
	 * Set the operation to be ran in the event of this menu being closed.
	 *
	 * @param inventoryClose The inventory close action
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setCloseAction(PaginatedMenuClose<T> inventoryClose) {
		this.MENU_CLOSE = inventoryClose;
		return this;
	}

	/**
	 * Create a {@link PaginatedProcessAction} to customize each item to be displayed within the collection.
	 *
	 * @param inventoryProcess The inventory processing operation.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setupProcess(PaginatedMenuProcess<T> inventoryProcess) {
		this.MENU_PROCESS = inventoryProcess;
		return this;
	}

	/**
	 * Initialize a border for the menu or fill remaining slots with specified materials.
	 *
	 * @return A border building elemement.
	 */
	public PaginatedBorderElement<T> setupBorder() {
		return new PaginatedBorderElement<>(this);
	}

	/**
	 * Initialize any additional elements with defined logic.
	 *
	 * @return An spare element builder.
	 */
	public PaginatedElementAdditions<T> extraElements() {
		return new PaginatedElementAdditions<>(this);
	}

	/**
	 * Automatically format all menu items in accordance and default to a specific page for opening.
	 *
	 * @param desiredPage The desired page to be opened.
	 * @return The same menu builder.
	 */
	protected PaginatedBuilder<T> adjust(int desiredPage) {
		PAGE = desiredPage;
		if (BORDER_ITEM != null) {
			switch (SIZE) {
				case 27:
					int f;
					for (f = 0; f < 10; f++) {
						if (INVENTORY.getItem(f) == null)
							INVENTORY.setItem(f, BORDER_ITEM);
					}
					INVENTORY.setItem(17, BORDER_ITEM);
					for (f = 18; f < 27; f++) {
						if (INVENTORY.getItem(f) == null)
							INVENTORY.setItem(f, BORDER_ITEM);
					}
					break;
				case 36:
					int h;
					for (h = 0; h < 10; h++) {
						if (INVENTORY.getItem(h) == null)
							INVENTORY.setItem(h, BORDER_ITEM);
					}
					INVENTORY.setItem(17, BORDER_ITEM);
					INVENTORY.setItem(18, BORDER_ITEM);
					INVENTORY.setItem(26, BORDER_ITEM);
					for (h = 27; h < 36; h++) {
						if (INVENTORY.getItem(h) == null)
							INVENTORY.setItem(h, BORDER_ITEM);
					}
					break;
				case 45:
					int o;
					for (o = 0; o < 10; o++) {
						if (INVENTORY.getItem(o) == null)
							INVENTORY.setItem(o, BORDER_ITEM);
					}
					INVENTORY.setItem(17, BORDER_ITEM);
					INVENTORY.setItem(18, BORDER_ITEM);
					INVENTORY.setItem(26, BORDER_ITEM);
					INVENTORY.setItem(27, BORDER_ITEM);
					INVENTORY.setItem(35, BORDER_ITEM);
					INVENTORY.setItem(36, BORDER_ITEM);
					for (o = 36; o < 45; o++) {
						if (INVENTORY.getItem(o) == null)
							INVENTORY.setItem(o, BORDER_ITEM);
					}
					break;
				case 54:
					int j;
					for (j = 0; j < 10; j++) {
						if (INVENTORY.getItem(j) == null)
							INVENTORY.setItem(j, BORDER_ITEM);
					}
					INVENTORY.setItem(17, BORDER_ITEM);
					INVENTORY.setItem(18, BORDER_ITEM);
					INVENTORY.setItem(26, BORDER_ITEM);
					INVENTORY.setItem(27, BORDER_ITEM);
					INVENTORY.setItem(35, BORDER_ITEM);
					INVENTORY.setItem(36, BORDER_ITEM);
					for (j = 44; j < 54; j++) {
						if (INVENTORY.getItem(j) == null)
							INVENTORY.setItem(j, BORDER_ITEM);
					}
					break;
			}
		}
		if (COLLECTION != null && !COLLECTION.isEmpty()) {
			PaginatedList<T> list = new PaginatedList<>(this.COLLECTION)
					.limit(LIMIT)
					.decorate((pagination, object, page, max, placement) -> {
						if (object != null) {
							this.INDEX = placement;
							if (MENU_PROCESS != null) {
								if (INDEX <= this.COLLECTION.size()) {
									PaginatedProcessAction<T> element = new PaginatedProcessAction<>(this, object);
									MENU_PROCESS.accept(element);
									CompletableFuture.runAsync(() -> INVENTORY.addItem(element.getItem())).join();

									if (!PROCESS_LIST.contains(element.getItem())) {
										PROCESS_LIST.add(element.getItem());
									}
								}
							}
						} else {
							this.PLUGIN.getLogger().warning("- +1 object failed to load in menu " + this.TITLE);
						}
					});

			list.get(this.PAGE);

		}
		if (FILLER_ITEM != null) {
			for (int l = 0; l < SIZE; l++) {
				if (INVENTORY.getItem(l) == null) {
					INVENTORY.setItem(l, FILLER_ITEM);
				}
			}
		}
		ItemStack left = NAVIGATION_LEFT.keySet().stream().findFirst().orElse(null);
		ItemStack right = NAVIGATION_RIGHT.keySet().stream().findFirst().orElse(null);
		ItemStack back = NAVIGATION_BACK.keySet().stream().findFirst().orElse(null);
		if (left != null) {
			if (!INVENTORY.contains(left)) {
				INVENTORY.setItem(NAVIGATION_LEFT.get(left), left);
				INVENTORY.setItem(NAVIGATION_RIGHT.get(right), right);
				INVENTORY.setItem(NAVIGATION_BACK.get(back), back);
			}
		}
		if (!INITIAL_CONTENTS.isEmpty()) {
			for (Map.Entry<ItemStack, Integer> entry : INITIAL_CONTENTS.entrySet()) {
				if (entry.getValue() == -1) {
					INVENTORY.addItem(entry.getKey());
				} else {
					INVENTORY.setItem(entry.getValue(), entry.getKey());
				}
			}
		}
		return this;
	}

	/**
	 * Automatically format all menu items in accordance for opening.
	 *
	 * @return The same menu builder.
	 */
	protected PaginatedBuilder<T> adjust() {
		if (BORDER_ITEM != null) {
			switch (SIZE) {
				case 27:
					int f;
					for (f = 0; f < 10; f++) {
						if (INVENTORY.getItem(f) == null)
							INVENTORY.setItem(f, BORDER_ITEM);
					}
					INVENTORY.setItem(17, BORDER_ITEM);
					for (f = 18; f < 27; f++) {
						if (INVENTORY.getItem(f) == null)
							INVENTORY.setItem(f, BORDER_ITEM);
					}
					break;
				case 36:
					int h;
					for (h = 0; h < 10; h++) {
						if (INVENTORY.getItem(h) == null)
							INVENTORY.setItem(h, BORDER_ITEM);
					}
					INVENTORY.setItem(17, BORDER_ITEM);
					INVENTORY.setItem(18, BORDER_ITEM);
					INVENTORY.setItem(26, BORDER_ITEM);
					for (h = 27; h < 36; h++) {
						if (INVENTORY.getItem(h) == null)
							INVENTORY.setItem(h, BORDER_ITEM);
					}
					break;
				case 45:
					int o;
					for (o = 0; o < 10; o++) {
						if (INVENTORY.getItem(o) == null)
							INVENTORY.setItem(o, BORDER_ITEM);
					}
					INVENTORY.setItem(17, BORDER_ITEM);
					INVENTORY.setItem(18, BORDER_ITEM);
					INVENTORY.setItem(26, BORDER_ITEM);
					INVENTORY.setItem(27, BORDER_ITEM);
					INVENTORY.setItem(35, BORDER_ITEM);
					INVENTORY.setItem(36, BORDER_ITEM);
					for (o = 36; o < 45; o++) {
						if (INVENTORY.getItem(o) == null)
							INVENTORY.setItem(o, BORDER_ITEM);
					}
					break;
				case 54:
					int j;
					for (j = 0; j < 10; j++) {
						if (INVENTORY.getItem(j) == null)
							INVENTORY.setItem(j, BORDER_ITEM);
					}
					INVENTORY.setItem(17, BORDER_ITEM);
					INVENTORY.setItem(18, BORDER_ITEM);
					INVENTORY.setItem(26, BORDER_ITEM);
					INVENTORY.setItem(27, BORDER_ITEM);
					INVENTORY.setItem(35, BORDER_ITEM);
					INVENTORY.setItem(36, BORDER_ITEM);
					for (j = 44; j < 54; j++) {
						if (INVENTORY.getItem(j) == null)
							INVENTORY.setItem(j, BORDER_ITEM);
					}
					break;
			}
		}
		if (COLLECTION != null && !COLLECTION.isEmpty()) {
			PaginatedList<T> list = new PaginatedList<>(this.COLLECTION)
					.limit(LIMIT)
					.decorate((pagination, object, page, max, placement) -> {
						if (object != null) {
							this.INDEX = placement;
							if (MENU_PROCESS != null) {
								if (INDEX <= this.COLLECTION.size()) {
									PaginatedProcessAction<T> element = new PaginatedProcessAction<>(this, object);
									MENU_PROCESS.accept(element);
									CompletableFuture.runAsync(() -> INVENTORY.addItem(element.getItem())).join();

									if (!PROCESS_LIST.contains(element.getItem())) {
										PROCESS_LIST.add(element.getItem());
									}
								}
							}
						} else {
							this.PLUGIN.getLogger().warning("- +1 object failed to load in menu " + this.TITLE);
						}
					});

			list.get(this.PAGE);

		}
		if (FILLER_ITEM != null) {
			for (int l = 0; l < SIZE; l++) {
				if (INVENTORY.getItem(l) == null) {
					INVENTORY.setItem(l, FILLER_ITEM);
				}
			}
		}
		ItemStack left = NAVIGATION_LEFT.keySet().stream().findFirst().orElse(null);
		ItemStack right = NAVIGATION_RIGHT.keySet().stream().findFirst().orElse(null);
		ItemStack back = NAVIGATION_BACK.keySet().stream().findFirst().orElse(null);
		if (left != null) {
			if (!INVENTORY.contains(left)) {
				INVENTORY.setItem(NAVIGATION_LEFT.get(left), left);
				INVENTORY.setItem(NAVIGATION_RIGHT.get(right), right);
				INVENTORY.setItem(NAVIGATION_BACK.get(back), back);
			}
		}
		if (!INITIAL_CONTENTS.isEmpty()) {
			for (Map.Entry<ItemStack, Integer> entry : INITIAL_CONTENTS.entrySet()) {
				if (entry.getValue() == -1) {
					INVENTORY.addItem(entry.getKey());
				} else {
					INVENTORY.setItem(entry.getValue(), entry.getKey());
				}
			}
		}
		return this;
	}

	/**
	 * Customize a page-back navigation key for the menu.
	 *
	 * @param item  The item to be used to page-back with.
	 * @param slot  The slot the item will reside permanently.
	 * @param click The inventory click action for the item.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setNavigationLeft(Supplier<ItemStack> item, int slot, PaginatedMenuClick<T> click) {
		this.NAVIGATION_LEFT.putIfAbsent(item.get(), slot);
		this.ITEM_ACTIONS.putIfAbsent(item.get(), click);
		return this;
	}

	/**
	 * Customize a page-forward navigation key for the menu.
	 *
	 * @param item  The item to be used to page-forward with.
	 * @param slot  The slot the item will reside permanently.
	 * @param click The inventory click action for the item.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setNavigationRight(Supplier<ItemStack> item, int slot, PaginatedMenuClick<T> click) {
		this.NAVIGATION_RIGHT.putIfAbsent(item.get(), slot);
		this.ITEM_ACTIONS.putIfAbsent(item.get(), click);
		return this;
	}

	/**
	 * Customize a page-exit navigation key for the menu.
	 *
	 * @param item  The item to be used to page-exit with.
	 * @param slot  The slot the item will reside permanently.
	 * @param click The inventory click action for the item.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setNavigationBack(Supplier<ItemStack> item, int slot, PaginatedMenuClick<T> click) {
		this.NAVIGATION_BACK.putIfAbsent(item.get(), slot);
		this.ITEM_ACTIONS.putIfAbsent(item.get(), click);
		return this;
	}

	/**
	 * Customize a page-back navigation key for the menu.
	 *
	 * @param item  The item to be used to page-back with.
	 * @param slot  The slot the item will reside permanently.
	 * @param click The inventory click action for the item.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setNavigationLeft(ItemStack item, int slot, PaginatedMenuClick<T> click) {
		this.NAVIGATION_LEFT.putIfAbsent(item, slot);
		this.ITEM_ACTIONS.putIfAbsent(item, click);
		return this;
	}

	/**
	 * Customize a page-forward navigation key for the menu.
	 *
	 * @param item  The item to be used to page-forward with.
	 * @param slot  The slot the item will reside permanently.
	 * @param click The inventory click action for the item.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setNavigationRight(ItemStack item, int slot, PaginatedMenuClick<T> click) {
		this.NAVIGATION_RIGHT.putIfAbsent(item, slot);
		this.ITEM_ACTIONS.putIfAbsent(item, click);
		return this;
	}

	/**
	 * Customize a page-exit navigation key for the menu.
	 *
	 * @param item  The item to be used to page-exit with.
	 * @param slot  The slot the item will reside permanently.
	 * @param click The inventory click action for the item.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setNavigationBack(ItemStack item, int slot, PaginatedMenuClick<T> click) {
		this.NAVIGATION_BACK.putIfAbsent(item, slot);
		this.ITEM_ACTIONS.putIfAbsent(item, click);
		return this;
	}

	/**
	 * Complete the menu building process and convert the builder into a Paginated Menu ready to be used.
	 *
	 * @return A fully built paginated menu.
	 */
	public Menu.Paginated<T> build() {
		return new Menu.Paginated<>(this);
	}

	/**
	 * Get the unique ID of this menu.
	 *
	 * @return A UUID.
	 */
	public UUID getId() {
		return ID;
	}

	/**
	 * Get the inventory for this menu.
	 *
	 * @return An inventory object.
	 */
	public Inventory getInventory() {
		return INVENTORY;
	}

	/**
	 * Get the listener registered with this menu.
	 *
	 * @return A bukkit listener.
	 */
	public PaginatedListener getController() {
		return CONTROLLER;
	}

	/**
	 * Get the amount of items specified per page.
	 *
	 * @return Amount of items per page.
	 */
	public int getAmountPerPage() {
		return LIMIT;
	}

	/**
	 * Get the max amount of pages after collection conversions.
	 *
	 * @return Max amount of GUI pages.
	 */
	public int getMaxPages() {
		return (COLLECTION.size() / (LIMIT - 1)) < 0 ? COLLECTION.size() / LIMIT : COLLECTION.size() / LIMIT - 1;
	}

	/**
	 * Get the default namespace used when one isnt provided.
	 *
	 * @return A namespaced key.
	 */
	public NamespacedKey getKey() {
		return NAMESPACE;
	}

	/**
	 * Get the collection involed with the menu.
	 *
	 * @return The string collection used in the GUI.
	 */
	public List<T> getCollection() {
		return COLLECTION;
	}

	/**
	 * Get the plugin registered with the menu.
	 *
	 * @return The plugin connected with the menu.
	 */
	public Plugin getPlugin() {
		return PLUGIN;
	}

	/**
	 * Internal bukkit event logic, everything built within the paginated builder will be applied here automatically.
	 */
	public class PaginatedListener implements Listener {

		private boolean metaMatches(ItemStack one, ItemStack two) {
			boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
			Material type;
			if (isNew) {
				type = Material.valueOf("PLAYER_HEAD");
			} else {
				type = Material.valueOf("SKULL_ITEM");
			}
			if (one.getType() == type && two.getType() == type) {
				if (one.hasItemMeta() && two.hasItemMeta()) {
					if (one.getItemMeta() instanceof SkullMeta && two.getItemMeta() instanceof SkullMeta) {
						SkullMeta Meta1 = (SkullMeta) one.getItemMeta();
						SkullMeta Meta2 = (SkullMeta) one.getItemMeta();
						if (Meta1.hasOwner() && Meta2.hasOwner()) {
							return Meta1.getOwningPlayer().getUniqueId().equals(Meta2.getOwningPlayer().getUniqueId());
						}
					}
					return false;
				}
				return false;
			}
			return false;
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClose(InventoryCloseEvent e) {
			if (!(e.getPlayer() instanceof Player))
				return;
			if (e.getView().getTopInventory().getSize() < SIZE)
				return;
			if (getInventory() == e.getInventory()) {

				Player p = (Player) e.getPlayer();

				if (TASK.containsKey(p)) {
					TASK.get(p).cancelTask();
					TASK.remove(p);
				}
				if (MENU_CLOSE != null) {
					MENU_CLOSE.closeEvent(new PaginatedCloseAction<>(PaginatedBuilder.this, p, e.getView()));
				}
				PAGE = 0;
				INDEX = 0;
				p.updateInventory();
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onMove(InventoryMoveItemEvent e) {
			if (e.getSource() == getInventory()) {
				e.setCancelled(true);
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onDrag(InventoryDragEvent e) {
			if (!(e.getWhoClicked() instanceof Player))
				return;
			if (e.getView().getTopInventory().getSize() < SIZE)
				return;
			if (getInventory() != e.getInventory())
				return;
			if (e.getInventory() == getInventory()) {
				e.setResult(Event.Result.DENY);
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClick(InventoryClickEvent e) {
			if (!(e.getWhoClicked() instanceof Player))
				return;
			if (e.getView().getTopInventory().getSize() < SIZE)
				return;

			if (e.getHotbarButton() != -1) {
				e.setCancelled(true);
				return;
			}

			if (getInventory() != e.getInventory())
				return;

			if (e.getClickedInventory() == e.getInventory()) {
				Player p = (Player) e.getWhoClicked();

				switch (e.getAction()) {
					case HOTBAR_MOVE_AND_READD:
					case HOTBAR_SWAP:
					case MOVE_TO_OTHER_INVENTORY:
						e.setResult(Event.Result.DENY);
						break;
				}

				e.setCancelled(true);

				if (e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();
					SyncMenuClickItemEvent<T> event = new SyncMenuClickItemEvent<>(PaginatedBuilder.this, p, e.getView(), item);
					Bukkit.getPluginManager().callEvent(event);
					if (event.isCancelled()) {
						e.setCancelled(false);
						return;
					}
					if (PROCESS_LIST.stream().anyMatch(i -> i.isSimilar(item) || metaMatches(i, item))) {
						ITEM_ACTIONS.entrySet().stream().filter(en -> en.getKey().isSimilar(item) || metaMatches(en.getKey(), item)).map(Map.Entry::getValue).findFirst().get().clickEvent(new PaginatedClickAction<>(PaginatedBuilder.this, p, e.getView(), item, e.isLeftClick(), e.isRightClick(), e.isShiftClick(), e.getClick() == ClickType.MIDDLE));
					}
					if (NAVIGATION_BACK.keySet().stream().anyMatch(i -> i.isSimilar(item))) {
						ITEM_ACTIONS.get(item).clickEvent(new PaginatedClickAction<>(PaginatedBuilder.this, p, e.getView(), item, e.isLeftClick(), e.isRightClick(), e.isShiftClick(), e.getClick() == ClickType.MIDDLE));
					}
					if (NAVIGATION_LEFT.keySet().stream().anyMatch(i -> i.isSimilar(item))) {
						if (PAGE == 1) {
							p.sendMessage(FIRST_PAGE_MESSAGE);
						} else {
							SyncMenuSwitchPageEvent<T> event1 = new SyncMenuSwitchPageEvent<>(PaginatedBuilder.this, p, e.getView(), item, PAGE);
							Bukkit.getPluginManager().callEvent(event1);
							if (!event1.isCancelled()) {
								PAGE -= 1;
							}
							ITEM_ACTIONS.get(item).clickEvent(new PaginatedClickAction<>(PaginatedBuilder.this, p, e.getView(), item, e.isLeftClick(), e.isRightClick(), e.isShiftClick(), e.getClick() == ClickType.MIDDLE));
						}
					}
					if (NAVIGATION_RIGHT.keySet().stream().anyMatch(i -> i.isSimilar(item))) {
						if (!((INDEX + 1) >= COLLECTION.size())) {
							SyncMenuSwitchPageEvent<T> event1 = new SyncMenuSwitchPageEvent<>(PaginatedBuilder.this, p, e.getView(), item, PAGE);
							Bukkit.getPluginManager().callEvent(event1);
							if (!event1.isCancelled()) {
								PAGE += 1;
								ITEM_ACTIONS.get(item).clickEvent(new PaginatedClickAction<>(PaginatedBuilder.this, p, e.getView(), item, e.isLeftClick(), e.isRightClick(), e.isShiftClick(), e.getClick() == ClickType.MIDDLE));
							}
						} else {
							p.sendMessage(LAST_PAGE_MESSAGE);
						}
					}
				}
			}

		}

	}


}
