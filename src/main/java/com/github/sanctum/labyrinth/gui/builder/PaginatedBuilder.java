package com.github.sanctum.labyrinth.gui.builder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class PaginatedBuilder {

	protected Inventory inv;
	protected final Plugin plugin;
	protected int amountPer;
	protected int index;
	protected int page;
	protected final UUID id;
	protected String title;
	protected String alreadyFirstPage;
	protected String alreadyLastPage;
	protected LinkedList<String> collection;
	protected ItemStack border;
	protected ItemStack fill;
	protected InventoryClose closeAction;
	protected final Map<ItemStack, Integer> navLeft = new HashMap<>();
	protected final Map<ItemStack, Integer> navRight = new HashMap<>();
	protected final Map<ItemStack, Integer> navBack = new HashMap<>();
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

	public PaginatedBuilder setTitle(String title) {
		this.title = title.replace("{PAGE}", "" + page);
		return this;
	}

	public PaginatedBuilder collect(LinkedList<String> collection) {
		this.collection = collection;
		return this;
	}

	public PaginatedBuilder limit(int amountPer) {
		this.amountPer = amountPer;
		return this;
	}

	public PaginatedBuilder setAlreadyFirst(String context) {
		this.alreadyFirstPage = context.replace("{PAGE}", "" + page);
		return this;
	}

	public PaginatedBuilder setAlreadyLast(String context) {
		this.alreadyLastPage = context.replace("{PAGE}", "" + page);
		;
		return this;
	}

	public PaginatedBuilder setCloseAction(InventoryClose inventoryClose) {
		this.closeAction = inventoryClose;
		return this;
	}

	public BorderElement addBorder() {
		return new BorderElement(this);
	}

	public UUID getId() {
		return id;
	}

	protected PaginatedBuilder adjust(int desiredPage) {
		page = desiredPage;
		if (border != null) {
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

					new BukkitRunnable() {
						@Override
						public void run() {
							inv.addItem(event.getItem());
							if (!contents.contains(event.getItem())) {
								contents.add(event.getItem());
							}
							if (fill != null) {
								for (int i = 0; i < 54; i++) {
									if (inv.getItem(i) == null) {
										inv.setItem(i, fill);
									}
								}
							}
						}
					}.runTask(plugin);
				}
			}
		}
		return this;
	}

	protected PaginatedBuilder adjust() {
		if (border != null) {
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

					new BukkitRunnable() {
						@Override
						public void run() {
							inv.addItem(event.getItem());
							if (!contents.contains(event.getItem())) {
								contents.add(event.getItem());
							}
							if (fill != null) {
								for (int i = 0; i < 54; i++) {
									if (inv.getItem(i) == null) {
										inv.setItem(i, fill);
									}
								}
							}
						}
					}.runTask(plugin);
				}
			}
		}
		return this;
	}

	public PaginatedBuilder setNavigationLeft(ItemStack item, int slot, InventoryClick click) {
		this.navLeft.putIfAbsent(item, slot);
		this.actions.putIfAbsent(item, click);
		return this;
	}

	public PaginatedBuilder setNavigationRight(ItemStack item, int slot, InventoryClick click) {
		this.navRight.putIfAbsent(item, slot);
		this.actions.putIfAbsent(item, click);
		return this;
	}

	public PaginatedBuilder setNavigationBack(ItemStack item, int slot, InventoryClick click) {
		this.navBack.putIfAbsent(item, slot);
		this.actions.putIfAbsent(item, click);
		return this;
	}

	public PaginatedMenu build() {
		return new PaginatedMenu(this);
	}

	public Inventory getInventory() {
		return inv;
	}

	public PaginatedListener getListener() {
		return listener;
	}

	public int getAmountPerPage() {
		return amountPer;
	}

	public int getMaxPages() {
		return (collection.size() / (amountPer - 1)) < 0 ? collection.size() / amountPer : collection.size() / amountPer - 1;
	}

	public NamespacedKey getKey() {
		return key;
	}

	public LinkedList<String> getCollection() {
		return collection;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	private static class PaginatedListener implements Listener {

		private final PaginatedBuilder builder;

		protected PaginatedListener(PaginatedBuilder builder) {
			this.builder = builder;
		}

		@EventHandler(priority = EventPriority.LOW)
		public void onFill(SyncMenuItemPreProcessEvent e) {
			try {
				UUID id = UUID.fromString(e.getContext());
			} catch (IllegalArgumentException ignored) {
				return;
			}
			e.buildItem(() -> {
				ItemStack item = e.getItem();
				ItemMeta meta = item.getItemMeta();
				UUID id = UUID.fromString(e.getContext());
				meta.setDisplayName(Bukkit.getOfflinePlayer(id).getName());
				meta.getPersistentDataContainer().set(builder.key, PersistentDataType.STRING, e.getContext());
				item.setItemMeta(meta);
				return item;
			});
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClose(InventoryCloseEvent e) {
			if (!(e.getPlayer() instanceof Player))
				return;
			if (e.getView().getTopInventory().getSize() < 54)
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
			if (e.getView().getTopInventory().getSize() < 54)
				return;

			if (e.getHotbarButton() != -1) {
				e.setCancelled(true);
				return;
			}

			if (e.getClickedInventory() == e.getInventory()) {
				Player p = (Player) e.getWhoClicked();
				if (e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();
					SyncMenuClickEvent event = new SyncMenuClickEvent(builder, p, e.getView(), item);
					Bukkit.getPluginManager().callEvent(event);
					if (event.isCancelled()) {
						e.setCancelled(true);
						return;
					}
					if (builder.contents.contains(item)) {
						builder.actions.get(item).clickEvent(new PaginatedClick(builder, p, e.getView(), item));
						e.setCancelled(true);
					}
					if (builder.navBack.keySet().stream().anyMatch(i -> i.isSimilar(item))) {
						builder.actions.get(item).clickEvent(new PaginatedClick(builder, p, e.getView(), item));
						e.setCancelled(true);
					}
					if (builder.navLeft.keySet().stream().anyMatch(i -> i.isSimilar(item))) {
						if (builder.page == 0) {
							p.sendMessage(builder.alreadyFirstPage);
						} else {
							builder.page -= 1;
							builder.actions.get(item).clickEvent(new PaginatedClick(builder, p, e.getView(), item));
						}
						e.setCancelled(true);
					}
					if (builder.navRight.keySet().stream().anyMatch(i -> i.isSimilar(item))) {
						if (!((builder.index + 1) >= builder.collection.size())) {
							builder.page += 1;
							builder.actions.get(item).clickEvent(new PaginatedClick(builder, p, e.getView(), item));
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
