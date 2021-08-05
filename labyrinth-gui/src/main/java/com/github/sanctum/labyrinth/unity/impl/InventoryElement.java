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

	private final Map<Player, Asynchronous> tasks;
	private final Set<ItemElement<?>> items;
	private final Set<PagedPlayer> index;
	private final String title;
	private final Menu menu;
	private final Map<Player, Inventory> invmap;
	private ListElement<?> listElement;
	private Inventory inventory;
	private int page = 1;
	private int limit = 5;
	private Comparator<? super ItemElement<?>> comparator = Comparator.comparing(ItemElement::getName);
	private Predicate<? super ItemElement<?>> predicate = itemElement -> true;
	private boolean paginated;
	private final boolean lazy;

	public InventoryElement(String title, Menu menu, boolean lazy) {
		this.items = new HashSet<>();
		this.menu = menu;
		this.tasks = new HashMap<>();
		this.index = new HashSet<>();
		this.invmap = new HashMap<>();
		this.title = title;
		this.lazy = lazy;
		this.inventory = Bukkit.createInventory(null, menu.getSize().getSlots(), StringUtils.use(MessageFormat.format(this.title, page, getTotalPages())).translate());
	}

	@Override
	public Inventory getElement() {
		if (this.menu.getProperties().contains(Menu.Property.REFILLABLE)) {
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
		Inventory inventory = this.invmap.computeIfAbsent(player, p -> Bukkit.createInventory(null, this.menu.getSize().getSlots(), StringUtils.use(MessageFormat.format(this.title, page, getTotalPages())).translate()));
		if (this.menu.getProperties().contains(Menu.Property.REFILLABLE)) {
			if (UniformedComponents.accept(Arrays.asList(inventory.getContents())).filter(i -> i != null).count() == 0) {
				if (paginated) {
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
		return new PaginatedList<>(getWorkflow()).limit(this.limit).compare(this.comparator).filter(this.predicate).getTotalPageCount() + 1;
	}

	public @Nullable Asynchronous getTask(Player player) {
		return tasks.get(player);
	}

	/**
	 * Get a players page positioning.
	 *
	 * Only to be used when {@link com.github.sanctum.labyrinth.unity.construct.Menu.Property#SHAREABLE} isn't present.
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
		this.paginated = true;
		this.listElement = element.setLimit(this.menu.getSize().getSlots()).setParent(this);
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

	public synchronized void open(Player player) {
		if (lazy) {
			// This area dictates that our inventory is "lazy" and needs to be instantiated
			this.inventory = Bukkit.createInventory(null, this.menu.getSize().getSlots(), StringUtils.use(MessageFormat.format(this.title, page, getTotalPages())).translate());
			this.invmap.remove(player);
		}
		Schedule.sync(() -> {


			switch (this.menu.getType()) {
				case PRINTABLE:
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
					player.openInventory(getElement());
					break;
				case PAGINATED:
					if (this.menu.getProperties().contains(Menu.Property.LIVE_META)) {
						getElement().setMaxStackSize(1);
						if (this.tasks.containsKey(player)) {
							this.tasks.get(player).cancelTask();
						}

						if (this.menu.getProperties().contains(Menu.Property.SHAREABLE)) {
							if (this.tasks.size() < 1) {
								this.tasks.put(player, Schedule.async(() -> {
									Schedule.sync(() -> {
										InventoryElement.this.getElement().clear();
										for (ItemElement<?> element : getGlobalSlot().getAttachment()) {
											Optional<Integer> in = element.getSlot();
											if (!element.getPage().equals(getGlobalSlot())) {
												element.setPage(getGlobalSlot());
											}
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

							SharedInventory inv = (SharedInventory) this;
							for (Player p : inv.getViewers()) {
								if (p.equals(player)) {
									p.openInventory(getElement());
								} else {
									inv.open(p);
								}
							}

						} else {
							this.tasks.put(player, Schedule.async(() -> {
								Schedule.sync(() -> {
									InventoryElement.this.getElement(player).clear();
									for (ItemElement<?> element : getPlayer(player).getPage().getAttachment()) {
										Optional<Integer> in = element.getSlot();
										if (!element.getPage().equals(getPlayer(player).getPage())) {
											element.setPage(getPlayer(player).getPage());
										}
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

							player.openInventory(getElement(player));

						}

						Schedule.sync(() -> {
							if (this.menu.getProperties().contains(Menu.Property.SHAREABLE)) {
								SharedInventory inv = (SharedInventory) this;
								for (Player p : inv.getViewers()) {
									if (p.equals(player)) {
										player.openInventory(getElement());
									} else {
										inv.open(p);
									}
								}
							} else {
								player.openInventory(getElement(player));
							}

						}).waitReal(2);

						return;
					} else {

						if (this.menu.getProperties().contains(Menu.Property.SHAREABLE)) {
							for (ItemElement<?> element : getGlobalSlot().getAttachment()) {
								if (!element.getPage().equals(getGlobalSlot())) {
									element.setPage(getGlobalSlot());
								}
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

							player.openInventory(getElement());
						} else {
							for (ItemElement<?> element : getPlayer(player).getPage().getAttachment()) {
								Optional<Integer> in = element.getSlot();
								if (!element.getPage().equals(getPlayer(player).getPage())) {
									element.setPage(getPlayer(player).getPage());
								}
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

							player.openInventory(getElement(player));
						}

					}
					break;
				case SINGULAR:
					if (this.menu.getProperties().contains(Menu.Property.ANIMATED)) {
						// TODO: setup animation slide stuff
						return;
					}
					if (this.menu.getProperties().contains(Menu.Property.LIVE_META)) {
						if (this.tasks.containsKey(player)) {
							this.tasks.get(player).cancelTask();
						}

						if (this.menu.getProperties().contains(Menu.Property.SHAREABLE)) {
							getElement().setMaxStackSize(1);
							if (this.tasks.size() < 1) {
								this.tasks.put(player, Schedule.async(() -> {
									Schedule.sync(() -> {
										InventoryElement.this.getElement().clear();
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
						} else {
							getElement(player).setMaxStackSize(1);
							this.tasks.put(player, Schedule.async(() -> {
								Schedule.sync(() -> {
									InventoryElement.this.getElement().clear();
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
								}).run();
							}));
							this.tasks.get(player).repeat(0, 1);

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

						}
						Schedule.sync(() -> {
							if (this.menu.getProperties().contains(Menu.Property.SHAREABLE)) {
								SharedInventory inv = (SharedInventory) this;
								for (Player p : inv.getViewers()) {
									if (p.equals(player)) {
										p.openInventory(getElement());
									} else {
										inv.open(player);
									}
								}
							} else {
								player.openInventory(getElement());
							}

						}).waitReal(2);

						return;
					}
					break;
			}
		}).run();
	}

	public boolean isPaginated() {
		return paginated;
	}

	public static class Page extends Menu.Element<InventoryElement, Set<ItemElement<?>>> {

		private final InventoryElement element;

		private final int num;

		public Page(int num, InventoryElement inventory) {
			this.element = inventory;
			this.num = num;
		}

		public boolean isFull() {
			return getAttachment().size() >= getElement().limit;
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
			return new HashSet<>(new PaginatedList<>(getElement().getWorkflow()).limit(getElement().limit).compare(getElement().comparator).filter(getElement().predicate).get(toNumber()));
		}
	}
}
