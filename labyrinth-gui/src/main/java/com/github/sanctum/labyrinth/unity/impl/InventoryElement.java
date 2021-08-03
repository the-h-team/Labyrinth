package com.github.sanctum.labyrinth.unity.impl;

import com.github.sanctum.labyrinth.formatting.PaginatedList;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Asynchronous;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.unity.construct.Menu;
import com.github.sanctum.labyrinth.unity.construct.PagedPlayer;
import com.github.sanctum.labyrinth.unity.impl.inventory.SharedInventory;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

	private final Map<Player, Asynchronous> tasks;
	private final Set<ItemElement<?>> items;
	private final Set<PagedPlayer> index;
	private final String title;
	private final Menu.Rows rows;
	private final Menu.Type parentType;
	private final Set<Menu.Property> properties;
	private final Map<Player, Inventory> invmap;
	private ListElement<?> listElement;
	private Inventory inventory;
	private int page = 1;
	private int limit = 5;
	private Comparator<? super ItemElement<?>> comparator = Comparator.comparing(ItemElement::getName);
	private Predicate<? super ItemElement<?>> predicate = itemElement -> true;
	private boolean paginated;
	private final boolean lazy;

	public InventoryElement(String title, Menu.Type type, Set<Menu.Property> properties, Menu.Rows rows, boolean lazy) {
		this.items = new HashSet<>();
		this.tasks = new HashMap<>();
		this.index = new HashSet<>();
		this.invmap = new HashMap<>();
		this.properties = properties;
		this.parentType = type;
		this.rows = rows;
		this.title = title;
		this.lazy = lazy;
		this.inventory = Bukkit.createInventory(null, this.rows.getSlots(), StringUtils.use(MessageFormat.format(this.title, page, getPageCount())).translate());
	}

	@Override
	public Inventory getElement() {
		if (getProperties().contains(Menu.Property.REFILLABLE)) {
			if (UniformedComponents.accept(Arrays.asList(this.inventory.getContents())).filter(i -> i != null).count() == 0) {
				if (paginated) {
					for (ItemElement<?> element : new PaginatedList<>(getWorkflow()).limit(this.limit).compare(this.comparator).filter(this.predicate).get(page)) {
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
		Inventory inventory = this.invmap.computeIfAbsent(player, p -> Bukkit.createInventory(null, this.rows.getSlots(), StringUtils.use(MessageFormat.format(this.title, page, getPageCount())).translate()));
		if (getProperties().contains(Menu.Property.REFILLABLE)) {
			if (UniformedComponents.accept(Arrays.asList(inventory.getContents())).filter(i -> i != null).count() == 0) {
				if (paginated) {
					for (ItemElement<?> element : new PaginatedList<>(getWorkflow()).limit(this.limit).compare(this.comparator).filter(this.predicate).get(page)) {
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

	public InventoryElement setFilter(Predicate<? super ItemElement<?>> predicate) {
		this.predicate = predicate;
		return this;
	}

	public InventoryElement setComparator(Comparator<? super ItemElement<?>> comparator) {
		this.comparator = comparator;
		return this;
	}

	public InventoryElement setLimit(int elementLimit) {
		this.limit = elementLimit;
		return this;
	}

	public InventoryElement setPage(int page) {
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

	public Set<ItemElement<?>> getPage(int page) {
		return new HashSet<>(new PaginatedList<>(getWorkflow()).limit(this.limit).compare(this.comparator).filter(this.predicate).get(page));
	}

	public Map<Integer, Set<ItemElement<?>>> getAllPages() {
		Map<Integer, Set<ItemElement<?>>> map = new HashMap<>();
		for (int i = 1; i < getPageCount(); i++) {
			map.put(i, getPage(i));
		}
		return map;
	}

	public int getPage() {
		return page;
	}

	public int getPageCount() {
		return new PaginatedList<>(getWorkflow()).limit(this.limit).compare(this.comparator).filter(this.predicate).getTotalPageCount();
	}

	public @Nullable Asynchronous getTask(Player player) {
		return tasks.get(player);
	}

	public PagedPlayer getPlayer(Player player) {
		PagedPlayer pl = this.index.stream().filter(p -> player.getName().equals(p.getPlayer().getName())).findFirst().orElse(null);
		if (pl == null) {
			pl = new PagedPlayer(player.getUniqueId());
			this.index.add(pl);
		}
		return pl;
	}

	public Set<ItemElement<?>> getWorkflow() {
		Set<ItemElement<?>> items = this.items.stream().filter(i -> !i.getSlot().isPresent()).collect(Collectors.toSet());
		if (isPaginated()) {
			items.addAll(listElement.getAttachment());
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

	public @NotNull Menu.Type getParentType() {
		return parentType;
	}

	public @NotNull Set<Menu.Property> getProperties() {
		return properties;
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

	public <R> InventoryElement addItem(ItemElement<R> item) {
		this.items.add(item);
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
		this.paginated = true;
		this.listElement = element.setMax(this.rows.getSlots());
		return this;
	}

	public InventoryElement addItem(Consumer<ItemElement<?>> builder) {
		ItemElement<?> element = new ItemElement<>();
		builder.accept(element);
		this.items.add(element);
		return this;
	}

	public <R> InventoryElement addItem(Consumer<ItemElement<R>> builder, R value) {
		ItemElement<R> element = new ItemElement<>(value);
		builder.accept(element);
		this.items.add(element);
		return this;
	}

	public synchronized void open(Player player) {
		if (lazy) {
			// This area dictates that our inventory is "lazy" and needs to be instantiated
			this.inventory = Bukkit.createInventory(null, this.rows.getSlots(), StringUtils.use(MessageFormat.format(this.title, page, getPageCount())).translate());
			this.invmap.remove(player);
		}
		Schedule.sync(() -> {
			Inventory i = !getProperties().contains(Menu.Property.SHAREABLE) ? getElement(player) : getElement();


			switch (getParentType()) {
				case PRINTABLE:
					for (ItemElement<?> element : new PaginatedList<>(getWorkflow()).limit(this.limit).compare(this.comparator).filter(this.predicate).get(page)) {
						Optional<Integer> in = element.getSlot();
						if (in.isPresent()) {
							i.setItem(in.get(), element.getElement());
						} else {
							if (!i.contains(element.getElement())) {
								i.addItem(element.getElement());
							}
						}
					}
					for (ItemElement<?> element : items) {
						Optional<Integer> in = element.getSlot();
						in.ifPresent(integer -> i.setItem(integer, element.getElement()));
					}
					player.openInventory(i);
					break;
				case PAGINATED:
					if (getProperties().contains(Menu.Property.LIVE_META)) {
						i.setMaxStackSize(1);
						if (this.tasks.containsKey(player)) {
							this.tasks.get(player).cancelTask();
						}

						if (getProperties().contains(Menu.Property.SHAREABLE)) {
							if (this.tasks.size() < 1) {
								this.tasks.put(player, Schedule.async(() -> {
									Schedule.sync(() -> {
										InventoryElement.this.getElement().clear();
										for (ItemElement<?> element : new PaginatedList<>(getWorkflow()).limit(this.limit).compare(this.comparator).filter(this.predicate).get(page)) {
											Optional<Integer> in = element.getSlot();
											if (in.isPresent()) {
												i.setItem(in.get(), element.getElement());
											} else {
												if (!i.contains(element.getElement())) {
													i.addItem(element.getElement());
												}
											}
										}
										for (ItemElement<?> element : items) {
											Optional<Integer> in = element.getSlot();
											in.ifPresent(integer -> i.setItem(integer, element.getElement()));
										}
									}).run();
								}));
								this.tasks.get(player).repeat(0, 1);
							}
						} else {
							this.tasks.put(player, Schedule.async(() -> {
								Schedule.sync(() -> {
									InventoryElement.this.getElement().clear();
									for (ItemElement<?> element : new PaginatedList<>(getWorkflow()).limit(this.limit).compare(this.comparator).filter(this.predicate).get(page)) {
										Optional<Integer> in = element.getSlot();
										if (in.isPresent()) {
											i.setItem(in.get(), element.getElement());
										} else {
											if (!i.contains(element.getElement())) {
												i.addItem(element.getElement());
											}
										}
									}
									for (ItemElement<?> element : items) {
										Optional<Integer> in = element.getSlot();
										in.ifPresent(integer -> i.setItem(integer, element.getElement()));
									}
								}).run();
							}));
							this.tasks.get(player).repeat(0, 1);

						}
						Schedule.sync(() -> {
							if (getProperties().contains(Menu.Property.SHAREABLE)) {
								SharedInventory inv = (SharedInventory) this;
								for (Player p : inv.getViewers()) {
									p.openInventory(i);
								}
							} else {
								player.openInventory(i);
							}

						}).waitReal(2);

						return;
					}
					for (ItemElement<?> element : new PaginatedList<>(getWorkflow()).limit(this.limit).compare(this.comparator).filter(this.predicate).get(page)) {
						Optional<Integer> in = element.getSlot();
						if (in.isPresent()) {
							i.setItem(in.get(), element.getElement());
						} else {
							if (!i.contains(element.getElement())) {
								i.addItem(element.getElement());
							}
						}
					}
					for (ItemElement<?> element : items) {
						Optional<Integer> in = element.getSlot();
						in.ifPresent(integer -> i.setItem(integer, element.getElement()));
					}
					if (getProperties().contains(Menu.Property.SHAREABLE)) {
						SharedInventory inv = (SharedInventory) this;
						for (Player p : inv.getViewers()) {
							p.openInventory(i);
						}
					} else {
						player.openInventory(i);
					}
					break;
				case SINGULAR:
					if (getProperties().contains(Menu.Property.ANIMATED)) {
						// TODO: setup animation slide stuff
						return;
					}
					if (getProperties().contains(Menu.Property.LIVE_META)) {
						i.setMaxStackSize(1);
						if (this.tasks.containsKey(player)) {
							this.tasks.get(player).cancelTask();
						}

						if (getProperties().contains(Menu.Property.SHAREABLE)) {
							if (this.tasks.size() < 1) {
								this.tasks.put(player, Schedule.async(() -> {
									Schedule.sync(() -> {
										InventoryElement.this.getElement().clear();
										for (ItemElement<?> element : new PaginatedList<>(getWorkflow()).limit(this.limit).compare(this.comparator).filter(this.predicate).get(page)) {
											Optional<Integer> in = element.getSlot();
											if (in.isPresent()) {
												i.setItem(in.get(), element.getElement());
											} else {
												if (!i.contains(element.getElement())) {
													i.addItem(element.getElement());
												}
											}
										}
										for (ItemElement<?> element : items) {
											Optional<Integer> in = element.getSlot();
											in.ifPresent(integer -> i.setItem(integer, element.getElement()));
										}
									}).run();
								}));
								this.tasks.get(player).repeat(0, 1);
							}
						} else {
							this.tasks.put(player, Schedule.async(() -> {
								Schedule.sync(() -> {
									InventoryElement.this.getElement().clear();
									for (ItemElement<?> element : new PaginatedList<>(getWorkflow()).limit(this.limit).compare(this.comparator).filter(this.predicate).get(page)) {
										Optional<Integer> in = element.getSlot();
										if (in.isPresent()) {
											i.setItem(in.get(), element.getElement());
										} else {
											if (!i.contains(element.getElement())) {
												i.addItem(element.getElement());
											}
										}
									}
									for (ItemElement<?> element : items) {
										Optional<Integer> in = element.getSlot();
										in.ifPresent(integer -> i.setItem(integer, element.getElement()));
									}
								}).run();
							}));
							this.tasks.get(player).repeat(0, 1);

						}
						Schedule.sync(() -> {
							if (getProperties().contains(Menu.Property.SHAREABLE)) {
								SharedInventory inv = (SharedInventory) this;
								for (Player p : inv.getViewers()) {
									p.openInventory(i);
								}
							} else {
								player.openInventory(i);
							}

						}).waitReal(2);

						return;
					}
					for (ItemElement<?> element : new PaginatedList<>(getWorkflow()).limit(this.limit).compare(this.comparator).filter(this.predicate).get(page)) {
						Optional<Integer> in = element.getSlot();
						if (in.isPresent()) {
							i.setItem(in.get(), element.getElement());
						} else {
							if (!i.contains(element.getElement())) {
								i.addItem(element.getElement());
							}
						}
					}
					for (ItemElement<?> element : items) {
						Optional<Integer> in = element.getSlot();
						in.ifPresent(integer -> i.setItem(integer, element.getElement()));
					}
					if (getProperties().contains(Menu.Property.SHAREABLE)) {
						SharedInventory inv = (SharedInventory) this;
						for (Player p : inv.getViewers()) {
							p.openInventory(i);
						}
					} else {
						player.openInventory(i);
					}
					break;
			}
		}).run();
	}

	public boolean isPaginated() {
		return paginated;
	}
}
