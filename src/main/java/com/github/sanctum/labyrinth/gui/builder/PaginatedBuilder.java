package com.github.sanctum.labyrinth.gui.builder;

import com.github.sanctum.labyrinth.gui.InventoryRows;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

/**
 * The primary object used to initialize a PaginatedMenu object.
 * Use this to customize everything from click,close events to element specifications.
 */
public final class PaginatedBuilder {

	protected Inventory inv;
	protected final Plugin plugin;
	protected int amountPer;
	protected int index;
	protected int page;
	protected int size;
	protected final UUID id;
	protected String title;
	protected String alreadyFirstPage;
	protected String alreadyLastPage;
	protected LinkedList<String> collection;
	protected ItemStack border;
	protected ItemStack fill;
	protected InventoryClose closeAction;
	protected InventoryProcess inventoryProcess;
	protected final Map<ItemStack, Integer> navLeft = new HashMap<>();
	protected final Map<ItemStack, Integer> navRight = new HashMap<>();
	protected final Map<ItemStack, Integer> navBack = new HashMap<>();
	protected final Map<ItemStack, Integer> additional = new HashMap<>();
	protected final PaginatedListener listener;
	protected final NamespacedKey key;
	protected final LinkedList<ItemStack> contents = new LinkedList<>();
	protected final Map<ItemStack, InventoryClick> actions = new HashMap<>();

	public PaginatedBuilder(Plugin plugin) {
		this.plugin = plugin;
		this.id = UUID.randomUUID();
		key = new NamespacedKey(plugin, "paginated_utility_manager");
		listener = new PaginatedListener(this);
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	public PaginatedBuilder(Plugin plugin, String title) {
		this.title = title;
		this.plugin = plugin;
		this.id = UUID.randomUUID();
		key = new NamespacedKey(plugin, "paginated_utility_manager");
		listener = new PaginatedListener(this);
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	/**
	 * Set the title to be viewed when players open this menu.
	 *
	 * @param title The title of the GUI.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder setTitle(String title) {
		this.title = title.replace("{PAGE}", "" + page);
		return this;
	}

	/**
	 * Store a specified collection to be converted to customized elements
	 *
	 * @param collection The collection of strings to use
	 * @return The same menu builder.
	 */
	public PaginatedBuilder collect(LinkedList<String> collection) {
		this.collection = collection;
		return this;
	}

	/**
	 * Limit the amount of items to be displayed per page.
	 *
	 * @param amountPer The amount of items per page.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder limit(int amountPer) {
		this.amountPer = amountPer;
		return this;
	}

	/**
	 * Define how large the inventory will be
	 *
	 * @param size The size of the inventory
	 * @return The same menu builder.
	 */
	public PaginatedBuilder setSize(int size) {
		this.size = size;
		return this;
	}

	/**
	 * Define how large the inventory will be
	 *
	 * @param rows The size of the inventory
	 * @return The same menu builder.
	 */
	public PaginatedBuilder setSize(InventoryRows rows) {
		this.size = rows.slotCount;
		return this;
	}

	/**
	 * Set the message to be displayed when a player attempts to switch to a previous page on the initial page.
	 *
	 * @param context The message to be displayed otherwise empty.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder setAlreadyFirst(String context) {
		this.alreadyFirstPage = context.replace("{PAGE}", "" + page);
		return this;
	}

	/**
	 * Set the message to be displayed when a player attempts to switch to the next page on the last page.
	 *
	 * @param context The message to be displayed otherwise empty.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder setAlreadyLast(String context) {
		this.alreadyLastPage = context.replace("{PAGE}", "" + page);
		;
		return this;
	}

	/**
	 * Set the operation to be ran in the event of this menu being closed.
	 *
	 * @param inventoryClose The inventory close action
	 * @return The same menu builder.
	 */
	public PaginatedBuilder setCloseAction(InventoryClose inventoryClose) {
		this.closeAction = inventoryClose;
		return this;
	}

	/**
	 * Create a {@link ProcessElement} to customize each item to be displayed within the collection.
	 *
	 * @param inventoryProcess The inventory processing operation.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder setupProcess(InventoryProcess inventoryProcess) {
		this.inventoryProcess = inventoryProcess;
		return this;
	}

	/**
	 * Initialize a border for the menu or fill remaining slots with specified materials.
	 *
	 * @return A border building elemement.
	 */
	public BorderElement addBorder() {
		return new BorderElement(this);
	}

	/**
	 * Initialize any additional elements with defined logic.
	 *
	 * @return An spare element builder.
	 */
	public SpareElement newItem() {
		return new SpareElement(this);
	}

	/**
	 * Automatically format all menu items in accordance and default to a specific page for opening.
	 *
	 * @param desiredPage The desired page to be opened.
	 * @return The same menu builder.
	 */
	protected PaginatedBuilder adjust(int desiredPage) {
		page = desiredPage;
		if (border != null) {
			switch (size) {
				case 27:
					int f;
					for (f = 0; f < 10; f++) {
						if (inv.getItem(f) == null)
							inv.setItem(f, border);
					}
					inv.setItem(17, border);
					for (f = 18; f < 27; f++) {
						if (inv.getItem(f) == null)
							inv.setItem(f, border);
					}
					break;
				case 36:
					int h;
					for (h = 0; h < 10; h++) {
						if (inv.getItem(h) == null)
							inv.setItem(h, border);
					}
					inv.setItem(17, border);
					inv.setItem(18, border);
					inv.setItem(26, border);
					for (h = 27; h < 36; h++) {
						if (inv.getItem(h) == null)
							inv.setItem(h, border);
					}
					break;
				case 45:
					int o;
					for (o = 0; o < 10; o++) {
						if (inv.getItem(o) == null)
							inv.setItem(o, border);
					}
					inv.setItem(17, border);
					inv.setItem(18, border);
					inv.setItem(26, border);
					inv.setItem(27, border);
					inv.setItem(35, border);
					inv.setItem(36, border);
					for (o = 36; o < 45; o++) {
						if (inv.getItem(o) == null)
							inv.setItem(o, border);
					}
					break;
				case 54:
					int j;
					for (j = 0; j < 10; j++) {
						if (inv.getItem(j) == null)
							inv.setItem(j, border);
					}
					inv.setItem(17, border);
					inv.setItem(18, border);
					inv.setItem(26, border);
					inv.setItem(27, border);
					inv.setItem(35, border);
					inv.setItem(36, border);
					for (j = 44; j < 54; j++) {
						if (inv.getItem(j) == null)
							inv.setItem(j, border);
					}
					break;
			}
		}
		if (collection == null) {
			collection = new LinkedList<>();
		}
		LinkedList<String> members = collection;
		if (!members.isEmpty()) {
			for (int i = 0; i < amountPer; i++) {
				index = amountPer * page + i;
				if (index >= members.size())
					break;
				if (members.get(index) != null) {
					boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
					ItemStack item;
					if (isNew) {
						item = new ItemStack(Material.valueOf("PLAYER_HEAD"));
					} else {
						item = new ItemStack(Material.valueOf("SKULL_ITEM"));
					}
					ItemStack left = navLeft.keySet().stream().findFirst().orElse(null);
					ItemStack right = navRight.keySet().stream().findFirst().orElse(null);
					ItemStack back = navBack.keySet().stream().findFirst().orElse(null);
					if (left != null) {
						if (!inv.contains(left)) {
							inv.setItem(navLeft.get(left), left);
							inv.setItem(navRight.get(right), right);
							inv.setItem(navBack.get(back), back);
						}
					}

					SyncMenuItemPreProcessEvent event = new SyncMenuItemPreProcessEvent(this, members.get(index), item);
					Bukkit.getPluginManager().callEvent(event);

					Schedule.sync(() -> {
						inv.addItem(event.getItem());
						if (!contents.contains(event.getItem())) {
							contents.add(event.getItem());
						}
						if (fill != null) {
							Schedule.sync(() -> {
								for (int l = 0; l < size; l++) {
									if (inv.getItem(l) == null) {
										inv.setItem(l, fill);
									}
								}
							}).debug().wait(1);
						}
					}).debug().run();
				}
			}
			if (!additional.isEmpty()) {
				for (Map.Entry<ItemStack, Integer> entry : additional.entrySet()) {
					if (entry.getValue() == -1) {
						inv.addItem(entry.getKey());
					} else {
						inv.setItem(entry.getValue(), entry.getKey());
					}
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
	protected PaginatedBuilder adjust() {
		if (border != null) {
			switch (size) {
				case 27:
					int f;
					for (f = 0; f < 10; f++) {
						if (inv.getItem(f) == null)
							inv.setItem(f, border);
					}
					inv.setItem(17, border);
					for (f = 18; f < 27; f++) {
						if (inv.getItem(f) == null)
							inv.setItem(f, border);
					}
					break;
				case 36:
					int h;
					for (h = 0; h < 10; h++) {
						if (inv.getItem(h) == null)
							inv.setItem(h, border);
					}
					inv.setItem(17, border);
					inv.setItem(18, border);
					inv.setItem(26, border);
					for (h = 27; h < 36; h++) {
						if (inv.getItem(h) == null)
							inv.setItem(h, border);
					}
					break;
				case 45:
					int o;
					for (o = 0; o < 10; o++) {
						if (inv.getItem(o) == null)
							inv.setItem(o, border);
					}
					inv.setItem(17, border);
					inv.setItem(18, border);
					inv.setItem(26, border);
					inv.setItem(27, border);
					inv.setItem(35, border);
					inv.setItem(36, border);
					for (o = 36; o < 45; o++) {
						if (inv.getItem(o) == null)
							inv.setItem(o, border);
					}
					break;
				case 54:
					int j;
					for (j = 0; j < 10; j++) {
						if (inv.getItem(j) == null)
							inv.setItem(j, border);
					}
					inv.setItem(17, border);
					inv.setItem(18, border);
					inv.setItem(26, border);
					inv.setItem(27, border);
					inv.setItem(35, border);
					inv.setItem(36, border);
					for (j = 44; j < 54; j++) {
						if (inv.getItem(j) == null)
							inv.setItem(j, border);
					}
					break;
			}
		}
		if (collection == null) {
			collection = new LinkedList<>();
		}
		LinkedList<String> members = collection;
		if (!members.isEmpty()) {
			for (int i = 0; i < amountPer; i++) {
				index = amountPer * page + i;
				if (index >= members.size())
					break;
				if (members.get(index) != null) {
					boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
					ItemStack item;
					if (isNew) {
						item = new ItemStack(Material.valueOf("PLAYER_HEAD"));
					} else {
						item = new ItemStack(Material.valueOf("SKULL_ITEM"));
					}
					ItemStack left = navLeft.keySet().stream().findFirst().orElse(null);
					ItemStack right = navRight.keySet().stream().findFirst().orElse(null);
					ItemStack back = navBack.keySet().stream().findFirst().orElse(null);
					if (left != null) {
						if (!inv.contains(left)) {
							inv.setItem(navLeft.get(left), left);
							inv.setItem(navRight.get(right), right);
							inv.setItem(navBack.get(back), back);
						}
					}
					SyncMenuItemPreProcessEvent event = new SyncMenuItemPreProcessEvent(this, members.get(index), item);
					Bukkit.getPluginManager().callEvent(event);

					Schedule.sync(() -> {
						inv.addItem(event.getItem());
						Schedule.sync(() -> {
							if (!contents.contains(event.getItem())) {
								contents.add(event.getItem());
							}
						}).debug().wait(1);
						if (fill != null) {
							Schedule.sync(() -> {
								for (int l = 0; l < size; l++) {
									if (inv.getItem(l) == null) {
										inv.setItem(l, fill);
									}
								}
							}).debug().wait(1);
						}
					}).debug().run();
				}
			}
			if (!additional.isEmpty()) {
				for (Map.Entry<ItemStack, Integer> entry : additional.entrySet()) {
					if (entry.getValue() == -1) {
						inv.addItem(entry.getKey());
					} else {
						inv.setItem(entry.getValue(), entry.getKey());
					}
				}
			}
		}
		return this;
	}

	/**
	 * Customize a page-back navigation key for the menu.
	 *
	 * @param item The item to be used to page-back with.
	 * @param slot The slot the item will reside permanently.
	 * @param click The inventory click action for the item.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder setNavigationLeft(ItemStack item, int slot, InventoryClick click) {
		this.navLeft.putIfAbsent(item, slot);
		this.actions.putIfAbsent(item, click);
		return this;
	}

	/**
	 * Customize a page-forward navigation key for the menu.
	 *
	 * @param item The item to be used to page-forward with.
	 * @param slot The slot the item will reside permanently.
	 * @param click The inventory click action for the item.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder setNavigationRight(ItemStack item, int slot, InventoryClick click) {
		this.navRight.putIfAbsent(item, slot);
		this.actions.putIfAbsent(item, click);
		return this;
	}

	/**
	 * Customize a page-exit navigation key for the menu.
	 *
	 * @param item The item to be used to page-exit with.
	 * @param slot The slot the item will reside permanently.
	 * @param click The inventory click action for the item.
	 * @return The same menu builder.
	 */
	public PaginatedBuilder setNavigationBack(ItemStack item, int slot, InventoryClick click) {
		this.navBack.putIfAbsent(item, slot);
		this.actions.putIfAbsent(item, click);
		return this;
	}

	/**
	 * Complete the menu building process and convert the builder into a Paginated Menu ready to be used.
	 *
	 * @return A fully built paginated menu.
	 */
	public PaginatedMenu build() {
		return new PaginatedMenu(this);
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
		return inv;
	}

	/**
	 * Get the listener registered with this menu.
	 *
	 * @return A bukkit listener.
	 */
	public PaginatedListener getListener() {
		return listener;
	}

	/**
	 * Get the amount of items specified per page.
	 *
	 * @return Amount of items per page.
	 */
	public int getAmountPerPage() {
		return amountPer;
	}

	/**
	 * Get the max amount of pages after collection conversions.
	 *
	 * @return Max amount of GUI pages.
	 */
	public int getMaxPages() {
		return (collection.size() / (amountPer - 1)) < 0 ? collection.size() / amountPer : collection.size() / amountPer - 1;
	}

	/**
	 * Get the default namespace used when one isnt provided.
	 *
	 * @return A namespaced key.
	 */
	public NamespacedKey getKey() {
		return key;
	}

	/**
	 * Get the collection involed with the menu.
	 *
	 * @return The string collection used in the GUI.
	 */
	public LinkedList<String> getCollection() {
		return collection;
	}

	/**
	 * Get the plugin registered with the menu.
	 *
	 * @return The plugin connected with the menu.
	 */
	public Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Internal bukkit event logic, everything built within the paginated builder will be applied here automatically.
	 */
	private static class PaginatedListener implements Listener {

		private final PaginatedBuilder builder;

		protected PaginatedListener(PaginatedBuilder builder) {
			this.builder = builder;
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onProcess(SyncMenuItemPreProcessEvent e) throws IllegalMenuStateException {
			if (builder.inventoryProcess == null) {
				throw new IllegalMenuStateException("No inventory processing procedure was found for menu '" + ChatColor.stripColor(builder.title) + "'");
			} else {
				builder.inventoryProcess.processEvent(new ProcessElement(e));
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onProcess(SyncMenuSwitchPageEvent e) throws IllegalMenuStateException {
			if (builder.inventoryProcess == null) {
				throw new IllegalMenuStateException("No inventory processing procedure was found for menu '" + ChatColor.stripColor(builder.title) + "'");
			} else {
				builder.inventoryProcess.processEvent(new ProcessElement(e));
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClose(InventoryCloseEvent e) {
			if (!(e.getPlayer() instanceof Player))
				return;
			if (e.getView().getTopInventory().getSize() < builder.size)
				return;
			if (builder.getInventory() == e.getInventory()) {
				if (builder.closeAction != null) {
					builder.closeAction.closeEvent(new PaginatedClose(builder, (Player) e.getPlayer(), e.getView()));
				}
				builder.page = 0;
				builder.index = 0;
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClick(InventoryClickEvent e) {
			if (!(e.getWhoClicked() instanceof Player))
				return;
			if (e.getView().getTopInventory().getSize() < builder.size)
				return;

			if (e.getHotbarButton() != -1) {
				e.setCancelled(true);
				return;
			}

			if (e.getClickedInventory() == e.getInventory()) {
				Player p = (Player) e.getWhoClicked();
				if (e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();
					SyncMenuClickItemEvent event = new SyncMenuClickItemEvent(builder, p, e.getView(), item);
					Bukkit.getPluginManager().callEvent(event);
					if (event.isCancelled()) {
						e.setCancelled(true);
						return;
					}
					if (builder.contents.stream().anyMatch(i -> i.isSimilar(item))) {
						builder.actions.get(item).clickEvent(new PaginatedClick(builder, p, e.getView(), item));
						e.setCancelled(true);
					}
					if (item.hasItemMeta()) {
						final boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
						Material skull = Items.getMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");
						if (item.getType() == skull) {
							SkullMeta meta = (SkullMeta) item.getItemMeta();
							if (meta.hasOwner()) {
								for (ItemStack it : builder.actions.keySet()) {
									if (it.hasItemMeta()) {
										if (it.getType() == skull) {
											SkullMeta sm = (SkullMeta) it.getItemMeta();
											if (sm.hasOwner()) {
												if (sm.getOwningPlayer().getUniqueId().equals(meta.getOwningPlayer().getUniqueId())) {
													builder.actions.get(it).clickEvent(new PaginatedClick(builder, p, e.getView(), item));
													break;
												}
											}
										}
									}
								}
							}
						}
					}
					if (builder.navBack.keySet().stream().anyMatch(i -> i.isSimilar(item))) {
						builder.actions.get(item).clickEvent(new PaginatedClick(builder, p, e.getView(), item));
						e.setCancelled(true);
					}
					if (builder.navLeft.keySet().stream().anyMatch(i -> i.isSimilar(item))) {
						if (builder.page == 0) {
							p.sendMessage(builder.alreadyFirstPage);
						} else {
							SyncMenuSwitchPageEvent event1 = new SyncMenuSwitchPageEvent(builder, p, e.getView(), item, builder.page);
							Bukkit.getPluginManager().callEvent(event1);
							if (!event1.isCancelled()) {
								builder.page -= 1;
							}
							builder.actions.get(item).clickEvent(new PaginatedClick(builder, p, e.getView(), item));
						}
						e.setCancelled(true);
					}
					if (builder.navRight.keySet().stream().anyMatch(i -> i.isSimilar(item))) {
						if (!((builder.index + 1) >= builder.collection.size())) {
							SyncMenuSwitchPageEvent event1 = new SyncMenuSwitchPageEvent(builder, p, e.getView(), item, builder.page);
							Bukkit.getPluginManager().callEvent(event1);
							if (!event1.isCancelled()) {
								builder.page += 1;
								builder.actions.get(item).clickEvent(new PaginatedClick(builder, p, e.getView(), item));
							}
						} else {
							p.sendMessage(builder.alreadyLastPage);
						}
						e.setCancelled(true);
					}
					if (e.getCurrentItem().equals(builder.border) || item.equals(builder.fill)) {
						e.setCancelled(true);
					}
				}
			}

		}

	}


}
