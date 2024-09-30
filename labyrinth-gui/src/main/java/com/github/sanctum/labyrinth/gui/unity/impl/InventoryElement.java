package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.service.AnvilMechanics;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.library.Mailer;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.BukkitTaskPredicate;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.container.ImmutablePantherMap;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherEntry;
import com.github.sanctum.panther.container.PantherEntryMap;
import com.github.sanctum.panther.container.PantherMap;
import com.github.sanctum.panther.container.PantherSet;
import com.github.sanctum.panther.util.AbstractPaginatedCollection;
import com.github.sanctum.panther.util.SpecialID;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class InventoryElement extends Menu.Element<Inventory, Set<ItemElement<?>>> {

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
		this.index = new HashSet<>();
		this.title = title;
		this.lazy = lazy;
	}

	public synchronized void open(Player player) {}

	public synchronized void close(Player player) {}

	@Override
	public Inventory getElement() {

		if (this.inventory == null) {
			this.inventory = Bukkit.createInventory(Menu.Instance.of(menu), menu.getSize().getSize(), StringUtils.use(MessageFormat.format(this.title, page, 0)).translate());
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
					for (ItemElement<?> element : AbstractPaginatedCollection.of(getContents()).limit(list.getLimit()).sort(list.comparator).filter(list.predicate).get(page)) {
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
					for (ItemElement<?> element : getContents()) {
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

	public InventoryElement setContents(Iterable<ItemElement<?>> elements) {
		items.clear();
		elements.forEach(this::addItem);
		return this;
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

	public Set<ItemElement<?>> getContents() {
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
		if (isAnimated()) {
			Animated animated = (Animated) this;
			for (Slide s : animated.getSlides()) {
				for (ItemElement<?> i : s.getAttachment().values()) {
					if (item.test(i)) return i;
				}
			}
		}
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
		boolean check = getParent().getProperties().contains(Menu.Property.LIVE_META) || getParent().getProperties().contains(Menu.Property.ANIMATED);
		if (isAnimated()) {
			Animated animated = (Animated) this;
			for (Slide s : animated.getSlides()) {
				for (ItemElement<?> i : s.getAttachment().values()) {
					if (isSimilar(i.getElement(), item)) return i;
				}
			}
		}
		return this.items.stream().filter(i -> {
			if (check) {
				return isSimilar(item, i.getElement());
			} else return i.getElement().isSimilar(item);
		}).findFirst().orElse(list == null ? null : list.getAttachment().stream().filter(i -> {
			if (check) {
				return isSimilar(item, i.getElement());
			} else return i.getElement().isSimilar(item);
		}).findFirst().orElse(border == null ? null : border.getAttachment().stream().filter(i -> i.getElement().isSimilar(item)).findFirst().orElse(filler == null ? null : filler.getAttachment().stream().filter(i -> {
			if (check) {
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
		if (isAnimated()) {
			Animated inv = (Animated) this;
			for (Slide s : inv.getSlides()) {
				for (ItemElement<?> item : s.getAttachment().values()) {
					if (item.getSlot().isPresent() && item.getSlot().get() == slot) return item;
				}
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
		if (isAnimated()) {
			Animated inv = (Animated) this;
			if (inv.getSlides().stream().anyMatch(s -> s.getAttachment().values().stream().anyMatch(it -> isSimilar(it.getElement(), item))))
				return true;
		}
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

	public boolean isAnimated() {
		return this instanceof Animated;
	}

	/*
	 * Element section \/
	 */
	public static class Slide extends Menu.Element<InventoryElement, PantherMap<Integer, ItemElement<?>>> {

		private final PantherMap<Integer, ItemElement<?>> items = new PantherEntryMap<>();
		private final InventoryElement parent;
		private long changeInterval;

		public Slide(InventoryElement parent) {
			this.parent = parent;
		}

		public Slide set(int slot, ItemElement<?> itemElement) throws IndexOutOfBoundsException {
			if (slot >= parent.inventory.getSize() || slot < 0)
				throw new IndexOutOfBoundsException("Cannot modify item element beyond natural scope.");
			items.put(slot, itemElement.setSlot(slot).setParent(parent));
			return this;
		}

		public Slide setNextDelay(long changeInterval) {
			this.changeInterval = changeInterval;
			return this;
		}

		public long getChangeInterval() {
			return changeInterval;
		}

		@Override
		public InventoryElement getElement() {
			return parent;
		}

		@Override
		public PantherMap<Integer, ItemElement<?>> getAttachment() {
			return ImmutablePantherMap.of(items);
		}
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
			ListElement<?> list = (ListElement<?>) element.getElement(e -> e instanceof ListElement);
			if (list == null) return new HashSet<>();

			Set<ItemElement<?>> set = StreamSupport.stream(AbstractPaginatedCollection.of(element.getContents()).limit(list.getLimit()).sort(list.comparator).get(toNumber()).spliterator(), false).sorted(list.comparator).collect(Collectors.toCollection(LinkedHashSet::new));
			for (ItemElement<?> extra : getElement().getAttachment()) {
				if (!set.contains(extra) && extra.isPlayerAdded() && extra.getPage().toNumber() == toNumber()) {
					set.add(extra);
				}
			}
			if (set.size() >= list.getLimit()) {
				this.full = true;
			}
			return set;
		}
	}

	/*
	 * Inventory section below \/
	 */


	public static class Animated extends InventoryElement {

		private final PantherMap<Integer, Slide> slides = new PantherEntryMap<>();
		private BukkitTaskPredicate<?>[] predicates = new BukkitTaskPredicate<?>[0];
		private long repeat;

		public Animated(String title, Menu menu) {
			super(title, menu, true);
		}

		public Animated addItem(Slide element) {
			slides.put(slides.size(), element);
			return this;
		}

		public Animated setRepeat(long repeat) {
			this.repeat = repeat;
			return this;
		}

		public Animated setPredicates(BukkitTaskPredicate<?>... predicates) {
			this.predicates = predicates;
			return this;
		}

		public long getRepeat() {
			return repeat;
		}

		public PantherCollection<Slide> getSlides() {
			return slides.values();
		}

		@Override
		public synchronized void close(Player player) {
			player.closeInventory();
		}

		@Override
		public synchronized void open(Player player) {
			TaskScheduler.of(() -> {
				slides.stream().sorted(Comparator.comparingInt(PantherEntry.Modifiable::getKey)).forEach(entry -> {
					TaskScheduler.of(() -> {
						getElement().clear();
						BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
						if (border != null) {
							for (ItemElement<?> element : border.getAttachment()) {
								Optional<Integer> i = element.getSlot();
								i.ifPresent(integer -> inventory.setItem(integer, element.getElement()));
							}
						}
						for (ItemElement<?> element : getContents()) {
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
						entry.getValue().getAttachment().forEach(entry2 -> getElement().setItem(entry2.getKey(), entry2.getValue().getElement()));
					}).scheduleLater(entry.getValue().changeInterval);
				});
			}).scheduleTimerAsync("Labyrinth:" + getParent().hashCode() + ";slide-" + player.getUniqueId(), 0, getRepeat(), predicates).next(() -> player.openInventory(getElement())).scheduleLater(2L);
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
			if ((getContents().size() % this.limit) == 0) {
				if (getContents().size() > 0) {
					totalPageCount = getContents().size() / this.limit;
				}
			} else {
				totalPageCount = (getContents().size() / this.limit) + 1;
			}
			return totalPageCount;
		}

		@Override
		public synchronized void close(Player player) {
			player.closeInventory();
		}

		@Override
		public synchronized void open(Player player) {
			MenuViewer viewer = getViewer(player);
			if (lazy) {
				// This area dictates that our inventory is "lazy" and needs to be instantiated
				viewer.setElement(null);
			}
			if (this.menu.getProperties().contains(Menu.Property.LIVE_META)) {
				viewer.getElement().setMaxStackSize(1);

				if (viewer.getTask() != null) {
					viewer.getTask().getTask().cancel();
				}

				viewer.setTask(TaskScheduler.of(() -> {
					viewer.getElement().clear();
					BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
					if (border != null) {
						for (ItemElement<?> element : border.getAttachment()) {
							Optional<Integer> i = element.getSlot();
							i.ifPresent(integer -> viewer.getElement().setItem(integer, element.getElement()));
						}
					}
					for (ItemElement<?> element : viewer.getPage().getAttachment()) {
						if (!viewer.getElement().contains(element.getElement())) {
							viewer.getElement().addItem(element.getElement());
						}
					}
					for (ItemElement<?> element : items) {
						Optional<Integer> in = element.getSlot();
						in.ifPresent(integer -> viewer.getElement().setItem(integer, element.getElement()));
					}
					FillerElement<?> filler = (FillerElement<?>) getElement(e -> e instanceof FillerElement);
					if (filler != null) {
						for (ItemElement<?> el : filler.getAttachment()) {
							int slot = el.getSlot().orElse(0);
							if (viewer.getElement().getItem(slot) == null) {
								viewer.getElement().setItem(slot, el.getElement());
							}
						}
					}
				}).scheduleTimer("Unity:" + SpecialID.builder().setLength(12).build(this) + ":" + player.getUniqueId(), 0, 60));

				TaskScheduler.of(() -> player.openInventory(viewer.getElement())).schedule();

			} else {

				for (ItemElement<?> element : viewer.getPage().getAttachment()) {
					if (!viewer.getElement().contains(element.getElement())) {
						viewer.getElement().addItem(element.getElement());
					}
				}
				for (ItemElement<?> element : items) {
					Optional<Integer> in = element.getSlot();
					in.ifPresent(integer -> viewer.getElement().setItem(integer, element.getElement()));
				}

				player.openInventory(viewer.getElement());

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
		public synchronized void close(Player player) {
			player.closeInventory();
		}

		@Override
		public synchronized void open(Player player) {
			viewers.add(player);
			MenuViewer viewer = getViewer(player);
			if (lazy) {
				// This area dictates that our inventory is "lazy" and needs to be instantiated
				this.inventory = Bukkit.createInventory(Menu.Instance.of(menu), this.menu.getSize().getSize(), StringUtils.use(MessageFormat.format(this.title, page, getTotalPages())).translate());
			}
			if (this.menu.getProperties().contains(Menu.Property.LIVE_META)) {
				getElement().setMaxStackSize(1);
				if (viewer.getTask() != null) {
					viewer.getTask().getTask().cancel();
				}
				if (getViewers().stream().map(this::getViewer).noneMatch(m -> m.getTask() != null)) {
					viewer.setTask(TaskScheduler.of(() -> {
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
					}).scheduleTimer("Unity:" + SpecialID.builder().setLength(12).build(this) + ":" + player.getUniqueId(), 0, 60));
				}

				TaskScheduler.of(() -> {
					SharedPaginated inv = this;
					for (Player p : inv.getViewers()) {
						if (p.equals(player)) {
							TaskScheduler.of(() -> player.openInventory(getElement())).schedule();
						} else {
							inv.open(p);
						}
					}
				}).scheduleLater(2);
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
		public synchronized void close(Player player) {
			player.closeInventory();
		}

		@Override
		public synchronized void open(Player player) {
			viewers.add(player);
			MenuViewer viewer = getViewer(player);
			if (lazy && getParent().getProperties().contains(Menu.Property.RECURSIVE)) {
				// This area dictates that our inventory is "lazy" and needs to be instantiated
				this.inventory = Bukkit.createInventory(Menu.Instance.of(menu), this.menu.getSize().getSize(), StringUtils.use(MessageFormat.format(this.title, page, 0)).translate());
			}

			if (this.menu.getProperties().contains(Menu.Property.ANIMATED)) {
				// TODO: setup animation slide stuff
				return;
			}
			if (this.menu.getProperties().contains(Menu.Property.LIVE_META)) {
				if (viewer.getTask() != null) {
					viewer.getTask().getTask().cancel();
				}

				getElement().setMaxStackSize(1);
				if (getViewers().stream().map(this::getViewer).noneMatch(m -> m.getTask() != null)) {
					viewer.setTask(TaskScheduler.of(() -> {
						getElement().clear();
						BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
						if (border != null) {
							for (ItemElement<?> element : border.getAttachment()) {
								Optional<Integer> i = element.getSlot();
								i.ifPresent(integer -> inventory.setItem(integer, element.getElement()));
							}
						}
						for (ItemElement<?> element : getContents()) {
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
					}).scheduleTimer("Unity:" + SpecialID.builder().setLength(12).build(this) + ":" + player.getUniqueId(), 0, 1));
				}

				TaskScheduler.of(() -> {
					Shared inv = this;
					for (Player p : inv.getViewers()) {
						if (p.equals(player)) {
							p.openInventory(getElement());
						} else {
							inv.open(player);
						}
					}
				}).scheduleLater(2);

				return;
			} else {
				BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
				if (border != null) {
					for (ItemElement<?> element : border.getAttachment()) {
						Optional<Integer> i = element.getSlot();
						i.ifPresent(integer -> inventory.setItem(integer, element.getElement()));
					}
				}
				for (ItemElement<?> element : getContents()) {
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

				player.openInventory(getElement());
			}

			for (Player p : viewers) {
				if (getElement() != null) {
					if (!p.getOpenInventory().getTopInventory().equals(getElement())) {
						TaskScheduler.of(() -> viewers.remove(p)).scheduleLater(1);
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
		public synchronized void close(Player player) {
			player.closeInventory();
		}

		@Override
		public synchronized void open(Player player) {
			MenuViewer viewer = getViewer(player);
			if (lazy && getParent().getProperties().contains(Menu.Property.RECURSIVE)) {
				// This area dictates that our inventory is "lazy" and needs to be instantiated
				this.inventory = Bukkit.createInventory(Menu.Instance.of(menu), this.menu.getSize().getSize(), StringUtils.use(MessageFormat.format(this.title, page, 0)).translate());
				viewer.setElement(null);
			}
			if (this.menu.getProperties().contains(Menu.Property.LIVE_META)) {
				if (viewer.getTask() != null) {
					viewer.getTask().getTask().cancel();
				}

				viewer.getElement().setMaxStackSize(1);
				viewer.setTask(TaskScheduler.of(() -> {
					viewer.getElement().clear();
					BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
					if (border != null) {
						for (ItemElement<?> element : border.getAttachment()) {
							Optional<Integer> i = element.getSlot();
							i.ifPresent(integer -> viewer.getElement().setItem(integer, element.getElement()));
						}
					}
					for (ItemElement<?> element : getContents()) {
						Optional<Integer> in = element.getSlot();
						if (in.isPresent()) {
							viewer.getElement().setItem(in.get(), element.getElement());
						} else {
							if (!viewer.getElement().contains(element.getElement())) {
								viewer.getElement().addItem(element.getElement());
							}
						}
					}
					for (ItemElement<?> element : items) {
						Optional<Integer> in = element.getSlot();
						in.ifPresent(integer -> viewer.getElement().setItem(integer, element.getElement()));
					}
					FillerElement<?> filler = (FillerElement<?>) getElement(e -> e instanceof FillerElement);
					if (filler != null) {
						for (ItemElement<?> el : filler.getAttachment()) {
							int slot = el.getSlot().orElse(0);
							if (viewer.getElement().getItem(slot) == null) {
								viewer.getElement().setItem(slot, el.getElement());
							}
						}
					}
				}).scheduleTimer("Unity:" + SpecialID.builder().setLength(12).build(this) + ":" + player.getUniqueId(), 0, 1));

			} else {
				BorderElement<?> border = (BorderElement<?>) getElement(e -> e instanceof BorderElement);
				if (border != null) {
					for (ItemElement<?> element : border.getAttachment()) {
						Optional<Integer> i = element.getSlot();
						i.ifPresent(integer -> viewer.getElement().setItem(integer, element.getElement()));
					}
				}
				for (ItemElement<?> element : getContents()) {
					Optional<Integer> in = element.getSlot();
					if (in.isPresent()) {
						viewer.getElement().setItem(in.get(), element.getElement());
					} else {
						if (!viewer.getElement().contains(element.getElement())) {
							viewer.getElement().addItem(element.getElement());
						}
					}
				}
				for (ItemElement<?> element : items) {
					Optional<Integer> in = element.getSlot();
					in.ifPresent(integer -> viewer.getElement().setItem(integer, element.getElement()));
				}
				FillerElement<?> filler = (FillerElement<?>) getElement(e -> e instanceof FillerElement);
				if (filler != null) {
					for (ItemElement<?> el : filler.getAttachment()) {
						int slot = el.getSlot().orElse(0);
						if (viewer.getElement().getItem(slot) == null) {
							viewer.getElement().setItem(slot, el.getElement());
						}
					}
				}
			}
			player.openInventory(viewer.getElement());
		}
	}

	public static class Printable extends InventoryElement {

		private final AnvilMechanics nms;
		private final AnvilGUI.Builder builder;
		private AnvilGUI gui = null;

		private int containerId;

		private final PantherCollection<Player> visible = new PantherSet<>();

		public Printable(String title, AnvilMechanics mechanics, Menu menu) {
			super(StringUtils.use(title).translate(), menu, true);
			this.nms = mechanics;
			this.builder = null;
		}

		public Printable(String title, AnvilGUI.Builder builder, Menu menu) {
			super(StringUtils.use(title).translate(), menu, true);
			this.nms = null;
			this.builder = builder;
		}

		public boolean isVisible(Player player) {
			return visible.contains(player);
		}

		@Override
		public synchronized void close(Player player) {
			close(player, true);
		}

		public void close(Player player, boolean sendPacket) {
			if (!visible.contains(player)) return;
			visible.remove(player);
			if (nms != null) {
				AnvilMechanics.Container container = nms.getContainer(player);
				if (container != null) container.close(player);
			} else {
				if (this.gui != null) {
					this.gui.closeInventory();
				} else {
					// TODO: input warning message
				}
			}
		}

		@Override
		public void open(Player player) {
			if (this.builder == null && nms == null) {
				Mailer mailer = Mailer.empty(player).prefix().start("&7[").middle("&2&lLabyrinth").end("&7]").finish();
				String reason = LabyrinthProvider.getInstance().isModded() ? "Modded Environment" : "Missing version support";
				mailer.chat("&c&lAn internal matter has prevented you from accessing this menu.").deploy(m -> {
					if (player.isOp()) {
						mailer.chat("&eReason: &f" + reason).queue();
					}
				});
				player.closeInventory();
				return;
			}

			if (this.builder == null) {
				final AnvilMechanics.Container container = nms.newContainer(player, this.getTitle(), true);

				setElement(container.getBukkitInventory());
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

				container.open(player);
			} else {
				for (ItemElement<?> it : getAttachment()) {
					if (it.getSlot().isPresent()) {
						int slot = it.getSlot().get();
						if (slot == 0) {
							builder.itemLeft(it.getElement());
						}
						if (slot == 1) {
							builder.itemRight(it.getElement());
						}
					}
				}
				builder.onClick((integer, stateSnapshot) -> Collections.emptyList());
				this.gui = builder.open(player);
				setElement(gui.getInventory());
			}

			visible.add(player);
		}

	}
}
