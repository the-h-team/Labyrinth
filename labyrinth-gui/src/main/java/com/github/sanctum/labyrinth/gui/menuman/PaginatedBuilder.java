package com.github.sanctum.labyrinth.gui.menuman;

import com.github.sanctum.labyrinth.formatting.PaginatedList;
import com.github.sanctum.labyrinth.gui.InventoryRows;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.task.Asynchronous;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
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
import org.bukkit.plugin.Plugin;

/**
 * The primary object used to start a {@link Menu.Paginated} object.
 * Use this to customize everything from click,close events to element specifications.
 */
public final class PaginatedBuilder<T> {

	final Map<Player, Asynchronous> task = new HashMap<>();
	final Map<ItemStack, Integer> navigationLeft;
	final Map<ItemStack, Integer> navigationRight;
	final Map<ItemStack, Integer> navigationBack;
	final Map<ItemStack, Integer> initialContents;
	final LinkedList<ItemStack> processList;
	final Map<ItemStack, PaginatedMenuClick<T>> itemActions;
	final UUID id;
	Inventory inventory;
	PaginatedListener controller; // TODO: establish finality
	NamespacedKey namespace;
	boolean live;
	private int limit = 28;
	int index = 1;
	int page = 1;
	int size = 54;
	List<T> collection;
	ItemStack borderItem;
	ItemStack fillerItem;
	private Plugin plugin;
	private String title;
	private String firstPageMessage;
	private String lastPageMessage;
	private PaginatedMenuClose<T> menuClose;
	private PaginatedMenuProcess<T> menuProcess;

	public PaginatedBuilder(Plugin plugin) {
		this.itemActions = new HashMap<>();
		this.processList = new LinkedList<>();
		this.navigationLeft = new HashMap<>();
		this.navigationRight = new HashMap<>();
		this.navigationBack = new HashMap<>();
		this.initialContents = new HashMap<>();
		this.plugin = plugin;
		this.id = UUID.randomUUID();
		this.namespace = new NamespacedKey(plugin, "paginated_utility_manager");
		this.controller = new PaginatedListener();
		Bukkit.getPluginManager().registerEvents(controller, plugin);
	}

	public PaginatedBuilder(List<T> list) {
		this.itemActions = new HashMap<>();
		this.processList = new LinkedList<>();
		this.navigationLeft = new HashMap<>();
		this.navigationRight = new HashMap<>();
		this.navigationBack = new HashMap<>();
		this.initialContents = new HashMap<>();
		this.collection = new LinkedList<>(list);
		this.id = UUID.randomUUID();
	}

	public PaginatedBuilder(Plugin plugin, List<T> list) {
		this.itemActions = new HashMap<>();
		this.collection = new LinkedList<>(list);
		this.processList = new LinkedList<>();
		this.navigationLeft = new HashMap<>();
		this.navigationRight = new HashMap<>();
		this.navigationBack = new HashMap<>();
		this.initialContents = new HashMap<>();
		this.plugin = plugin;
		this.id = UUID.randomUUID();
		namespace = new NamespacedKey(plugin, "paginated_utility_manager");
		controller = new PaginatedListener();
		Bukkit.getPluginManager().registerEvents(controller, plugin);
	}

	public PaginatedBuilder(Plugin plugin, String title) {
		this.title = title;
		this.plugin = plugin;
		this.itemActions = new HashMap<>();
		this.processList = new LinkedList<>();
		this.navigationLeft = new HashMap<>();
		this.navigationRight = new HashMap<>();
		this.navigationBack = new HashMap<>();
		this.initialContents = new HashMap<>();
		this.id = UUID.randomUUID();
		namespace = new NamespacedKey(plugin, "paginated_utility_manager");
		controller = new PaginatedListener();
		Bukkit.getPluginManager().registerEvents(controller, plugin);
	}

	/**
	 * A crucial step to initializing your menu.
	 *
	 * @param plugin The plugin to register the menu under.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> forPlugin(Plugin plugin) {
		namespace = new NamespacedKey(plugin, "paginated_utility_manager");
		this.plugin = plugin;
		controller = new PaginatedListener();
		Bukkit.getPluginManager().registerEvents(controller, plugin);
		return this;
	}

	/**
	 * Sort the given elements by your own comparisons.
	 *
	 * @param comparable The ordering to apply for this listing.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> sort(Comparator<? super T> comparable) {
		this.collection.sort(comparable);
		return this;
	}

	/**
	 * Set the title to be viewed when players open this menu.
	 *
	 * @param title The title of the GUI.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setTitle(String title) {
		this.title = title.replace("{PAGE}", "" + page);
		return this;
	}

	public PaginatedBuilder<T> isLive() {
		this.live = true;
		return this;
	}

	/**
	 * Store a specified collection to be converted to customized elements
	 *
	 * @param collection The collection of elements to use
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> collect(List<T> collection) {
		this.collection = new LinkedList<>(collection);
		return this;
	}

	/**
	 * Store a specified collection to be converted to customized elements
	 *
	 * @param collection The collection of elements to use
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> collect(LinkedList<T> collection) {
		this.collection = new LinkedList<>(collection);
		return this;
	}

	/**
	 * Limit the amount of items to be displayed per page.
	 *
	 * @param amountPer The amount of items per page.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> limit(int amountPer) {
		this.limit = amountPer;
		return this;
	}

	/**
	 * Define how large the inventory will be
	 *
	 * @param size The size of the inventory
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setSize(int size) {
		this.size = size;
		return this;
	}

	/**
	 * Define how large the inventory will be
	 *
	 * @param rows The size of the inventory
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setSize(InventoryRows rows) {
		this.size = rows.slotCount;
		return this;
	}

	/**
	 * Set the message to be displayed when a player attempts to switch to a previous page on the initial page.
	 *
	 * @param context The message to be displayed otherwise empty.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setAlreadyFirst(String context) {
		this.firstPageMessage = context.replace("{PAGE}", "" + page);
		return this;
	}

	/**
	 * Set the message to be displayed when a player attempts to switch to the next page on the last page.
	 *
	 * @param context The message to be displayed otherwise empty.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setAlreadyLast(String context) {
		this.lastPageMessage = context.replace("{PAGE}", "" + page);
		return this;
	}

	/**
	 * Set the operation to be ran in the event of this menu being closed.
	 *
	 * @param inventoryClose The inventory close action
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setCloseAction(PaginatedMenuClose<T> inventoryClose) {
		this.menuClose = inventoryClose;
		return this;
	}

	/**
	 * Create a {@link PaginatedProcessAction} to customize each item to be displayed within the collection.
	 *
	 * @param inventoryProcess The inventory processing operation.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder<T> setupProcess(PaginatedMenuProcess<T> inventoryProcess) {
		this.menuProcess = inventoryProcess;
		return this;
	}

	/**
	 * Initialize a border for the menu or fill remaining slots with specified materials.
	 *
	 * @return A border building element.
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
		page = desiredPage;
		if (borderItem != null) {
			switch (size) {
				case 27:
					int f;
					for (f = 0; f < 10; f++) {
						if (inventory.getItem(f) == null)
							inventory.setItem(f, borderItem);
					}
					inventory.setItem(17, borderItem);
					for (f = 18; f < 27; f++) {
						if (inventory.getItem(f) == null)
							inventory.setItem(f, borderItem);
					}
					break;
				case 36:
					int h;
					for (h = 0; h < 10; h++) {
						if (inventory.getItem(h) == null)
							inventory.setItem(h, borderItem);
					}
					inventory.setItem(17, borderItem);
					inventory.setItem(18, borderItem);
					inventory.setItem(26, borderItem);
					for (h = 27; h < 36; h++) {
						if (inventory.getItem(h) == null)
							inventory.setItem(h, borderItem);
					}
					break;
				case 45:
					int o;
					for (o = 0; o < 10; o++) {
						if (inventory.getItem(o) == null)
							inventory.setItem(o, borderItem);
					}
					inventory.setItem(17, borderItem);
					inventory.setItem(18, borderItem);
					inventory.setItem(26, borderItem);
					inventory.setItem(27, borderItem);
					inventory.setItem(35, borderItem);
					inventory.setItem(36, borderItem);
					for (o = 36; o < 45; o++) {
						if (inventory.getItem(o) == null)
							inventory.setItem(o, borderItem);
					}
					break;
				case 54:
					int j;
					for (j = 0; j < 10; j++) {
						if (inventory.getItem(j) == null)
							inventory.setItem(j, borderItem);
					}
					inventory.setItem(17, borderItem);
					inventory.setItem(18, borderItem);
					inventory.setItem(26, borderItem);
					inventory.setItem(27, borderItem);
					inventory.setItem(35, borderItem);
					inventory.setItem(36, borderItem);
					for (j = 44; j < 54; j++) {
						if (inventory.getItem(j) == null)
							inventory.setItem(j, borderItem);
					}
					break;
			}
		}
		if (collection != null && !collection.isEmpty()) {
			PaginatedList<T> list = new PaginatedList<>(this.collection)
					.limit(limit)
					.decorate((pagination, object, page, max, placement) -> {
						if (object != null) {
							this.index = placement;
							if (menuProcess != null) {
								if (index <= this.collection.size()) {
									PaginatedProcessAction<T> element = new PaginatedProcessAction<>(this, object);
									menuProcess.accept(element);
									CompletableFuture.runAsync(() -> inventory.addItem(element.getItem())).join();

									if (!processList.contains(element.getItem())) {
										processList.add(element.getItem());
									}
								}
							}
						} else {
							this.plugin.getLogger().warning("- +1 object failed to load in menu " + this.title);
						}
					});

			list.get(this.page);

		}
		if (fillerItem != null) {
			for (int l = 0; l < size; l++) {
				if (inventory.getItem(l) == null) {
					inventory.setItem(l, fillerItem);
				}
			}
		}
		ItemStack left = navigationLeft.keySet().stream().findFirst().orElse(null);
		ItemStack right = navigationRight.keySet().stream().findFirst().orElse(null);
		ItemStack back = navigationBack.keySet().stream().findFirst().orElse(null);
		if (left != null) {
			if (!inventory.contains(left)) {
				inventory.setItem(navigationLeft.get(left), left);
				inventory.setItem(navigationRight.get(right), right);
				inventory.setItem(navigationBack.get(back), back);
			}
		}
		if (!initialContents.isEmpty()) {
			for (Map.Entry<ItemStack, Integer> entry : initialContents.entrySet()) {
				if (entry.getValue() == -1) {
					inventory.addItem(entry.getKey());
				} else {
					inventory.setItem(entry.getValue(), entry.getKey());
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
		if (borderItem != null) {
			switch (size) {
				case 27:
					int f;
					for (f = 0; f < 10; f++) {
						if (inventory.getItem(f) == null)
							inventory.setItem(f, borderItem);
					}
					inventory.setItem(17, borderItem);
					for (f = 18; f < 27; f++) {
						if (inventory.getItem(f) == null)
							inventory.setItem(f, borderItem);
					}
					break;
				case 36:
					int h;
					for (h = 0; h < 10; h++) {
						if (inventory.getItem(h) == null)
							inventory.setItem(h, borderItem);
					}
					inventory.setItem(17, borderItem);
					inventory.setItem(18, borderItem);
					inventory.setItem(26, borderItem);
					for (h = 27; h < 36; h++) {
						if (inventory.getItem(h) == null)
							inventory.setItem(h, borderItem);
					}
					break;
				case 45:
					int o;
					for (o = 0; o < 10; o++) {
						if (inventory.getItem(o) == null)
							inventory.setItem(o, borderItem);
					}
					inventory.setItem(17, borderItem);
					inventory.setItem(18, borderItem);
					inventory.setItem(26, borderItem);
					inventory.setItem(27, borderItem);
					inventory.setItem(35, borderItem);
					inventory.setItem(36, borderItem);
					for (o = 36; o < 45; o++) {
						if (inventory.getItem(o) == null)
							inventory.setItem(o, borderItem);
					}
					break;
				case 54:
					int j;
					for (j = 0; j < 10; j++) {
						if (inventory.getItem(j) == null)
							inventory.setItem(j, borderItem);
					}
					inventory.setItem(17, borderItem);
					inventory.setItem(18, borderItem);
					inventory.setItem(26, borderItem);
					inventory.setItem(27, borderItem);
					inventory.setItem(35, borderItem);
					inventory.setItem(36, borderItem);
					for (j = 44; j < 54; j++) {
						if (inventory.getItem(j) == null)
							inventory.setItem(j, borderItem);
					}
					break;
			}
		}
		if (collection != null && !collection.isEmpty()) {
			PaginatedList<T> list = new PaginatedList<>(this.collection)
					.limit(limit)
					.decorate((pagination, object, page, max, placement) -> {
						if (object != null) {
							this.index = placement;
							if (menuProcess != null) {
								if (index <= this.collection.size()) {
									PaginatedProcessAction<T> element = new PaginatedProcessAction<>(this, object);
									menuProcess.accept(element);
									CompletableFuture.runAsync(() -> inventory.addItem(element.getItem())).join();

									if (!processList.contains(element.getItem())) {
										processList.add(element.getItem());
									}
								}
							}
						} else {
							this.plugin.getLogger().warning("- +1 object failed to load in menu " + this.title);
						}
					});

			list.get(this.page);

		}
		if (fillerItem != null) {
			for (int l = 0; l < size; l++) {
				if (inventory.getItem(l) == null) {
					inventory.setItem(l, fillerItem);
				}
			}
		}
		ItemStack left = navigationLeft.keySet().stream().findFirst().orElse(null);
		ItemStack right = navigationRight.keySet().stream().findFirst().orElse(null);
		ItemStack back = navigationBack.keySet().stream().findFirst().orElse(null);
		if (left != null) {
			if (!inventory.contains(left)) {
				inventory.setItem(navigationLeft.get(left), left);
				inventory.setItem(navigationRight.get(right), right);
				inventory.setItem(navigationBack.get(back), back);
			}
		}
		if (!initialContents.isEmpty()) {
			for (Map.Entry<ItemStack, Integer> entry : initialContents.entrySet()) {
				if (entry.getValue() == -1) {
					inventory.addItem(entry.getKey());
				} else {
					inventory.setItem(entry.getValue(), entry.getKey());
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
		this.navigationLeft.putIfAbsent(item.get(), slot);
		this.itemActions.putIfAbsent(item.get(), click);
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
		this.navigationRight.putIfAbsent(item.get(), slot);
		this.itemActions.putIfAbsent(item.get(), click);
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
		this.navigationBack.putIfAbsent(item.get(), slot);
		this.itemActions.putIfAbsent(item.get(), click);
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
		this.navigationLeft.putIfAbsent(item, slot);
		this.itemActions.putIfAbsent(item, click);
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
		this.navigationRight.putIfAbsent(item, slot);
		this.itemActions.putIfAbsent(item, click);
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
		this.navigationBack.putIfAbsent(item, slot);
		this.itemActions.putIfAbsent(item, click);
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
		return id;
	}

	/**
	 * Get the inventory for this menu.
	 *
	 * @return An inventory object.
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Get the listener registered with this menu.
	 *
	 * @return A bukkit listener.
	 */
	public PaginatedListener getController() {
		return controller;
	}

	/**
	 * Get the amount of items specified per page.
	 *
	 * @return Amount of items per page.
	 */
	public int getAmountPerPage() {
		return limit;
	}

	/**
	 * Get the max amount of pages after collection conversions.
	 *
	 * @return Max amount of GUI pages.
	 */
	public int getMaxPages() {
		return (collection.size() / (limit - 1)) < 0 ? collection.size() / limit : collection.size() / limit - 1;
	}

	/**
	 * Get the default namespace used when one isn't provided.
	 *
	 * @return A namespaced key.
	 */
	public NamespacedKey getKey() {
		return namespace;
	}

	/**
	 * Get the collection involved with the menu.
	 *
	 * @return The string collection used in the GUI.
	 */
	public List<T> getCollection() {
		return collection;
	}

	/**
	 * Get the plugin registered with the menu.
	 *
	 * @return The plugin connected with the menu.
	 */
	public Plugin getPlugin() { // TODO: mark nullity
		return plugin;
	}

	/**
	 * Internal bukkit event logic, everything built within the paginated builder will be applied here automatically.
	 */
	public class PaginatedListener implements Listener {

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClose(InventoryCloseEvent e) {
			if (!(e.getPlayer() instanceof Player))
				return;
			if (e.getView().getTopInventory().getSize() < size)
				return;
			if (e.getInventory().equals(getInventory())) {

				Player p = (Player) e.getPlayer();

				if (task.containsKey(p)) {
					task.get(p).cancelTask();
					task.remove(p);
				}
				if (menuClose != null) {
					menuClose.closeEvent(new PaginatedCloseAction<>(PaginatedBuilder.this, p, e.getView()));
				}
				page = 0;
				index = 0;
				p.updateInventory();
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onMove(InventoryMoveItemEvent e) {
			if (e.getSource().equals(getInventory())) {
				e.setCancelled(true);
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onDrag(InventoryDragEvent e) {
			if (!(e.getWhoClicked() instanceof Player))
				return;
			if (e.getView().getTopInventory().getSize() < size)
				return;
			if (!e.getInventory().equals(getInventory())) return;

			if (e.getInventory().equals(getInventory())) {
				e.setResult(Event.Result.DENY);
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClick(InventoryClickEvent e) {
			if (!(e.getWhoClicked() instanceof Player))
				return;
			if (e.getView().getTopInventory().getSize() < size) return;

			if (e.getHotbarButton() != -1) {
				e.setCancelled(true);
				return;
			}

			if (!e.getInventory().equals(getInventory())) return;

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
					if (processList.stream().anyMatch(i -> i.isSimilar(item))) {
						// TODO: Reformat to safe optional use
						itemActions.entrySet().stream().filter(en -> en.getKey().isSimilar(item)).map(Map.Entry::getValue).findFirst().get().clickEvent(new PaginatedClickAction<>(PaginatedBuilder.this, p, e.getView(), item, e.isLeftClick(), e.isRightClick(), e.isShiftClick(), e.getClick() == ClickType.MIDDLE));
					}
					if (navigationBack.keySet().stream().anyMatch(i -> i.isSimilar(item))) {
						itemActions.get(item).clickEvent(new PaginatedClickAction<>(PaginatedBuilder.this, p, e.getView(), item, e.isLeftClick(), e.isRightClick(), e.isShiftClick(), e.getClick() == ClickType.MIDDLE));
					}
					if (navigationLeft.keySet().stream().anyMatch(i -> i.isSimilar(item))) {
						if (page == 1) {
							p.sendMessage(firstPageMessage);
						} else {
							SyncMenuSwitchPageEvent<T> event1 = new SyncMenuSwitchPageEvent<>(PaginatedBuilder.this, p, e.getView(), item, page);
							Bukkit.getPluginManager().callEvent(event1);
							if (!event1.isCancelled()) {
								page -= 1;
							}
							itemActions.get(item).clickEvent(new PaginatedClickAction<>(PaginatedBuilder.this, p, e.getView(), item, e.isLeftClick(), e.isRightClick(), e.isShiftClick(), e.getClick() == ClickType.MIDDLE));
						}
					}
					if (navigationRight.keySet().stream().anyMatch(i -> i.isSimilar(item))) {
						if (!((index + 1) >= collection.size() + 1)) {
							SyncMenuSwitchPageEvent<T> event1 = new SyncMenuSwitchPageEvent<>(PaginatedBuilder.this, p, e.getView(), item, page);
							Bukkit.getPluginManager().callEvent(event1);
							if (!event1.isCancelled()) {
								page += 1;
								itemActions.get(item).clickEvent(new PaginatedClickAction<>(PaginatedBuilder.this, p, e.getView(), item, e.isLeftClick(), e.isRightClick(), e.isShiftClick(), e.getClick() == ClickType.MIDDLE));
							}
						} else {
							p.sendMessage(lastPageMessage);
						}
					}
				}
			}

		}

	}


}
