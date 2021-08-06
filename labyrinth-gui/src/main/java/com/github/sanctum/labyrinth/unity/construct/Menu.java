package com.github.sanctum.labyrinth.unity.construct;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.task.Asynchronous;
import com.github.sanctum.labyrinth.unity.impl.ClickElement;
import com.github.sanctum.labyrinth.unity.impl.ClosingElement;
import com.github.sanctum.labyrinth.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.unity.impl.ItemElement;
import com.github.sanctum.labyrinth.unity.impl.OpeningElement;
import com.github.sanctum.labyrinth.unity.impl.PreProcessElement;
import java.io.IOException;
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
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A unified menu creation object.
 */
public abstract class Menu {

	protected static final Set<Menu> menus = new HashSet<>();

	protected final Set<Element<?, ?>> elements;
	protected final Rows rows;
	protected final Type type;
	protected final Plugin host;
	protected final String title;
	protected final Set<Property> properties;
	protected Controller controller;
	protected String key;
	private boolean retrieved;

	protected Open open;
	protected Click click;
	protected Close close;
	protected Process process;

	protected Menu(Plugin host, String title, Rows rows, Type type, Property... properties) {
		this.rows = rows;
		this.host = host;
		this.title = title;
		this.type = type;
		this.elements = new HashSet<>();
		this.properties = new HashSet<>(Arrays.asList(properties));
		if (this.properties.contains(Property.CACHEABLE)) {
			menus.add(this);
		}
	}

	/**
	 * @return The menu's inventory element.
	 */
	public abstract InventoryElement getInventory();


	public abstract void open(Player player);

	/**
	 * @return The namespace for the menu.
	 */
	public final Optional<String> getKey() {
		return Optional.ofNullable(this.key);
	}

	public boolean isRetrieved() {
		return retrieved;
	}

	/**
	 * @return The type of menu.
	 */
	public final Type getType() {
		return type;
	}

	/**
	 * @return The size of the inventory.
	 */
	public final Rows getSize() {
		return rows;
	}

	/**
	 * @return The list of all menu properties.
	 */
	public final Set<Property> getProperties() {
		return properties;
	}

	protected final void registerController() throws InstantiationException {
		if (this.controller == null) {

			if (this.host == null) {
				throw new InstantiationException("Menu host cannot be null!");
			}

			this.controller = new Controller();
			Bukkit.getPluginManager().registerEvents(this.controller, host);
		}
	}

	/**
	 * Retrieve all saved {@link ItemElement} storage space from an allotted {@link PersistentContainer} and load it into cache.
	 */
	public synchronized final void retrieve() {
		if (!this.retrieved) {
			this.retrieved = true;
			PersistentContainer container = LabyrinthProvider.getInstance().getContainer(new NamespacedKey(host, "labyrinth-gui-" + this.key));
			if (getInventory().isPaginated()) {
				Map<Integer, ItemStack[]> map = (Map<Integer, ItemStack[]>) container.get(Map.class, getInventory().getTitle());
				if (map != null) {

					if (!getProperties().contains(Property.SAVABLE)) {
						container.delete(getInventory().getTitle());
						return;
					}

					InventoryElement inv = getInventory();

					for (Map.Entry<Integer, ItemStack[]> entry : map.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).collect(Collectors.toList())) {
						for (ItemStack i : entry.getValue()) {
							if (i != null && i.getType() != Material.AIR) {
								inv.addItem(new ItemElement<>(container).setParent(getInventory()).setElement(i));
							}
						}
					}
				}
				return;
			}
			ItemStack[] array = container.get(ItemStack[].class, getInventory().getTitle());
			if (array != null) {

				if (!getProperties().contains(Property.SAVABLE)) {
					container.delete(getInventory().getTitle());
					return;
				}

				for (ItemStack i : array) {
					if (i != null && i.getType() != Material.AIR) {
						getInventory().addItem(new ItemElement<>(container).setParent(getInventory()).setElement(i));
					}
				}
			}
		}
	}

	/**
	 * Save all items from this menu's inventory into a pre-made {@link PersistentContainer} space.
	 */
	public synchronized final void save() {
		if (!getProperties().contains(Property.SAVABLE)) return;
		PersistentContainer container = LabyrinthProvider.getInstance().getContainer(new NamespacedKey(host, "labyrinth-gui-" + this.key));
		if (getInventory().isPaginated()) {
			Map<Integer, ItemStack[]> s = new HashMap<>();
			for (InventoryElement.Page entry : getInventory().getAllPages()) {
				s.put(entry.toNumber(), UniformedComponents.accept(entry.getAttachment().stream().map(ItemElement::getElement).collect(Collectors.toList())).array());
			}
			container.attach(getInventory().getTitle(), s);
		} else {
			container.attach(getInventory().getTitle(), getInventory().getElement().getContents());
		}
	}

	/**
	 * Add custom elements to this inventories cache space.
	 *
	 * @param element The element inheritance to submit
	 * @param <T>     The primary element for this element
	 * @param <R>     The secondary element for this element
	 * @return The primary element from the submitted element.
	 */
	public final <T, R> T addElement(Element<T, R> element) {
		elements.add(element);
		return element.getElement();
	}

	/**
	 * Browse for an element of desired type, for example only one {@link InventoryElement} instance is ever loaded into cache at a time
	 * you can find it by using this method.
	 *
	 * @param function The browsing function for element reducing.
	 * @param <R>      The resulting value.
	 * @return An element of desired type.
	 */
	public final <R> R getElement(Predicate<Element<?, ?>> function) {
		for (Element<?, ?> e : elements) {
			if (function.test(e)) {
				return (R) e;
			}
		}
		return null;
	}

	/**
	 * Handle any miscellaneous click actions that need it.
	 *
	 * @param click The action to take.
	 * @param <T>   The type for this menu.
	 * @return The same menu instance.
	 */
	public final <T extends Menu> T addAction(Click click) {
		this.click = click;
		return (T) this;
	}

	/**
	 * An operation for deciding click action results.
	 */
	@FunctionalInterface
	public interface Click {

		void apply(ClickElement element);

	}

	/**
	 * An operation for deciding closing menu results.
	 */
	@FunctionalInterface
	public interface Close {

		void apply(ClosingElement element);

	}

	/**
	 * An operation for deciding processing menu results.
	 */
	@FunctionalInterface
	public interface Open {

		void apply(OpeningElement element);

	}

	/**
	 * An operation for deciding pre-processing menu results.
	 */
	@FunctionalInterface
	public interface Process {

		void apply(PreProcessElement element);

	}

	/**
	 * An operation for setting up new item elements from a {@link java.util.List}
	 * Primarily used in {@link com.github.sanctum.labyrinth.unity.impl.ListElement}
	 *
	 * @param <T> The list value being fed during operation.
	 */
	@FunctionalInterface
	public interface Populate<T> {

		void accept(T value, ItemElement<?> element);

	}

	/**
	 * Abstraction made for consolidating different types of similar objects into one cache space.
	 *
	 * @param <T> The first data value of this element.
	 * @param <V> The second data value of this element.
	 */
	public static abstract class Element<T, V> {

		private final Set<Element<?, ?>> elements = new HashSet<>();

		public abstract T getElement();

		public abstract V getAttachment();

		public final <X, Y> X addElement(Element<X, Y> element) {
			elements.add(element);
			return element.getElement();
		}

		public final @Nullable Element<?, ?> findElement(Predicate<Element<?, ?>> predicate) {
			for (Element<?, ?> e : elements) {
				if (predicate.test(e)) {
					return e;
				}
			}
			return null;
		}

		public final @NotNull Set<Element<?, ?>> getElements() {
			return this.elements;
		}
	}

	/**
	 * Each property has its own effect and changes how a menu can act and be used.
	 */
	public enum Property {

		/**
		 * This menu is animated with slides of {@link ItemElement} display's.
		 * Animated menu's can be {@link Property#REFILLABLE}, {@link Property#CACHEABLE} & automatically
		 * have live meta.
		 */
		ANIMATED,

		/**
		 * This menu is going to be frequently re-used and requires a fast storage space to be allotted.
		 * This property fits with most other properties without effecting them depending on the desired use.
		 */
		CACHEABLE,

		/**
		 * This menu will have recursively updating item lore and requires task's to do so.
		 * Live updating item lore is best fit for statistic situations where no further animation is required and
		 * can not be used with {@link Property#ANIMATED}.
		 */
		LIVE_META,

		/**
		 * This menu's contents can be persistently stored internally using a dedicated {@link PersistentContainer}
		 * Upon re-use/initialization of the same menu all of its original contents will automatically re-cache.
		 */
		SAVABLE,

		/**
		 * This menu's contents are to be automatically refilled when all items have been removed and one of the past slots have been re-interacted with.
		 * This property fits with most other properties without effecting them depending on the desired use.
		 */
		REFILLABLE,

		RECURSIVE,

		/**
		 * This menu's intention is to be shared with multiple player's in the same inventory instance, anything you give or take can be seen and interacted with by any other viewers.
		 * This property doesn't play nice with animated GUI so we recommend you not trying it.. :)
		 */
		SHAREABLE;

	}

	/**
	 * The type of inventory for a given menu.
	 */
	public enum Type {

		/**
		 * This menu type represents that of a multi-paged inventory space.
		 */
		PAGINATED,

		/**
		 * This menu type represents that of an anvil inventory space.
		 */
		PRINTABLE,

		/**
		 * This menu type represents that of a standard inventory space.
		 */
		SINGULAR
	}

	/**
	 * A utility used to determine an inventories size.
	 */
	public enum Rows {

		ONE(9),

		TWO(18),

		THREE(27),

		FOUR(36),

		FIVE(45),

		SIX(54);

		private final int slots;

		Rows(int slots) {
			this.slots = slots;
		}

		/**
		 * @return The size of the inventory in reference to {@link Bukkit#createInventory(InventoryHolder, int, String)}
		 */
		public int getSlots() {
			return slots;
		}
	}

	/**
	 * A factory for passing generic values on runtime to a menu builder.
	 *
	 * @param <T> A type representative of a menu builder
	 * @param <V> A type representative of a menu
	 */
	public interface BuilderFactory<T extends Builder<V>, V extends Menu> {

		T createBuilder();

	}

	/**
	 * A builder for a new default menu implementation instance.
	 *
	 * @param <T> A type representative of a menu.
	 */
	public static abstract class Builder<T extends Menu> {

		private final Type type;

		private Plugin host;

		private String title;

		private Close close;

		private Consumer<InventoryElement> inventoryEdit;

		private Open open;

		private Process process;

		private String key;

		private final Set<Property> properties = new HashSet<>();

		private Rows size;

		public Builder(Type type) {
			this.type = type;
		}


		/**
		 * Set the title of the menu.
		 *
		 * @param title The title of our menu.
		 * @return Our builder instance.
		 */
		public Builder<T> setTitle(String title) {
			this.title = title;
			return this;
		}

		/**
		 * Set the host for the menu. (Required)
		 *
		 * @param plugin The host of the menu.
		 * @return Our builder instance.
		 */
		public Builder<T> setHost(@NotNull Plugin plugin) {
			this.host = plugin;
			return this;
		}

		/**
		 * Set the size of the inventory.
		 *
		 * @param rows The amount of rows.
		 * @return Our builder instance.
		 */
		public Builder<T> setSize(Rows rows) {
			this.size = rows;
			return this;
		}

		/**
		 * Set the namespace for this menu, if you plan on using {@link Property#SAVABLE}
		 * this step is required.
		 *
		 * @param key The optional namespace for the menu.
		 * @return Our builder instance.
		 */
		public Builder<T> setKey(String key) {
			this.key = key;
			return this;
		}

		/**
		 * Setup the properties for this menu.
		 *
		 * @param properties The menu properties.
		 * @return Our builder instance.
		 */
		public Builder<T> setProperty(Property... properties) {
			this.properties.addAll(Arrays.asList(properties));
			return this;
		}

		/**
		 * Modify the inventory for creation.
		 *
		 * @param edit The operation for editing inventory elements.
		 * @return Our builder instance.
		 */
		public Builder<T> setStock(Consumer<InventoryElement> edit) {
			this.inventoryEdit = edit;
			return this;
		}

		/**
		 * Set what happens when a player closes the menu.
		 *
		 * @param close The event taking place on menu close.
		 * @return Our builder instance.
		 */
		public Builder<T> setCloseEvent(Close close) {
			this.close = close;
			return this;
		}

		/**
		 * Set what happens when a player opens the menu.
		 *
		 * @param open The event taking place on menu open.
		 * @return Our builder instance.
		 */
		public Builder<T> setOpenEvent(Open open) {
			this.open = open;
			return this;
		}

		/**
		 * Set what happens right before a player views the menu.
		 *
		 * @param process The event taking place before menu open.
		 * @return Our builder instance.
		 */
		public Builder<T> setProcessEvent(Process process) {
			this.process = process;
			return this;
		}

		public T orGet(Predicate<Menu> predicate) {

			for (Menu m : Menu.menus) {
				if (predicate.test(m)) {
					return (T) m;
				}
			}

			Menu menu = null;

			switch (type) {
				case PAGINATED:
					menu = new PaginatedMenu(this.host, this.title, this.size, Type.PAGINATED, properties.toArray(new Property[0]));
					break;
				case PRINTABLE:
					menu = new PrintableMenu(this.host, this.title, this.size, Type.PRINTABLE, properties.toArray(new Property[0]));
					break;
				case SINGULAR:
					menu = new SingularMenu(this.host, this.title, this.size, Type.SINGULAR, properties.toArray(new Property[0]));
					break;
			}

			if (inventoryEdit != null) {
				inventoryEdit.accept(menu.getInventory());
			}
			if (this.key != null) {
				menu.key = this.key;
			}
			menu.close = close;
			menu.open = open;
			try {
				menu.registerController();
			} catch (InstantiationException ex) {
				ex.printStackTrace();
			}
			return (T) menu;
		}

		public T join() {
			Menu menu = null;

			switch (type) {
				case PAGINATED:
					menu = new PaginatedMenu(this.host, this.title, this.size, Type.PAGINATED, properties.toArray(new Property[0]));
					break;
				case PRINTABLE:
					menu = new PrintableMenu(this.host, this.title, this.size, Type.PRINTABLE, properties.toArray(new Property[0]));
					break;
				case SINGULAR:
					menu = new SingularMenu(this.host, this.title, this.size, Type.SINGULAR, properties.toArray(new Property[0]));
					break;
			}

			if (inventoryEdit != null) {
				inventoryEdit.accept(menu.getInventory());
			}
			if (this.key != null) {
				menu.key = this.key;
			}
			menu.close = close;
			menu.open = open;
			try {
				menu.registerController();
			} catch (InstantiationException ex) {
				ex.printStackTrace();
			}
			return (T) menu;
		}


	}

	private class Controller implements Listener {

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClose(InventoryCloseEvent e) {
			if (!(e.getPlayer() instanceof Player))
				return;
			if (getType() != Type.PRINTABLE) {
				if (e.getView().getTopInventory().getSize() < rows.slots) return;
			}

			Inventory target = getInventory().getElement();

			if (!getProperties().contains(Property.SHAREABLE)) {
				target = getInventory().getElement((Player) e.getPlayer());
			}

			if (e.getInventory().equals(target)) {

				Player p = (Player) e.getPlayer();

				if (getProperties().contains(Property.LIVE_META) || getProperties().contains(Property.ANIMATED)) {
					// TODO: Shutdown logic for running tasks
					Asynchronous task = getInventory().getTask(p);
					if (task != null) {
						task.cancelTask();
					}
				}

				if (close != null) {
					ClosingElement element = new ClosingElement(Menu.this, (Player) e.getPlayer(), e.getView());
					close.apply(element);

					if (element.isCancelled()) {
						getInventory().open(p);
					}

				}

				p.updateInventory();
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClose(InventoryOpenEvent e) {
			if (!(e.getPlayer() instanceof Player))
				return;
			if (getType() != Type.PRINTABLE) {
				if (e.getView().getTopInventory().getSize() < rows.slots) return;
			}

			Inventory target = getInventory().getElement();

			if (e.getInventory().equals(target)) {

				switch (getType()) {
					case SINGULAR:

					case PAGINATED:
					case PRINTABLE:
				}

				Player p = (Player) e.getPlayer();

				if (open != null) {
					OpeningElement element = new OpeningElement(Menu.this, (Player) e.getPlayer(), e.getView());

					open.apply(element);

					if (element.isCancelled()) {
						element.getElement().closeInventory();
					}

				}


				p.updateInventory();
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onDrag(InventoryDragEvent e) {
			if (!(e.getWhoClicked() instanceof Player))
				return;
			if (getType() != Type.PRINTABLE) {
				if (e.getView().getTopInventory().getSize() < rows.slots) return;
			}

			Inventory target = getInventory().getElement();

			if (!getProperties().contains(Property.SHAREABLE)) {
				target = getInventory().getElement((Player) e.getWhoClicked());
			}

			if (!e.getInventory().equals(target)) return;

			if (e.getInventory().equals(target)) {
				e.setResult(Event.Result.DENY);
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClick(InventoryClickEvent e) {
			if (!(e.getWhoClicked() instanceof Player))
				return;

			if (getType() != Type.PRINTABLE) {
				if (e.getView().getTopInventory().getSize() < rows.slots) return;
			}

			Inventory target = getInventory().getElement();

			if (!getProperties().contains(Property.SHAREABLE)) {
				target = getInventory().getElement((Player) e.getWhoClicked());
			}


			if (!e.getInventory().equals(target)) return;

			if (e.getClickedInventory() == e.getInventory()) {

				Player p = (Player) e.getWhoClicked();

				if (e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();

					ItemElement<?> element = getInventory().match(i -> i.getSlot().isPresent() && e.getRawSlot() == i.getSlot().get() && item.getType() == i.getElement().getType());
					if (element != null) {
						Click click = element.getAttachment();
						if (click == null) return;
						ClickElement clickElement = new ClickElement(p, e.getRawSlot(), e.getAction(), element, e.getView());
						click.apply(clickElement);

						if (clickElement.getResult() != null) {
							e.setResult(clickElement.getResult());
						}

						if (!clickElement.isHotbarAllowed()) {
							if (e.getHotbarButton() != -1) {
								e.setCancelled(true);
								return;
							}
						}

						if (element.getNavigation() != null) {
							ClickElement.Consumer consumer = clickElement.getConsumer();
							switch (element.getNavigation()) {
								case Back:
									if (consumer != null) {
										consumer.accept(clickElement.getElement(), false);
									}
									break;
								case Next:
									if (Menu.this instanceof PaginatedMenu) {
										PaginatedMenu m = (PaginatedMenu) Menu.this;

										if (getProperties().contains(Property.SAVABLE)) {
											if (!getProperties().contains(Property.SHAREABLE)) {
												m.getInventory().getPlayer(clickElement.getElement()).setPage(m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() + 1);
											} else {
												m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() + 1);
											}
											if (consumer != null) {
												consumer.accept(clickElement.getElement(), true);
											}
										} else {
											if (!getProperties().contains(Property.SHAREABLE)) {
												if ((m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() + 1) <= m.getInventory().getTotalPages()) {
													m.getInventory().getPlayer(clickElement.getElement()).setPage(m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() + 1);
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), true);
													}
												} else {
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), false);
													}
												}
											} else {
												if ((m.getInventory().getGlobalSlot().toNumber() + 1) <= m.getInventory().getTotalPages()) {
													m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() + 1);
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), true);
													}
												} else {
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), false);
													}
												}
											}
										}
										break;
									}
									if (consumer != null) {
										consumer.accept(clickElement.getElement(), false);
									}
									break;
								case Previous:
									if (Menu.this instanceof PaginatedMenu) {
										PaginatedMenu m = (PaginatedMenu) Menu.this;

										if (getProperties().contains(Property.SAVABLE)) {

											if (!getProperties().contains(Property.SHAREABLE)) {
												if ((m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() - 1) >= 1) {
													m.getInventory().getPlayer(clickElement.getElement()).setPage(m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() - 1);
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), true);
													}
												} else {
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), false);
													}
												}
											} else {
												if ((m.getInventory().getGlobalSlot().toNumber() - 1) >= 1) {
													m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() - 1);
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), true);
													}
												} else {
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), false);
													}
												}
											}
										} else {
											if (!getProperties().contains(Property.SHAREABLE)) {
												if ((m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() - 1) < m.getInventory().getTotalPages() && (m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() - 1) >= 1) {
													m.getInventory().getPlayer(clickElement.getElement()).setPage(m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() - 1);
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), true);
													}
												} else {
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), false);
													}
												}
											} else {
												if ((m.getInventory().getGlobalSlot().toNumber() - 1) < m.getInventory().getTotalPages() && (m.getInventory().getGlobalSlot().toNumber() - 1) >= 1) {
													m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() - 1);
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), true);
													}
												} else {
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), false);
													}
												}
											}
										}
										break;
									}
									if (consumer != null) {
										consumer.accept(clickElement.getElement(), false);
									}
									break;
							}
						}

						if (clickElement.isCancelled()) {
							e.setCancelled(true);
						}

					} else {
						ItemElement<?> element2 = getInventory().match(item);
						if (element2 != null) {
							Click click = element2.getAttachment();
							if (click == null) return;
							ClickElement clickElement = new ClickElement(p, e.getRawSlot(), e.getAction(), element2, e.getView());
							click.apply(clickElement);

							if (clickElement.getResult() != null) {
								e.setResult(clickElement.getResult());
							}

							if (!clickElement.isHotbarAllowed()) {
								if (e.getHotbarButton() != -1) {
									e.setCancelled(true);
									return;
								}
							}

							if (element2.getNavigation() != null) {
								ClickElement.Consumer consumer = clickElement.getConsumer();
								switch (element2.getNavigation()) {
									case Back:
										if (consumer != null) {
											consumer.accept(clickElement.getElement(), false);
										}
										break;
									case Next:
										if (Menu.this instanceof PaginatedMenu) {
											PaginatedMenu m = (PaginatedMenu) Menu.this;

											if (getProperties().contains(Property.SAVABLE)) {
												if (!getProperties().contains(Property.SHAREABLE)) {
													m.getInventory().getPlayer(clickElement.getElement()).setPage(m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() + 1);
												} else {
													m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() + 1);
												}
												if (consumer != null) {
													consumer.accept(clickElement.getElement(), true);
												}
											} else {
												if (!getProperties().contains(Property.SHAREABLE)) {
													if ((m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() + 1) <= m.getInventory().getTotalPages()) {
														m.getInventory().getPlayer(clickElement.getElement()).setPage(m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() + 1);
														if (consumer != null) {
															consumer.accept(clickElement.getElement(), true);
														}
													} else {
														if (consumer != null) {
															consumer.accept(clickElement.getElement(), false);
														}
													}
												} else {
													if ((m.getInventory().getGlobalSlot().toNumber() + 1) <= m.getInventory().getTotalPages()) {
														m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() + 1);
														if (consumer != null) {
															consumer.accept(clickElement.getElement(), true);
														}
													} else {
														if (consumer != null) {
															consumer.accept(clickElement.getElement(), false);
														}
													}
												}
											}
											break;
										}
										if (consumer != null) {
											consumer.accept(clickElement.getElement(), false);
										}
										break;
									case Previous:
										if (Menu.this instanceof PaginatedMenu) {
											PaginatedMenu m = (PaginatedMenu) Menu.this;

											if (getProperties().contains(Property.SAVABLE)) {

												if (!getProperties().contains(Property.SHAREABLE)) {
													if ((m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() - 1) >= 1) {
														m.getInventory().getPlayer(clickElement.getElement()).setPage(m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() - 1);
														if (consumer != null) {
															consumer.accept(clickElement.getElement(), true);
														}
													} else {
														if (consumer != null) {
															consumer.accept(clickElement.getElement(), false);
														}
													}
												} else {
													if ((m.getInventory().getGlobalSlot().toNumber() - 1) >= 1) {
														m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() - 1);
														if (consumer != null) {
															consumer.accept(clickElement.getElement(), true);
														}
													} else {
														if (consumer != null) {
															consumer.accept(clickElement.getElement(), false);
														}
													}
												}
											} else {
												if (!getProperties().contains(Property.SHAREABLE)) {
													if ((m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() - 1) < m.getInventory().getTotalPages() && (m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() - 1) >= 1) {
														m.getInventory().getPlayer(clickElement.getElement()).setPage(m.getInventory().getPlayer(clickElement.getElement()).getPage().toNumber() - 1);
														if (consumer != null) {
															consumer.accept(clickElement.getElement(), true);
														}
													} else {
														if (consumer != null) {
															consumer.accept(clickElement.getElement(), false);
														}
													}
												} else {
													if ((m.getInventory().getGlobalSlot().toNumber() - 1) < m.getInventory().getTotalPages() && (m.getInventory().getGlobalSlot().toNumber() - 1) >= 1) {
														m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() - 1);
														if (consumer != null) {
															consumer.accept(clickElement.getElement(), true);
														}
													} else {
														if (consumer != null) {
															consumer.accept(clickElement.getElement(), false);
														}
													}
												}
											}
											break;
										}
										if (consumer != null) {
											consumer.accept(clickElement.getElement(), false);
										}
										break;
								}
							}
							if (clickElement.isCancelled()) {
								e.setCancelled(true);
							}
						}
					}
				}

				if (!e.isCancelled()) {

					if (Menu.this.click != null) {
						ClickElement element = new ClickElement((Player) e.getWhoClicked(), e.getRawSlot(), e.getAction(), new ItemElement<>().setParent(getInventory()).setElement(e.getCurrentItem()), e.getView());
						Menu.this.click.apply(element);

						if (element.getResult() != null) {
							e.setResult(element.getResult());
						}

						if (!element.isHotbarAllowed()) {
							if (e.getHotbarButton() != -1) {
								e.setCancelled(true);
								return;
							}
						}

						if (element.isCancelled()) {
							e.setCancelled(true);
						}
					}

				}

			}

		}

	}

}
