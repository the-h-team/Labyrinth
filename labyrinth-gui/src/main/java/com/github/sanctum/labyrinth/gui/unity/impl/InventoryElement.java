package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.data.service.AnvilMechanics;
import com.github.sanctum.labyrinth.formatting.PaginatedList;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Asynchronous;
import com.github.sanctum.labyrinth.task.Schedule;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class InventoryElement extends Menu.Element<Inventory, Set<ItemElement<?>>> {

	protected final Map<Player, Asynchronous> tasks;
	protected final Set<MenuViewer> index;
	protected final Set<ItemElement<?>> items;
	protected final boolean lazy;
	protected final String title;
	protected final Menu menu;
	protected Inventory inventory;
	protected int limit = 5;
	protected int page = 1;

	public InventoryElement(String title, Menu menu, boolean lazy) {
		this.items = new HashSet<>();
		this.menu = menu;
		this.tasks = new HashMap<>();
		this.index = new HashSet<>();
		this.title = title;
		this.lazy = lazy;
	}

	public synchronized void open(Player player) {
	}

	@Override
	public Inventory getElement() {

		if (this.inventory == null) {
			this.inventory = Bukkit.createInventory(null, menu.getSize().getSize(), StringUtils.use(MessageFormat.format(this.title, page, 0)).translate());
		}

		if (this.menu.getProperties().contains(Menu.Property.REFILLABLE)) {
			if (UniformedComponents.accept(Arrays.asList(this.inventory.getContents())).filter(i -> i != null).count() == 0) {
				if (isPaginated()) {
					BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
					if (border != null) {
						for (ItemElement<?> element : border.getAttachment()) {
							Optional<Integer> i = element.getSlot();
							i.ifPresent(integer -> this.inventory.setItem(integer, element.getElement()));
						}
					}
					ListElement<?> list = (ListElement<?>) getElement(e -> e instanceof ListElement);
					if (list == null) return this.inventory;
					for (ItemElement<?> element : new PaginatedList<>(getWorkflow()).limit(list.getLimit()).compare(list.comparator).filter(list.predicate).get(page)) {
						if (!this.inventory.contains(element.getElement())) {
							this.inventory.addItem(element.getElement());
						}
					}
					for (ItemElement<?> element : items) {
						Optional<Integer> i = element.getSlot();
						i.ifPresent(integer -> this.inventory.setItem(integer, element.getElement()));
					}
					FillerElement<?> filler = (FillerElement<?>) getElement(e -> e instanceof FillerElement);
					if (filler != null) {
						for (ItemElement<?> el : filler.getAttachment()) {
							int slot = el.getSlot().orElse(0);
							if (this.inventory.getItem(slot) == null) {
								this.inventory.setItem(slot, el.getElement());
							}
						}
					}
				} else {
					BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
					if (border != null) {
						for (ItemElement<?> element : border.getAttachment()) {
							Optional<Integer> i = element.getSlot();
							i.ifPresent(integer -> this.inventory.setItem(integer, element.getElement()));
						}
					}
					for (ItemElement<?> element : getWorkflow()) {
						if (!this.inventory.contains(element.getElement())) {
							this.inventory.addItem(element.getElement());
						}
					}
					for (ItemElement<?> element : items) {
						Optional<Integer> i = element.getSlot();
						i.ifPresent(integer -> this.inventory.setItem(integer, element.getElement()));
					}
					FillerElement<?> filler = (FillerElement<?>) getElement(e -> e instanceof FillerElement);
					if (filler != null) {
						for (ItemElement<?> el : filler.getAttachment()) {
							int slot = el.getSlot().orElse(0);
							if (this.inventory.getItem(slot) == null) {
								this.inventory.setItem(slot, el.getElement());
							}
						}
					}
				}
			}
		}
		return this.inventory;
	}

	public @Nullable Asynchronous getTask(Player player) {
		return tasks.get(player);
	}

	/**
	 * Get a players page positioning.
	 * <p>
	 * Only to be used when {@link com.github.sanctum.labyrinth.gui.unity.construct.Menu.Property#SHAREABLE} isn't present.
	 *
	 * @param player The player to use.
	 * @return A paged player.
	 */
	public MenuViewer getViewer(Player player) {
		MenuViewer pl = this.index.stream().filter(p -> player.getName().equals(p.getPlayer().getName())).findFirst().orElse(null);
		if (pl == null) {
			pl = new MenuViewer(player.getUniqueId(), this);
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
			ListElement<?> list = (ListElement<?>) getElement(e -> e instanceof ListElement);
			if (list != null) {
				items.addAll(list.getAttachment());
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

	public @Nullable ItemElement<?> getItem(Predicate<ItemElement<?>> item) {
		ListElement<?> list = (ListElement<?>) getElement(e -> e instanceof ListElement);
		BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
		FillerElement<?> filler = (FillerElement<?>) getElement(e -> e instanceof FillerElement);
		if (isPaginated()) {
			return this.items.stream().filter(item).findFirst().orElse(list == null ? null : list.getAttachment().stream().filter(item).findFirst().orElse(border == null ? null : border.getAttachment().stream().filter(item).findFirst().orElse(filler == null ? null : filler.getAttachment().stream().filter(item).findFirst().orElse(null))));
		} else {
			return this.items.stream().filter(item).findFirst().orElse(border == null ? null : border.getAttachment().stream().filter(item).findFirst().orElse(filler == null ? null : filler.getAttachment().stream().filter(item).findFirst().orElse(null)));
		}
	}

	public @Nullable ItemElement<?> getItem(ItemStack item) {
		ListElement<?> list = (ListElement<?>) getElement(e -> e instanceof ListElement);
		BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
		FillerElement<?> filler = (FillerElement<?>) getElement(e -> e instanceof FillerElement);
		return this.items.stream().filter(i -> {
			if (getParent().getProperties().contains(Menu.Property.LIVE_META)) {
				return isSimilar(item, i.getElement());
			} else return i.getElement().isSimilar(item);
		}).findFirst().orElse(list == null ? null : list.getAttachment().stream().filter(i -> {
			if (getParent().getProperties().contains(Menu.Property.LIVE_META)) {
				return isSimilar(item, i.getElement());
			} else return i.getElement().isSimilar(item);
		}).findFirst().orElse(border == null ? null : border.getAttachment().stream().filter(i -> i.getElement().isSimilar(item)).findFirst().orElse(filler == null ? null : filler.getAttachment().stream().filter(i -> {
			if (getParent().getProperties().contains(Menu.Property.LIVE_META)) {
				return isSimilar(item, i.getElement());
			} else return i.getElement().isSimilar(item);
		}).findFirst().orElse(null))));
	}

	public @Nullable ItemElement<?> getItem(int slot) {
		for (ItemElement<?> it : getAttachment()) {
			if (it.getSlot().map(s -> s == slot).orElse(false)) {
				return it;
			}
		}
		BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
		FillerElement<?> filler = (FillerElement<?>) getElement(e -> e instanceof FillerElement);
		if (border != null) {
			for (ItemElement<?> it : border.getAttachment()) {
				if (it.getSlot().map(s -> s == slot).orElse(false)) {
					return it;
				}
			}
		}
		if (filler != null) {
			for (ItemElement<?> it : filler.getAttachment()) {
				if (it.getSlot().map(s -> s == slot).orElse(false)) {
					return it;
				}
			}
		}
		return null;
	}

	public boolean contains(ItemStack item) {
		ListElement<?> list = (ListElement<?>) getElement(e -> e instanceof ListElement);
		return this.items.stream().map(ItemElement::getElement).anyMatch(i -> {
			if (getParent().getProperties().contains(Menu.Property.LIVE_META)) {
				return isSimilar(item, i);
			} else return i.isSimilar(item);
		}) || list != null && list.getAttachment().stream().map(ItemElement::getElement).anyMatch(i -> {
			if (getParent().getProperties().contains(Menu.Property.LIVE_META)) {
				return isSimilar(item, i);
			} else return i.isSimilar(item);
		});
	}

	public boolean isSimilar(@Nullable ItemStack stack, @Nullable ItemStack stack2) {
		if (stack == null) {
			return false;
		}
		if (stack == stack2) {
			return true;
		}
		Material comparisonType = (stack2.getType().isLegacy()) ? Bukkit.getUnsafe().fromLegacy(stack2.getData(), true) : stack2.getType();
		return comparisonType == stack.getType() && stack2.getDurability() == stack.getDurability() && stack2.hasItemMeta() == stack.hasItemMeta() && (Objects.equals(stack2.getItemMeta().getDisplayName(), stack.getItemMeta().getDisplayName()));
	}

	public InventoryElement setElement(Inventory inventory) {
		this.inventory = inventory;
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
			getViewer(target).getElement().remove(item.getElement());
		}
		return this;
	}

	public <R> InventoryElement addItem(ItemElement<R> item) {
		this.items.add(item.setParent(this));
		return this;
	}

	public <R> InventoryElement addItem(BorderElement<R> element) {
		addElement(element);
		return this;
	}

	public <R> InventoryElement addItem(FillerElement<R> element) {
		addElement(element);
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
		addElement(element.setParent(this));
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

		private PaginatedList<ItemElement<?>> pagination;

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

		private ListElement<?> init() {
			ListElement<?> list = (ListElement<?>) element.getElement(e -> e instanceof ListElement);
			if (list != null) {
				if (this.pagination == null) {
					this.pagination = new PaginatedList<>(element.getWorkflow()).limit(list.getLimit()).compare(list.comparator).filter(list.predicate);
				}
			}
			return list;
		}

		@Override
		public Set<ItemElement<?>> getAttachment() {
			ListElement<?> list = init();
			if (list == null) return new HashSet<>();
			Set<ItemElement<?>> set = new HashSet<>(pagination.update(element.getWorkflow()).compare(list.comparator).get(toNumber())).stream().sorted(list.comparator).collect(Collectors.toCollection(LinkedHashSet::new));
			for (ItemElement<?> it : getElement().getAttachment()) {
				if (!set.contains(it) && it.isPlayerAdded() && it.getPage().toNumber() == toNumber()) {
					set.add(it);
				}
			}
			if (set.size() >= list.getLimit()) {
				this.full = true;
			}
			return set;
		}
	}

	public static class Animated extends InventoryElement {

		public Animated(String title, Menu menu) {
			super(title, menu, true);
		}



	}

	public static class Paginated extends InventoryElement {

		public Paginated(String title, Menu menu) {
			super(title, menu, true);
		}

		public Paginated setGlobalSlot(int page) {
			this.page = page;
			return this;
		}

		public Page getPage(int page) {
			if (getElement(e -> e instanceof Page && ((Page) e).toNumber() == page) != null) {
				return (Page) getElement(e -> e instanceof Page && ((Page) e).toNumber() == page);
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

		@Override
		public synchronized void open(Player player) {

			if (lazy) {
				// This area dictates that our inventory is "lazy" and needs to be instantiated
				getViewer(player).setElement(null);
			}
			if (this.menu.getProperties().contains(Menu.Property.LIVE_META)) {
				getViewer(player).getElement().setMaxStackSize(1);
				if (this.tasks.containsKey(player)) {
					this.tasks.get(player).cancelTask();
				}

				this.tasks.put(player, Schedule.async(() -> Schedule.sync(() -> {
					getViewer(player).getElement().clear();
					for (ItemElement<?> element : getViewer(player).getPage().getAttachment()) {
						if (!getViewer(player).getElement().contains(element.getElement())) {
							getViewer(player).getElement().addItem(element.getElement());
						}
					}
					for (ItemElement<?> element : items) {
						Optional<Integer> in = element.getSlot();
						in.ifPresent(integer -> getViewer(player).getElement().setItem(integer, element.getElement()));
					}
				}).run()));
				this.tasks.get(player).repeat(0, 1);

				Schedule.sync(() -> player.openInventory(getViewer(player).getElement())).waitReal(2);

			} else {

				for (ItemElement<?> element : getViewer(player).getPage().getAttachment()) {
					if (!getViewer(player).getElement().contains(element.getElement())) {
						getViewer(player).getElement().addItem(element.getElement());
					}
				}
				for (ItemElement<?> element : items) {
					Optional<Integer> in = element.getSlot();
					in.ifPresent(integer -> getViewer(player).getElement().setItem(integer, element.getElement()));
				}

				player.openInventory(getViewer(player).getElement());

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
				this.inventory = Bukkit.createInventory(null, this.menu.getSize().getSize(), StringUtils.use(MessageFormat.format(this.title, page, getTotalPages())).translate());
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
								if (!getElement().contains(element.getElement())) {
									getElement().addItem(element.getElement());
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
			} else {

				for (ItemElement<?> element : getGlobalSlot().getAttachment()) {
					if (!getElement().contains(element.getElement())) {
						getElement().addItem(element.getElement());
					}
				}
				for (ItemElement<?> element : items) {
					Optional<Integer> in = element.getSlot();
					in.ifPresent(integer -> getElement().setItem(integer, element.getElement()));
				}

				player.openInventory(getElement());

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
				this.inventory = Bukkit.createInventory(null, this.menu.getSize().getSize(), StringUtils.use(MessageFormat.format(this.title, page, 0)).translate());
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
							BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
							if (border != null) {
								for (ItemElement<?> element : border.getAttachment()) {
									Optional<Integer> i = element.getSlot();
									i.ifPresent(integer -> inventory.setItem(integer, element.getElement()));
								}
							}
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
							FillerElement<?> filler = (FillerElement<?>) getElement(e -> e instanceof FillerElement);
							if (filler != null) {
								for (ItemElement<?> el : filler.getAttachment()) {
									int slot = el.getSlot().orElse(0);
									if (getElement().getItem(slot) == null) {
										getElement().setItem(slot, el.getElement());
									}
								}
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
				BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
				if (border != null) {
					for (ItemElement<?> element : border.getAttachment()) {
						Optional<Integer> i = element.getSlot();
						i.ifPresent(integer -> inventory.setItem(integer, element.getElement()));
					}
				}
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
				FillerElement<?> filler = (FillerElement<?>) getElement(e -> e instanceof FillerElement);
				if (filler != null) {
					for (ItemElement<?> el : filler.getAttachment()) {
						int slot = el.getSlot().orElse(0);
						if (getElement().getItem(slot) == null) {
							getElement().setItem(slot, el.getElement());
						}
					}
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
				this.inventory = Bukkit.createInventory(null, this.menu.getSize().getSize(), StringUtils.use(MessageFormat.format(this.title, page, 0)).translate());
				getViewer(player).setElement(null);
			}

			if (this.menu.getProperties().contains(Menu.Property.ANIMATED)) {
				// TODO: setup animation slide stuff
				return;
			}
			if (this.menu.getProperties().contains(Menu.Property.LIVE_META)) {
				if (this.tasks.containsKey(player)) {
					this.tasks.get(player).cancelTask();
				}

				getViewer(player).getElement().setMaxStackSize(1);
				this.tasks.put(player, Schedule.async(() -> {
					Schedule.sync(() -> {
						getViewer(player).getElement().clear();
						BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
						if (border != null) {
							for (ItemElement<?> element : border.getAttachment()) {
								Optional<Integer> i = element.getSlot();
								i.ifPresent(integer -> getViewer(player).getElement().setItem(integer, element.getElement()));
							}
						}
						for (ItemElement<?> element : getWorkflow()) {
							Optional<Integer> in = element.getSlot();
							if (in.isPresent()) {
								getViewer(player).getElement().setItem(in.get(), element.getElement());
							} else {
								if (!getViewer(player).getElement().contains(element.getElement())) {
									getViewer(player).getElement().addItem(element.getElement());
								}
							}
						}
						for (ItemElement<?> element : items) {
							Optional<Integer> in = element.getSlot();
							in.ifPresent(integer -> getViewer(player).getElement().setItem(integer, element.getElement()));
						}
						FillerElement<?> filler = (FillerElement<?>) getElement(e -> e instanceof FillerElement);
						if (filler != null) {
							for (ItemElement<?> el : filler.getAttachment()) {
								int slot = el.getSlot().orElse(0);
								if (getViewer(player).getElement().getItem(slot) == null) {
									getViewer(player).getElement().setItem(slot, el.getElement());
								}
							}
						}
					}).run();
				}));
				this.tasks.get(player).repeat(0, 1);

				Schedule.sync(() -> player.openInventory(getViewer(player).getElement())).waitReal(2);

			} else {
				BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
				if (border != null) {
					for (ItemElement<?> element : border.getAttachment()) {
						Optional<Integer> i = element.getSlot();
						i.ifPresent(integer -> getViewer(player).getElement().setItem(integer, element.getElement()));
					}
				}
				for (ItemElement<?> element : getWorkflow()) {
					Optional<Integer> in = element.getSlot();
					if (in.isPresent()) {
						getViewer(player).getElement().setItem(in.get(), element.getElement());
					} else {
						if (!getViewer(player).getElement().contains(element.getElement())) {
							getViewer(player).getElement().addItem(element.getElement());
						}
					}
				}
				for (ItemElement<?> element : items) {
					Optional<Integer> in = element.getSlot();
					in.ifPresent(integer -> getViewer(player).getElement().setItem(integer, element.getElement()));
				}
				FillerElement<?> filler = (FillerElement<?>) getElement(e -> e instanceof FillerElement);
				if (filler != null) {
					for (ItemElement<?> el : filler.getAttachment()) {
						int slot = el.getSlot().orElse(0);
						if (getViewer(player).getElement().getItem(slot) == null) {
							getViewer(player).getElement().setItem(slot, el.getElement());
						}
					}
				}
				Schedule.sync(() -> player.openInventory(getViewer(player).getElement())).run();
			}
		}
	}

	public static class Printable extends InventoryElement {

		private final AnvilMechanics nms;

		private int containerId;

		private boolean visible;

		public Printable(String title, AnvilMechanics mechanics, Menu menu) {
			super(StringUtils.use(title).translate(), menu, true);
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
