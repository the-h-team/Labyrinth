package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.data.service.AnvilMechanics;
import com.github.sanctum.labyrinth.formatting.PaginatedList;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Asynchronous;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class InventoryElement extends Menu.Element<Inventory, Set<ItemElement<?>>> {

	protected final Map<Player, Asynchronous> tasks;
	protected final Set<ItemElement<?>> items;
	protected final Set<PagedPlayer> index;
	protected final boolean lazy;
	protected final String title;
	protected final Menu menu;
	protected final Map<Player, Inventory> invmap;
	protected ListElement<?> listElement;
	protected Inventory inventory;
	protected int limit = 5;
	protected int page = 1;

	public InventoryElement(String title, Menu menu, boolean lazy) {
		this.items = new HashSet<>();
		this.menu = menu;
		this.tasks = new HashMap<>();
		this.index = new HashSet<>();
		this.invmap = new HashMap<>();
		this.title = title;
		this.lazy = lazy;
	}

	public synchronized void open(Player player) {}

	@Override
	public Inventory getElement() {

		if (this.inventory == null) {
			this.inventory = Bukkit.createInventory(null, menu.getSize().getSlots(), StringUtils.use(MessageFormat.format(this.title, page, getTotalPages())).translate());
		}

		if (this.menu.getProperties().contains(Menu.Property.REFILLABLE)) {
			if (UniformedComponents.accept(Arrays.asList(this.inventory.getContents())).filter(i -> i != null).count() == 0) {
				if (isPaginated()) {
					for (ItemElement<?> element : new PaginatedList<>(getWorkflow()).limit(this.listElement.getLimit()).compare(this.listElement.comparator).filter(this.listElement.predicate).get(page)) {
						Optional<Integer> i = element.getSlot();
						if (i.isPresent()) {
							this.inventory.setItem(i.get(), element.getElement());
						} else {
							if (!this.inventory.contains(element.getElement())) {
								this.inventory.addItem(element.getElement());
							}
						}
					}
					for (ItemElement<?> element : items) {
						Optional<Integer> i = element.getSlot();
						i.ifPresent(integer -> this.inventory.setItem(integer, element.getElement()));
					}
				} else {
					for (ItemElement<?> element : items) {
						Optional<Integer> i = element.getSlot();
						if (i.isPresent()) {
							this.inventory.setItem(i.get(), element.getElement());
						} else {
							if (!this.inventory.contains(element.getElement())) {
								this.inventory.addItem(element.getElement());
							}
						}
					}
				}
			}
		}
		return this.inventory;
	}

	public Inventory getElement(Player player) {
		Inventory inventory = this.invmap.computeIfAbsent(player, p -> Bukkit.createInventory(null, this.menu.getSize().getSlots(), StringUtils.use(MessageFormat.format(this.title, getPlayer(player).getPage().toNumber(), getTotalPages())).translate()));
		if (this.menu.getProperties().contains(Menu.Property.REFILLABLE)) {
			if (UniformedComponents.accept(Arrays.asList(inventory.getContents())).filter(i -> i != null).count() == 0) {
				if (isPaginated()) {
					for (ItemElement<?> element : getPlayer(player).getPage().getAttachment()) {
						Optional<Integer> i = element.getSlot();
						if (i.isPresent()) {
							inventory.setItem(i.get(), element.getElement());
						} else {
							if (!inventory.contains(element.getElement())) {
								inventory.addItem(element.getElement());
							}
						}
					}
					for (ItemElement<?> element : items) {
						Optional<Integer> i = element.getSlot();
						i.ifPresent(integer -> inventory.setItem(integer, element.getElement()));
					}
				} else {
					for (ItemElement<?> element : items) {
						Optional<Integer> i = element.getSlot();
						if (i.isPresent()) {
							inventory.setItem(i.get(), element.getElement());
						} else {
							if (!inventory.contains(element.getElement())) {
								inventory.addItem(element.getElement());
							}
						}
					}
				}
			}
		}
		return inventory;
	}

	public InventoryElement setGlobalSlot(int page) {
		this.page = page;
		return this;
	}

	public @Nullable ItemElement<?> getItem(Predicate<ItemElement<?>> predicate) {
		for (ItemElement<?> it : getAttachment()) {
			if (predicate.test(it)) {
				return it;
			}
		}
		return null;
	}

	public Page getPage(int page) {
		if (findElement(e -> e instanceof Page && ((Page)e).toNumber() == page) != null) {
			return (Page) findElement(e -> e instanceof Page && ((Page)e).toNumber() == page);
		} else {
			Page p = new Page(page, this);
			addElement(p);
			return p;
		}
	}

	public Set<Page> getAllPages() {
		Set<Page> set = new HashSet<>();
		for (int i = 1; i < getTotalPages() + 1; i++) {
			set.add(getPage(i));
		}
		return set;
	}

	public Page getGlobalSlot() {
		return getPage(this.page);
	}

	public int getTotalPages() {
		int totalPageCount = 1;
		if ((getWorkflow().size() % this.limit) == 0) {
			if (getWorkflow().size() > 0) {
				totalPageCount = getWorkflow().size() / this.limit;
			}
		} else {
			totalPageCount = (getWorkflow().size() / this.limit) + 1;
		}
		return totalPageCount;
	}

	public @Nullable Asynchronous getTask(Player player) {
		return tasks.get(player);
	}

	/**
	 * Get a players page positioning.
	 *
	 * Only to be used when {@link com.github.sanctum.labyrinth.gui.unity.construct.Menu.Property#SHAREABLE} isn't present.
	 *
	 * @param player The player to use.
	 * @return A paged player.
	 */
	public PagedPlayer getPlayer(Player player) {
		PagedPlayer pl = this.index.stream().filter(p -> player.getName().equals(p.getPlayer().getName())).findFirst().orElse(null);
		if (pl == null) {
			pl = new PagedPlayer(player.getUniqueId(), this);
			this.index.add(pl);
		}
		return pl;
	}

	public Set<ItemElement<?>> getWorkflow() {
		Set<ItemElement<?>> items = new HashSet<>();
		for (ItemElement<?> it : this.items) {
			if (!it.getSlot().isPresent() && !it.isPlayerAdded()) {
				items.add(it);
			}
		}
		if (isPaginated()) {
			if (this.listElement != null) {
				items.addAll(listElement.getAttachment());
			}
		}
		return items;
	}

	@Override
	public Set<ItemElement<?>> getAttachment() {
		return this.items;
	}

	public @NotNull String getTitle() {
		return title;
	}

	public @NotNull Menu getParent() {
		return this.menu;
	}

	public @Nullable ItemElement<?> match(Predicate<ItemElement<?>> item) {
		return this.items.stream().filter(item).findFirst().orElse(null);
	}

	public @Nullable ItemElement<?> match(ItemStack item) {
		return this.items.stream().filter(i -> i.getElement().isSimilar(item)).findFirst().orElse(this.listElement == null ? null : this.listElement.getAttachment().stream().filter(i -> i.getElement().isSimilar(item)).findFirst().orElse(null));
	}

	public boolean contains(ItemStack item) {
		return this.items.stream().map(ItemElement::getElement).anyMatch(i -> i.isSimilar(item)) || this.listElement != null && this.listElement.getAttachment().stream().map(ItemElement::getElement).anyMatch(i -> i.isSimilar(item));
	}

	public InventoryElement setElement(Inventory inventory) {
		this.inventory = inventory;
		return this;
	}

	public InventoryElement setElement(Player target, Inventory inventory) {
		this.invmap.put(target, inventory);
		return this;
	}

	public <R> InventoryElement removeItem(ItemElement<R> item, boolean sincere) {
		this.items.remove(item);
		if (sincere) {
			this.getElement().remove(item.getElement());
		}
		return this;
	}

	public <R> InventoryElement removeItem(Player target, ItemElement<R> item, boolean sincere) {
		this.items.remove(item);
		if (sincere) {
			this.getElement(target).remove(item.getElement());
		}
		return this;
	}

	public <R> InventoryElement addItem(ItemElement<R> item) {
		this.items.add(item.setParent(this));
		return this;
	}

	public InventoryElement addItem(ItemStack... itemStacks) {
		for (ItemStack i : itemStacks) {
			if (i != null && i.getType() != Material.AIR) {
				addItem(new ItemElement<>().setElement(i));
			}
		}
		return this;
	}

	public InventoryElement addItem(ItemElement<?>... elements) {
		for (ItemElement<?> e : elements) {
			addItem(e);
		}
		return this;
	}

	public InventoryElement addItem(ListElement<?> element) {
		this.listElement = element.setParent(this);
		this.limit = element.getLimit();
		return this;
	}

	public InventoryElement addItem(Consumer<ItemElement<?>> builder) {
		ItemElement<?> element = new ItemElement<>();
		builder.accept(element);
		return addItem(element);
	}

	public <R> InventoryElement addItem(Consumer<ItemElement<R>> builder, R value) {
		ItemElement<R> element = new ItemElement<>(value);
		builder.accept(element);
		return addItem(element);
	}

	public boolean isPaginated() {
		return this instanceof Paginated;
	}

	public static class Page extends Menu.Element<InventoryElement, Set<ItemElement<?>>> {

		private final InventoryElement element;

		private final int num;

		private boolean full;

		public Page(int num, InventoryElement inventory) {
			this.element = inventory;
			this.num = num;
		}

		public boolean isFull() {
			return full;
		}

		public int toNumber() {
			return this.num;
		}

		@Override
		public InventoryElement getElement() {
			return this.element;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Page)) return false;
			Page page = (Page) o;
			return num == page.num &&
					getElement().equals(page.getElement());
		}

		@Override
		public int hashCode() {
			return Objects.hash(getElement(), num);
		}

		@Override
		public Set<ItemElement<?>> getAttachment() {
			Set<ItemElement<?>> set = new HashSet<>(new PaginatedList<>(getElement().getWorkflow()).limit(getElement().listElement.getLimit()).compare(getElement().listElement.comparator).filter(getElement().listElement.predicate).get(toNumber()));
			for (ItemElement<?> it : getElement().getAttachment()) {
				if (!set.contains(it) && it.isPlayerAdded() && it.getPage().toNumber() == toNumber()) {
					set.add(it);
				}
			}
			if (set.size() >= getElement().listElement.getLimit()) {
				this.full = true;
			}
			return set;
		}
	}

	public static class Paginated extends InventoryElement {

		public Paginated(String title, Menu menu) {
			super(title, menu, true);
		}

		@Override
		public synchronized void open(Player player) {

			if (lazy) {
				// This area dictates that our inventory is "lazy" and needs to be instantiated
				this.invmap.remove(player);
			}
			if (this.menu.getProperties().contains(Menu.Property.LIVE_META)) {
				getElement(player).setMaxStackSize(1);
				if (this.tasks.containsKey(player)) {
					this.tasks.get(player).cancelTask();
				}

				this.tasks.put(player, Schedule.async(() -> Schedule.sync(() -> {
					getElement(player).clear();
					for (ItemElement<?> element : getPlayer(player).getPage().getAttachment()) {
						Optional<Integer> in = element.getSlot();
						if (in.isPresent()) {
							getElement(player).setItem(in.get(), element.getElement());
						} else {
							if (!getElement(player).contains(element.getElement())) {
								getElement(player).addItem(element.getElement());
							}
						}
					}
					for (ItemElement<?> element : items) {
						Optional<Integer> in = element.getSlot();
						in.ifPresent(integer -> getElement(player).setItem(integer, element.getElement()));
					}
				}).run()));
				this.tasks.get(player).repeat(0, 1);

				Schedule.sync(() -> player.openInventory(getElement(player))).waitReal(2);

			} else {

				for (ItemElement<?> element : getPlayer(player).getPage().getAttachment()) {
					Optional<Integer> in = element.getSlot();
					if (in.isPresent()) {
						getElement(player).setItem(in.get(), element.getElement());
					} else {
						if (!getElement(player).contains(element.getElement())) {
							getElement(player).addItem(element.getElement());
						}
					}
				}
				for (ItemElement<?> element : items) {
					Optional<Integer> in = element.getSlot();
					in.ifPresent(integer -> getElement(player).setItem(integer, element.getElement()));
				}

				Schedule.sync(() -> player.openInventory(getElement(player))).run();

			}
		}
	}

	public static class SharedPaginated extends Paginated {

		private final Set<Player> viewers;

		public SharedPaginated(String title, Menu menu) {
			super(title, menu);
			this.viewers = new HashSet<>();
		}

		public Set<Player> getViewers() {
			return viewers;
		}

		@Override
		public synchronized void open(Player player) {
			viewers.add(player);

			if (lazy) {
				// This area dictates that our inventory is "lazy" and needs to be instantiated
				this.inventory = Bukkit.createInventory(null, this.menu.getSize().getSlots(), StringUtils.use(MessageFormat.format(this.title, page, getTotalPages())).translate());
			}
			if (this.menu.getProperties().contains(Menu.Property.LIVE_META)) {
				getElement().setMaxStackSize(1);
				if (this.tasks.containsKey(player)) {
					this.tasks.get(player).cancelTask();
				}

				if (this.tasks.size() < 1) {
					this.tasks.put(player, Schedule.async(() -> {
						Schedule.sync(() -> {
							getElement().clear();
							for (ItemElement<?> element : getGlobalSlot().getAttachment()) {
								Optional<Integer> in = element.getSlot();
								if (in.isPresent()) {
									getElement().setItem(in.get(), element.getElement());
								} else {
									if (!getElement().contains(element.getElement())) {
										getElement().addItem(element.getElement());
									}
								}
							}
							for (ItemElement<?> element : items) {
								Optional<Integer> in = element.getSlot();
								in.ifPresent(integer -> getElement().setItem(integer, element.getElement()));
							}
						}).run();
					}));
					this.tasks.get(player).repeat(0, 1);
				}

				Schedule.sync(() -> {
					SharedPaginated inv = this;
					for (Player p : inv.getViewers()) {
						if (p.equals(player)) {
							Schedule.sync(() -> player.openInventory(getElement())).run();
						} else {
							inv.open(p);
						}
					}
				}).waitReal(2);

				return;
			} else {

				for (ItemElement<?> element : getGlobalSlot().getAttachment()) {
					Optional<Integer> in = element.getSlot();
					if (in.isPresent()) {
						getElement().setItem(in.get(), element.getElement());
					} else {
						if (!getElement().contains(element.getElement())) {
							getElement().addItem(element.getElement());
						}
					}
				}
				for (ItemElement<?> element : items) {
					Optional<Integer> in = element.getSlot();
					in.ifPresent(integer -> getElement().setItem(integer, element.getElement()));
				}

				Schedule.sync(() -> player.openInventory(getElement())).run();

			}

			for (Player p : viewers) {
				if (getElement() != null) {
					if (!p.getOpenInventory().getTopInventory().equals(getElement())) {
						Schedule.sync(() -> viewers.remove(p)).wait(1);
					}
				}
			}
		}
	}

	public static class Shared extends Normal {

		private final Set<Player> viewers;

		public Shared(String title, Menu menu) {
			super(title, menu);
			this.viewers = new HashSet<>();
		}

		public Set<Player> getViewers() {
			return viewers;
		}

		@Override
		public synchronized void open(Player player) {
			viewers.add(player);

			if (lazy && getParent().getProperties().contains(Menu.Property.RECURSIVE)) {
				// This area dictates that our inventory is "lazy" and needs to be instantiated
				this.inventory = Bukkit.createInventory(null, this.menu.getSize().getSlots(), StringUtils.use(MessageFormat.format(this.title, page, getTotalPages())).translate());
			}

			if (this.menu.getProperties().contains(Menu.Property.ANIMATED)) {
				// TODO: setup animation slide stuff
				return;
			}
			if (this.menu.getProperties().contains(Menu.Property.LIVE_META)) {
				if (this.tasks.containsKey(player)) {
					this.tasks.get(player).cancelTask();
				}

				getElement().setMaxStackSize(1);
				if (this.tasks.size() < 1) {
					this.tasks.put(player, Schedule.async(() -> {
						Schedule.sync(() -> {
							getElement().clear();
							for (ItemElement<?> element : getWorkflow()) {
								Optional<Integer> in = element.getSlot();
								if (in.isPresent()) {
									getElement().setItem(in.get(), element.getElement());
								} else {
									if (!getElement().contains(element.getElement())) {
										getElement().addItem(element.getElement());
									}
								}
							}
							for (ItemElement<?> element : items) {
								Optional<Integer> in = element.getSlot();
								in.ifPresent(integer -> getElement().setItem(integer, element.getElement()));
							}
						}).run();
					}));
					this.tasks.get(player).repeat(0, 1);
				}

				Schedule.sync(() -> {
					Shared inv = this;
					for (Player p : inv.getViewers()) {
						if (p.equals(player)) {
							p.openInventory(getElement());
						} else {
							inv.open(player);
						}
					}
				}).waitReal(2);

				return;
			} else {
				for (ItemElement<?> element : getWorkflow()) {
					Optional<Integer> in = element.getSlot();
					if (in.isPresent()) {
						getElement().setItem(in.get(), element.getElement());
					} else {
						if (!getElement().contains(element.getElement())) {
							getElement().addItem(element.getElement());
						}
					}
				}
				for (ItemElement<?> element : items) {
					Optional<Integer> in = element.getSlot();
					in.ifPresent(integer -> getElement().setItem(integer, element.getElement()));
				}

				Schedule.sync(() -> player.openInventory(getElement())).run();
			}

			for (Player p : viewers) {
				if (getElement() != null) {
					if (!p.getOpenInventory().getTopInventory().equals(getElement())) {
						Schedule.sync(() -> viewers.remove(p)).wait(1);
					}
				}
			}
		}
	}

	public static class Normal extends InventoryElement {

		public Normal(String title, Menu menu) {
			super(title, menu, true);
		}

		@Override
		public synchronized void open(Player player) {

			if (lazy && getParent().getProperties().contains(Menu.Property.RECURSIVE)) {
				// This area dictates that our inventory is "lazy" and needs to be instantiated
				this.inventory = Bukkit.createInventory(null, this.menu.getSize().getSlots(), StringUtils.use(MessageFormat.format(this.title, page, getTotalPages())).translate());
				this.invmap.remove(player);
			}

			if (this.menu.getProperties().contains(Menu.Property.ANIMATED)) {
				// TODO: setup animation slide stuff
				return;
			}
			if (this.menu.getProperties().contains(Menu.Property.LIVE_META)) {
				if (this.tasks.containsKey(player)) {
					this.tasks.get(player).cancelTask();
				}

				getElement(player).setMaxStackSize(1);
				this.tasks.put(player, Schedule.async(() -> {
					Schedule.sync(() -> {
						getElement(player).clear();
						for (ItemElement<?> element : getWorkflow()) {
							Optional<Integer> in = element.getSlot();
							if (in.isPresent()) {
								getElement(player).setItem(in.get(), element.getElement());
							} else {
								if (!getElement(player).contains(element.getElement())) {
									getElement(player).addItem(element.getElement());
								}
							}
						}
						for (ItemElement<?> element : items) {
							Optional<Integer> in = element.getSlot();
							in.ifPresent(integer -> getElement(player).setItem(integer, element.getElement()));
						}
					}).run();
				}));
				this.tasks.get(player).repeat(0, 1);

				Schedule.sync(() -> player.openInventory(getElement(player))).waitReal(2);

			} else {
				for (ItemElement<?> element : getWorkflow()) {
					Optional<Integer> in = element.getSlot();
					if (in.isPresent()) {
						getElement(player).setItem(in.get(), element.getElement());
					} else {
						if (!getElement(player).contains(element.getElement())) {
							getElement(player).addItem(element.getElement());
						}
					}
				}
				for (ItemElement<?> element : items) {
					Optional<Integer> in = element.getSlot();
					in.ifPresent(integer -> getElement(player).setItem(integer, element.getElement()));
				}

				Schedule.sync(() -> player.openInventory(getElement(player))).run();
			}
		}
	}

	public static class Printable extends InventoryElement {

		private final AnvilMechanics nms;

		private int containerId;

		private boolean visible;

		public Printable(String title, AnvilMechanics mechanics, Menu menu) {
			super(title, menu, true);
			this.nms = mechanics;
		}

		public boolean isVisible() {
			return visible;
		}

		@Override
		public void open(Player player) {
			nms.handleInventoryCloseEvent(player);
			nms.setActiveContainerDefault(player);

			final Object container = nms.newContainerAnvil(player, this.getTitle());

			setElement(nms.toBukkitInventory(container));

			for (ItemElement<?> it : getAttachment()) {
				if (it.getSlot().isPresent()) {
					int slot = it.getSlot().get();
					if (slot == 0) {
						getElement().setItem(0, it.getElement());
					}
					if (slot == 1) {
						getElement().setItem(1, it.getElement());
					}
					if (slot == 2) {
						getElement().setItem(2, it.getElement());
					}
				}
			}

			containerId = nms.getNextContainerId(player, container);
			nms.sendPacketOpenWindow(player, containerId, this.getTitle());
			nms.setActiveContainer(player, container);
			nms.setActiveContainerId(container, containerId);
			nms.addActiveContainerSlotListener(container, player);

			visible = true;
		}

		public void close(Player player, boolean sendPacket) {
			if (!visible)
				throw new IllegalArgumentException("You can't close an inventory that isn't open!");
			visible = false;

			if (!sendPacket) {
				nms.handleInventoryCloseEvent(player);
			}
			nms.setActiveContainerDefault(player);
			nms.sendPacketCloseWindow(player, containerId);
		}

	}
}
