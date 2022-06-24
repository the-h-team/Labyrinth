package com.github.sanctum.labyrinth.gui.unity.construct;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.labyrinth.gui.unity.event.MenuClickEvent;
import com.github.sanctum.labyrinth.gui.unity.event.MenuDragItemEvent;
import com.github.sanctum.labyrinth.gui.unity.impl.ClickElement;
import com.github.sanctum.labyrinth.gui.unity.impl.ClosingElement;
import com.github.sanctum.labyrinth.gui.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.gui.unity.impl.ItemElement;
import com.github.sanctum.labyrinth.gui.unity.impl.OpeningElement;
import com.github.sanctum.labyrinth.gui.unity.impl.PreProcessElement;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.RenderedTask;
import com.github.sanctum.labyrinth.task.Task;
import com.github.sanctum.labyrinth.task.TaskMonitor;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
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

	protected static Controller controller;
	protected AnvilController anvil;
	protected final Set<Element<?, ?>> elements;
	protected final Rows rows;
	protected final Type type;
	protected final Plugin host;
	protected final String title;
	protected final Set<Property> properties;
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
			MenuRegistration.getInstance().register(this).deploy();
		}
	}

	/**
	 * @return The menu's inventory element.
	 */
	public abstract InventoryElement getInventory();

	public abstract void open(Player player);

	public void close(Player player) {
		getInventory().close(player);
	}

	/**
	 * @return The listener for anvil events.
	 */
	public Listener getController() {
		return this.anvil;
	}

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

	public final Optional<Close> getCloseEvent() {
		return Optional.ofNullable(this.close);
	}

	public final @NotNull Plugin getHost() {
		return host;
	}

	protected final void registerController() throws InstantiationException {
		if (controller == null) {
			controller = new Controller();
			Bukkit.getPluginManager().registerEvents(controller, LabyrinthProvider.getInstance().getPluginInstance());
		}
		if (anvil == null && this instanceof PrintableMenu) {
			if (this.host == null) {
				throw new InstantiationException("Menu host cannot be null!");
			}
			anvil = new AnvilController();
			Bukkit.getPluginManager().registerEvents(anvil, host);
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
				Map<Integer, UniformedComponents<ItemStack>> map = (Map<Integer, UniformedComponents<ItemStack>>) container.get(Map.class, getInventory().getTitle());
				if (map != null) {

					if (!getProperties().contains(Property.SAVABLE)) {
						container.delete(getInventory().getTitle());
						return;
					}

					InventoryElement inv = getInventory();

					for (Map.Entry<Integer, UniformedComponents<ItemStack>> entry : map.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).collect(Collectors.toList())) {
						for (ItemStack i : entry.getValue().array()) {
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
			InventoryElement.Paginated inv = (InventoryElement.Paginated) getInventory();
			Map<Integer, UniformedComponents<ItemStack>> s = new HashMap<>();
			for (InventoryElement.Page entry : inv.getAllPages()) {
				s.put(entry.toNumber(), UniformedComponents.accept(entry.getAttachment().stream().map(ItemElement::getElement).collect(Collectors.toList())));
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
	 * Primarily used in {@link com.github.sanctum.labyrinth.gui.unity.impl.ListElement}
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

		public final @Nullable Element<?, ?> getElement(Predicate<Element<?, ?>> predicate) {
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

		/**
		 * This menu will have a recursively updating inventory title, this option is best used with pagination to consistently display page placement
		 * in your title using {0} {1} placeholders.
		 */
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
		 * *NEW* This menu type represents that of a modded inventory space.
		 */
		MODDED,
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
	 * Define inventory size.
	 * <p>
	 * Helps enforce slot parameter contract of
	 * {@link Bukkit#createInventory(InventoryHolder, int, String)}
	 * (int must be divisible by 9)
	 */
	public enum Rows {

		/**
		 * Slots: 9
		 */
		ONE(9),

		/**
		 * Slots: 18
		 */
		TWO(18),

		/**
		 * Slots: 27
		 */
		THREE(27),

		/**
		 * Slots: 36
		 */
		FOUR(36),

		/**
		 * Slots: 45
		 */
		FIVE(45),

		/**
		 * Slots: 54
		 */
		SIX(54);

		private final int slots;

		Rows(int slots) {
			this.slots = slots;
		}

		/**
		 * @return The size of the inventory.
		 */
		public int getSize() {
			return slots;
		}

		public int[] getSlots(Panel layout) {
			return layout.get(getSize());
		}

	}

	/**
	 * Get slots for a specific position within an inventory.
	 */
	public enum Panel {

		/**
		 * The top bar of slots within the inventory.
		 */
		TOP(i -> IntStream.range(0, 9).toArray()),

		/**
		 * The bottom bar of slots within the inventory.
		 */
		BOTTOM(i -> IntStream.range(i - 9, i).toArray()),

		/**
		 * The middle space of the inventory.
		 */
		MIDDLE(i -> {
			if (i <= 18) {
				return IntStream.range(0, 9).toArray();
			}
			return IntStream.range(10, i).filter(n -> n < i - 9 && n % 9 != 0 && n % 9 != 8).toArray();
		}),

		/**
		 * The left bar of slots within the inventory.
		 */
		LEFT(i -> IntStream.iterate(0, n -> n + 9).limit(i / 9).toArray()),

		/**
		 * The right bar of slots within the inventory.
		 */
		RIGHT(i -> IntStream.iterate(8, n -> n + 9).limit(i / 9).toArray());

		private final Function<Integer, int[]> generatorFunction;
		private final Map<Integer, int[]> cache = new HashMap<>();

		Panel(final Function<Integer, int[]> generatorFunction) {
			this.generatorFunction = generatorFunction;
		}

		public int[] get(int slots) {
			int[] result = cache.computeIfAbsent(slots, generatorFunction);
			return Arrays.copyOf(result, result.length);
		}

	}

	/**
	 * A factory for passing generic values on runtime to a menu builder.
	 *
	 * @param <T> A type representative of a menu builder
	 * @param <V> A type representative of a menu
	 */
	public interface BuilderFactory<T extends Builder<V, K>, V extends Menu, K extends InventoryElement> {

		T createBuilder();

	}

	/**
	 * A builder for a new default menu implementation instance.
	 *
	 * @param <T> A type representative of a menu.
	 */
	public static abstract class Builder<T extends Menu, K extends InventoryElement> {

		private final Type type;

		private Plugin host;

		private String title;

		private Close close;

		private Consumer<K> inventoryEdit;

		private Open open;

		private Process process;

		private String key;

		protected final Set<Property> properties = new HashSet<>();

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
		public Builder<T, K> setTitle(String title) {
			this.title = title;
			return this;
		}

		/**
		 * Set the host for the menu. (Required)
		 *
		 * @param plugin The host of the menu.
		 * @return Our builder instance.
		 */
		public Builder<T, K> setHost(@NotNull Plugin plugin) {
			this.host = plugin;
			return this;
		}

		/**
		 * Set the size of the inventory.
		 *
		 * @param rows The amount of rows.
		 * @return Our builder instance.
		 */
		public Builder<T, K> setSize(Rows rows) {
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
		public Builder<T, K> setKey(String key) {
			this.key = key;
			return this;
		}

		/**
		 * Setup the properties for this menu.
		 *
		 * @param properties The menu properties.
		 * @return Our builder instance.
		 */
		public Builder<T, K> setProperty(Property... properties) {
			this.properties.addAll(Arrays.asList(properties));
			return this;
		}

		/**
		 * Modify the inventory for creation.
		 *
		 * @param edit The operation for editing inventory elements.
		 * @return Our builder instance.
		 */
		public Builder<T, K> setStock(Consumer<K> edit) {
			this.inventoryEdit = edit;
			return this;
		}

		/**
		 * Set what happens when a player closes the menu.
		 *
		 * @param close The event taking place on menu close.
		 * @return Our builder instance.
		 */
		public Builder<T, K> setCloseEvent(Close close) {
			this.close = close;
			return this;
		}

		/**
		 * Set what happens when a player opens the menu.
		 *
		 * @param open The event taking place on menu open.
		 * @return Our builder instance.
		 */
		public Builder<T, K> setOpenEvent(Open open) {
			this.open = open;
			return this;
		}

		/**
		 * Set what happens right before a player views the menu.
		 *
		 * @param process The event taking place before menu open.
		 * @return Our builder instance.
		 */
		public Builder<T, K> setProcessEvent(Process process) {
			this.process = process;
			return this;
		}

		public T orGet(Predicate<Menu> predicate) {
			Menu test = MenuRegistration.getInstance().getAll().get().stream().filter(predicate).findFirst().orElse(null);
			if (test != null) return (T) test;

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
				inventoryEdit.accept((K) menu.getInventory());
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
				inventoryEdit.accept((K) menu.getInventory());
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

	@FunctionalInterface
	public interface Instance extends InventoryHolder {

		@NotNull Menu getMenu();

		@NotNull
		@Override
		default Inventory getInventory() {
			return getMenu().getInventory().getElement();
		}

		static @NotNull Instance of(@NotNull Menu menu) {
			return () -> menu;
		}

		static @NotNull Instance of(@NotNull Menu menu, Player player) {
			return new Instance() {

				@Override
				public @NotNull Inventory getInventory() {
					return player != null ? menu.getInventory().getViewer(player).getInventory().getElement() : menu.getInventory().getElement();
				}

				@Override
				public @NotNull Menu getMenu() {
					return menu;
				}
			};
		}

	}

	/**
	 * Private listener construction for anvil usage.
	 */
	class AnvilController implements Listener {

		private void unRegisterHandlers() {
			HandlerList.unregisterAll(getController());
			InventoryCloseEvent.getHandlerList().unregister(getController());
			InventoryOpenEvent.getHandlerList().unregister(getController());
			InventoryDragEvent.getHandlerList().unregister(getController());
			InventoryClickEvent.getHandlerList().unregister(getController());
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClose(InventoryCloseEvent e) {
			if (!(e.getPlayer() instanceof Player))
				return;
			if (getType() != Type.PRINTABLE) {
				if (e.getView().getTopInventory().getSize() < rows.slots) return;
			}

			Inventory target = getInventory().getElement();

			if (!getProperties().contains(Property.SHAREABLE)) {
				target = getInventory().getViewer((Player) e.getPlayer()).getElement();
			}

			if (e.getInventory().equals(target)) {
				if (LabyrinthProvider.getInstance().isModded()) {
					TaskScheduler.of(() -> {
						Arrays.stream(e.getPlayer().getInventory().getContents()).filter(i -> i != null && i.getType() != Material.AIR).forEach(i -> {
							if (i.hasItemMeta() && !i.getItemMeta().hasDisplayName() && i.getType() == (Items.findMaterial("playerhead") == null ? Items.findMaterial("skullitem") : Items.findMaterial("playerhead"))) {
								i.setAmount(0);
								i.setType(Material.AIR);
							}
						});
					}).scheduleLater(1);
				}
				Player p = (Player) e.getPlayer();

				if (getProperties().contains(Property.LIVE_META) || getProperties().contains(Property.ANIMATED)) {
					RenderedTask task = getInventory().getViewer(p).getTask();
					if (task != null) {
						task.getTask().cancel();
					}
					Task t = TaskMonitor.getLocalInstance().get("Labyrinth:" + Menu.this.hashCode() + ";slide-" + p.getUniqueId());
					if (t != null) {
						t.cancel();
					}
				}

				if (close != null) {
					ClosingElement element = new ClosingElement(Menu.this, (Player) e.getPlayer(), e.getView());
					close.apply(element);

					if (element.isCancelled()) {
						getInventory().open(p);
					}

				}
				Inventory finalTarget = target;
				if (!getProperties().contains(Property.CACHEABLE)) {
					TaskScheduler.of(() -> {
						if (finalTarget.getViewers().stream().noneMatch(v -> v instanceof Player)) {
							unRegisterHandlers();
						}
					}).scheduleLater(2);
				}
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onOpen(InventoryOpenEvent e) {
			if (!(e.getPlayer() instanceof Player))
				return;
			if (getType() != Type.PRINTABLE) {
				if (e.getView().getTopInventory().getSize() < rows.slots) return;
			}

			Inventory target = getInventory().getElement();

			if (e.getInventory().equals(target)) {

				Player p = (Player) e.getPlayer();

				if (open != null) {
					OpeningElement element = new OpeningElement(Menu.this, (Player) e.getPlayer(), e.getView());

					open.apply(element);

					if (element.isCancelled()) {
						element.getElement().closeInventory();
					}

				}
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
				target = getInventory().getViewer((Player) e.getWhoClicked()).getElement();
			}

			if (!e.getInventory().equals(target)) return;

			if (e.getInventory().equals(target)) {
				e.setResult(Event.Result.DENY);
			}

			ItemStack attempt = e.getCursor() != null ? e.getCursor() : e.getOldCursor();
			ItemElement<?> element = getInventory().getItem(attempt);
			if (element == null) element = new ItemElement<>().setPlayerAdded(true).setParent(getInventory()).setElement(attempt);
			MenuDragItemEvent event = new Vent.Call<>(new MenuDragItemEvent(Menu.this, (Player) e.getWhoClicked(), element)).run();
			if (event.isCancelled()) e.setCancelled(true);

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
				target = getInventory().getViewer((Player) e.getWhoClicked()).getElement();
			}


			if (!e.getInventory().equals(target)) return;

			if (e.getClickedInventory() == e.getInventory()) {
				Player p = (Player) e.getWhoClicked();
				if (e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();
					ItemElement<?> fixedSlotElement = getInventory().getItem(i -> i.getSlot().isPresent() && e.getRawSlot() == i.getSlot().get() && item.getType() == i.getElement().getType() && i.getType() != ItemElement.ControlType.ITEM_FILLER && i.getType() != ItemElement.ControlType.ITEM_BORDER);
					if (fixedSlotElement != null) {
						MenuClickEvent event = new Vent.Call<>(new MenuClickEvent(p, Menu.this, fixedSlotElement)).run();
						if (event.isCancelled()) {
							e.setCancelled(true);
							return;
						}
						Click click = fixedSlotElement.getAttachment();
						ClickElement clickElement = new ClickElement(p, e.getRawSlot(), e.getAction(), e.getClick(), fixedSlotElement, e.getCursor(), e.getView());
						if (click != null) {
							click.apply(clickElement);
						}
						if (clickElement.getResult() != null) {
							e.setResult(clickElement.getResult());
						}
						if (e.getHotbarButton() != -1) {
							if (!clickElement.isHotbarAllowed()) {
								e.setCancelled(true);
							}
						}

						if (fixedSlotElement.getType() != null) {
							ClickElement.Consumer consumer = clickElement.getConsumer();
							switch (fixedSlotElement.getType()) {
								case TAKEAWAY:
									fixedSlotElement.remove(false);
									break;
								case BUTTON_EXIT:
									if (consumer != null) {
										consumer.accept(clickElement.getElement(), false);
									}
									break;
								case BUTTON_NEXT:
									if (Menu.this instanceof PaginatedMenu) {
										PaginatedMenu m = (PaginatedMenu) Menu.this;

										if (getProperties().contains(Property.SAVABLE)) {
											if (!getProperties().contains(Property.SHAREABLE)) {
												m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1);
											} else {
												m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() + 1);
											}
											if (consumer != null) {
												consumer.accept(clickElement.getElement(), true);
											}
										} else {
											if (!getProperties().contains(Property.SHAREABLE)) {
												if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1) <= m.getInventory().getTotalPages()) {
													m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1);
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
								case BUTTON_BACK:
									if (Menu.this instanceof PaginatedMenu) {
										PaginatedMenu m = (PaginatedMenu) Menu.this;

										if (getProperties().contains(Property.SAVABLE)) {

											if (!getProperties().contains(Property.SHAREABLE)) {
												if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) >= 1) {
													m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1);
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
												if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) < m.getInventory().getTotalPages() && (m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) >= 1) {
													m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1);
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

						ItemElement<?> hotKeyElement = getInventory().getItem(e.getRawSlot());
						if (hotKeyElement != null) {
							MenuClickEvent event = new Vent.Call<>(new MenuClickEvent(p, Menu.this, hotKeyElement)).run();
							if (event.isCancelled()) {
								e.setCancelled(true);
								return;
							}
							Click click = hotKeyElement.getAttachment();
							ClickElement clickElement = new ClickElement(p, e.getRawSlot(), e.getAction(), e.getClick(), hotKeyElement, e.getCursor(), e.getView());
							if (click != null) {
								click.apply(clickElement);
							}

							if (clickElement.getResult() != null) {
								e.setResult(clickElement.getResult());
							}
							if (e.getHotbarButton() != -1) {
								if (!clickElement.isHotbarAllowed()) {
									e.setCancelled(true);
								}
							}

							if (hotKeyElement.getType() != null) {
								ClickElement.Consumer consumer = clickElement.getConsumer();
								switch (hotKeyElement.getType()) {
									case TAKEAWAY:
										hotKeyElement.remove(false);
										break;
									case BUTTON_EXIT:
										if (consumer != null) {
											consumer.accept(clickElement.getElement(), false);
										}
										break;
									case BUTTON_NEXT:
										if (Menu.this instanceof PaginatedMenu) {
											PaginatedMenu m = (PaginatedMenu) Menu.this;

											if (getProperties().contains(Property.SAVABLE)) {
												if (!getProperties().contains(Property.SHAREABLE)) {
													m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1);
												} else {
													m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() + 1);
												}
												if (consumer != null) {
													consumer.accept(clickElement.getElement(), true);
												}
											} else {
												if (!getProperties().contains(Property.SHAREABLE)) {
													if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1) <= m.getInventory().getTotalPages()) {
														m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1);
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
									case BUTTON_BACK:
										if (Menu.this instanceof PaginatedMenu) {
											PaginatedMenu m = (PaginatedMenu) Menu.this;

											if (getProperties().contains(Property.SAVABLE)) {

												if (!getProperties().contains(Property.SHAREABLE)) {
													if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) >= 1) {
														m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1);
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
													if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) < m.getInventory().getTotalPages() && (m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) >= 1) {
														m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1);
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

						ItemElement<?> otherElement = getInventory().getItem(item);
						if (otherElement != null) {
							MenuClickEvent event = new Vent.Call<>(new MenuClickEvent(p, Menu.this, otherElement)).run();
							if (event.isCancelled()) {
								e.setCancelled(true);
								return;
							}
							Click click = otherElement.getAttachment();
							ClickElement clickElement = new ClickElement(p, e.getRawSlot(), e.getAction(), e.getClick(), otherElement, e.getCursor(), e.getView());
							if (click == null) {
								if (Menu.this.click != null) {
									Menu.this.click.apply(clickElement);
								}
							} else {
								click.apply(clickElement);
							}

							if (clickElement.getResult() != null) {
								e.setResult(clickElement.getResult());
							}

							if (e.getHotbarButton() != -1) {
								if (!clickElement.isHotbarAllowed()) {
									e.setCancelled(true);
								}
							}

							if (otherElement.getType() != null) {
								ClickElement.Consumer consumer = clickElement.getConsumer();
								switch (otherElement.getType()) {
									case TAKEAWAY:
										otherElement.remove(false);
										break;
									case BUTTON_EXIT:
										if (consumer != null) {
											consumer.accept(clickElement.getElement(), false);
										}
										break;
									case BUTTON_NEXT:
										if (Menu.this instanceof PaginatedMenu) {
											PaginatedMenu m = (PaginatedMenu) Menu.this;

											if (getProperties().contains(Property.SAVABLE)) {
												if (!getProperties().contains(Property.SHAREABLE)) {
													m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1);
												} else {
													m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() + 1);
												}
												if (consumer != null) {
													consumer.accept(clickElement.getElement(), true);
												}
											} else {
												if (!getProperties().contains(Property.SHAREABLE)) {
													if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1) <= m.getInventory().getTotalPages()) {
														m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1);
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
									case BUTTON_BACK:
										if (Menu.this instanceof PaginatedMenu) {
											PaginatedMenu m = (PaginatedMenu) Menu.this;

											if (getProperties().contains(Property.SAVABLE)) {

												if (!getProperties().contains(Property.SHAREABLE)) {
													if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) >= 1) {
														m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1);
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
													if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) < m.getInventory().getTotalPages() && (m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) >= 1) {
														m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1);
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
							return;
						}

						if (!e.isCancelled()) {

							ItemElement<?> element1 = new ItemElement<>().setPlayerAdded(true).setParent(getInventory()).setElement(e.getCurrentItem());
							MenuClickEvent event = new Vent.Call<>(new MenuClickEvent(p, Menu.this, element1)).run();
							if (event.isCancelled()) {
								e.setCancelled(true);
								return;
							}
							if (getProperties().contains(Property.SHAREABLE)) {
								if (getInventory().isPaginated()) {
									InventoryElement.Paginated inv = (InventoryElement.Paginated) getInventory();
									element1.setPage(inv.getGlobalSlot());
								}

							} else {
								element1.setPage(getInventory().getViewer((Player) e.getWhoClicked()).getPage());
							}

							if (Menu.this.click != null) {
								ClickElement element3 = new ClickElement((Player) e.getWhoClicked(), e.getRawSlot(), e.getAction(), e.getClick(), element1, e.getCursor(), e.getView());
								Menu.this.click.apply(element3);

								if (element3.getResult() != null) {
									e.setResult(element3.getResult());
								}

								if (e.getHotbarButton() != -1) {
									if (!element3.isHotbarAllowed()) {
										e.setCancelled(true);
									}
								}

								if (element3.isCancelled()) {
									e.setCancelled(true);
								}
							}

						}

					}

				}

				if (!e.isCancelled()) {

					ItemElement<?> el = new ItemElement<>().setPlayerAdded(true).setParent(getInventory()).setElement(e.getCursor());
					MenuClickEvent event = new Vent.Call<>(new MenuClickEvent(p, Menu.this, el)).run();
					if (event.isCancelled()) {
						e.setCancelled(true);
						return;
					}
					if (getProperties().contains(Property.SHAREABLE)) {
						if (getInventory().isPaginated()) {
							InventoryElement.Paginated inv = (InventoryElement.Paginated) getInventory();
							el.setPage(inv.getGlobalSlot());
						}

					} else {
						el.setPage(getInventory().getViewer((Player) e.getWhoClicked()).getPage());
					}

					if (Menu.this.click != null) {
						ClickElement element3 = new ClickElement((Player) e.getWhoClicked(), e.getRawSlot(), e.getAction(), e.getClick(), el, e.getCursor(), e.getView());
						Menu.this.click.apply(element3);

						if (element3.getResult() != null) {
							e.setResult(element3.getResult());
						}

						if (e.getHotbarButton() != -1) {
							if (!element3.isHotbarAllowed()) {
								e.setCancelled(true);
							}
						}

						if (element3.isCancelled()) {
							e.setCancelled(true);
						}
					}

				}

			}

		}

	}

	/**
	 * Private listener construction.
	 */
	static class Controller implements Listener {

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClose(InventoryCloseEvent e) {
			if (!(e.getInventory().getHolder() instanceof Instance)) return;
			if (!(e.getPlayer() instanceof Player))	return;
			Menu menu = ((Instance)e.getInventory().getHolder()).getMenu();
			if (menu.getType() != Type.PRINTABLE) {
				if (e.getView().getTopInventory().getSize() < menu.rows.slots) return;
			}

			Player p = (Player) e.getPlayer();

			if (menu.getProperties().contains(Property.LIVE_META) || menu.getProperties().contains(Property.ANIMATED)) {
				RenderedTask task = menu.getInventory().getViewer(p).getTask();
				if (task != null) {
					task.getTask().cancel();
				}
				Task t = TaskMonitor.getLocalInstance().get("Labyrinth:" + menu.hashCode() + ";slide-" + p.getUniqueId());
				if (t != null) {
					t.cancel();
				}
			}

			if (menu instanceof PrintableMenu) {
				menu.close(p); // verify that the correct packets are sent.
			}

			if (menu.close != null) {
				ClosingElement element = new ClosingElement(menu, (Player) e.getPlayer(), e.getView());
				menu.close.apply(element);

				if (element.isCancelled()) {
					menu.getInventory().open(p);
				}

			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onOpen(InventoryOpenEvent e) {
			if (!(e.getInventory().getHolder() instanceof Instance)) return;
			if (!(e.getPlayer() instanceof Player)) return;
			Menu menu = ((Instance)e.getInventory().getHolder()).getMenu();
			if (menu.open != null) {
				OpeningElement element = new OpeningElement(menu, (Player) e.getPlayer(), e.getView());

				menu.open.apply(element);

				if (element.isCancelled()) {
					menu.close((Player) e.getPlayer());
				}

			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onDrag(InventoryDragEvent e) {
			if (!(e.getInventory().getHolder() instanceof Instance)) return;
			if (!(e.getWhoClicked() instanceof Player))	return;
			Menu menu = ((Instance)e.getInventory().getHolder()).getMenu();
			ItemStack attempt = e.getCursor() != null ? e.getCursor() : e.getOldCursor();
			ItemElement<?> element = menu.getInventory().getItem(attempt);
			if (element == null) element = new ItemElement<>().setPlayerAdded(true).setParent(menu.getInventory()).setElement(attempt);
			MenuDragItemEvent event = new Vent.Call<>(new MenuDragItemEvent(menu, (Player) e.getWhoClicked(), element)).run();
			if (event.isCancelled()) e.setCancelled(true);
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClick(InventoryClickEvent e) {
			if (!(e.getInventory().getHolder() instanceof Instance)) return;
			if (!(e.getWhoClicked() instanceof Player))	return;
			Menu menu = ((Instance)e.getInventory().getHolder()).getMenu();

			if (menu.getType() != Type.PRINTABLE) {
				if (e.getView().getTopInventory().getSize() < menu.rows.slots) return;
			}

			Player p = (Player) e.getWhoClicked();
			if (e.getCurrentItem() != null) {
				ItemStack item = e.getCurrentItem();
				ItemElement<?> fixedSlotElement = menu.getInventory().getItem(i -> i.getSlot().isPresent() && e.getRawSlot() == i.getSlot().get() && item.getType() == i.getElement().getType() && i.getType() != ItemElement.ControlType.ITEM_FILLER && i.getType() != ItemElement.ControlType.ITEM_BORDER);
				if (fixedSlotElement != null) {
					MenuClickEvent event = new Vent.Call<>(new MenuClickEvent(p, menu, fixedSlotElement)).run();
					if (event.isCancelled()) {
						e.setCancelled(true);
						return;
					}
					Click click = fixedSlotElement.getAttachment();
					ClickElement clickElement = new ClickElement(p, e.getRawSlot(), e.getAction(), e.getClick(), fixedSlotElement, e.getCursor(), e.getView());
					if (click != null) {
						click.apply(clickElement);
					}
					if (clickElement.getResult() != null) {
						e.setResult(clickElement.getResult());
					}
					if (e.getHotbarButton() != -1) {
						if (!clickElement.isHotbarAllowed()) {
							e.setCancelled(true);
						}
					}

					if (fixedSlotElement.getType() != null) {
						ClickElement.Consumer consumer = clickElement.getConsumer();
						switch (fixedSlotElement.getType()) {
							case TAKEAWAY:
								fixedSlotElement.remove(false);
								break;
							case BUTTON_EXIT:
								if (consumer != null) {
									consumer.accept(clickElement.getElement(), false);
								}
								break;
							case BUTTON_NEXT:
								if (menu instanceof PaginatedMenu) {
									PaginatedMenu m = (PaginatedMenu) menu;

									if (menu.getProperties().contains(Property.SAVABLE)) {
										if (!menu.getProperties().contains(Property.SHAREABLE)) {
											m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1);
										} else {
											m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() + 1);
										}
										if (consumer != null) {
											consumer.accept(clickElement.getElement(), true);
										}
									} else {
										if (!menu.getProperties().contains(Property.SHAREABLE)) {
											if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1) <= m.getInventory().getTotalPages()) {
												m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1);
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
							case BUTTON_BACK:
								if (menu instanceof PaginatedMenu) {
									PaginatedMenu m = (PaginatedMenu) menu;

									if (menu.getProperties().contains(Property.SAVABLE)) {

										if (!menu.getProperties().contains(Property.SHAREABLE)) {
											if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) >= 0) {
												m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1);
												if (consumer != null) {
													consumer.accept(clickElement.getElement(), true);
												}
											} else {
												if (consumer != null) {
													consumer.accept(clickElement.getElement(), false);
												}
											}
										} else {
											if ((m.getInventory().getGlobalSlot().toNumber() - 1) >= 0) {
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
										if (!menu.getProperties().contains(Property.SHAREABLE)) {
											if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) < m.getInventory().getTotalPages() && (m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) >= 1) {
												m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1);
												if (consumer != null) {
													consumer.accept(clickElement.getElement(), true);
												}
											} else {
												if (consumer != null) {
													consumer.accept(clickElement.getElement(), false);
												}
											}
										} else {
											if ((m.getInventory().getGlobalSlot().toNumber() - 1) < m.getInventory().getTotalPages() && (m.getInventory().getGlobalSlot().toNumber() - 1) >= 0) {
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

					ItemElement<?> hotKeyElement = menu.getInventory().getItem(e.getRawSlot());
					if (hotKeyElement != null) {
						MenuClickEvent event = new Vent.Call<>(new MenuClickEvent(p, menu, hotKeyElement)).run();
						if (event.isCancelled()) {
							e.setCancelled(true);
							return;
						}
						Click click = hotKeyElement.getAttachment();
						ClickElement clickElement = new ClickElement(p, e.getRawSlot(), e.getAction(), e.getClick(), hotKeyElement, e.getCursor(), e.getView());
						if (click != null) {
							click.apply(clickElement);
						}

						if (clickElement.getResult() != null) {
							e.setResult(clickElement.getResult());
						}
						if (e.getHotbarButton() != -1) {
							if (!clickElement.isHotbarAllowed()) {
								e.setCancelled(true);
							}
						}

						if (hotKeyElement.getType() != null) {
							ClickElement.Consumer consumer = clickElement.getConsumer();
							switch (hotKeyElement.getType()) {
								case TAKEAWAY:
									hotKeyElement.remove(false);
									break;
								case BUTTON_EXIT:
									if (consumer != null) {
										consumer.accept(clickElement.getElement(), false);
									}
									break;
								case BUTTON_NEXT:
									if (menu instanceof PaginatedMenu) {
										PaginatedMenu m = (PaginatedMenu) menu;

										if (menu.getProperties().contains(Property.SAVABLE)) {
											if (!menu.getProperties().contains(Property.SHAREABLE)) {
												m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1);
											} else {
												m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() + 1);
											}
											if (consumer != null) {
												consumer.accept(clickElement.getElement(), true);
											}
										} else {
											if (!menu.getProperties().contains(Property.SHAREABLE)) {
												if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1) <= m.getInventory().getTotalPages()) {
													m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1);
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
								case BUTTON_BACK:
									if (menu instanceof PaginatedMenu) {
										PaginatedMenu m = (PaginatedMenu) menu;

										if (menu.getProperties().contains(Property.SAVABLE)) {

											if (!menu.getProperties().contains(Property.SHAREABLE)) {
												if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) >= 0) {
													m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1);
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), true);
													}
												} else {
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), false);
													}
												}
											} else {
												if ((m.getInventory().getGlobalSlot().toNumber() - 1) >= 0) {
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
											if (!menu.getProperties().contains(Property.SHAREABLE)) {
												if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) < m.getInventory().getTotalPages() && (m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) >= 1) {
													m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1);
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), true);
													}
												} else {
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), false);
													}
												}
											} else {
												if ((m.getInventory().getGlobalSlot().toNumber() - 1) < m.getInventory().getTotalPages() && (m.getInventory().getGlobalSlot().toNumber() - 1) >= 0) {
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

					ItemElement<?> otherElement = menu.getInventory().getItem(item);
					if (otherElement != null) {
						MenuClickEvent event = new Vent.Call<>(new MenuClickEvent(p, menu, otherElement)).run();
						if (event.isCancelled()) {
							e.setCancelled(true);
							return;
						}
						Click click = otherElement.getAttachment();
						ClickElement clickElement = new ClickElement(p, e.getRawSlot(), e.getAction(), e.getClick(), otherElement, e.getCursor(), e.getView());
						if (click == null) {
							if (menu.click != null) {
								menu.click.apply(clickElement);
							}
						} else {
							click.apply(clickElement);
						}

						if (clickElement.getResult() != null) {
							e.setResult(clickElement.getResult());
						}

						if (e.getHotbarButton() != -1) {
							if (!clickElement.isHotbarAllowed()) {
								e.setCancelled(true);
							}
						}

						if (otherElement.getType() != null) {
							ClickElement.Consumer consumer = clickElement.getConsumer();
							switch (otherElement.getType()) {
								case TAKEAWAY:
									otherElement.remove(false);
									break;
								case BUTTON_EXIT:
									if (consumer != null) {
										consumer.accept(clickElement.getElement(), false);
									}
									break;
								case BUTTON_NEXT:
									if (menu instanceof PaginatedMenu) {
										PaginatedMenu m = (PaginatedMenu) menu;

										if (menu.getProperties().contains(Property.SAVABLE)) {
											if (!menu.getProperties().contains(Property.SHAREABLE)) {
												m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1);
											} else {
												m.getInventory().setGlobalSlot(m.getInventory().getGlobalSlot().toNumber() + 1);
											}
											if (consumer != null) {
												consumer.accept(clickElement.getElement(), true);
											}
										} else {
											if (!menu.getProperties().contains(Property.SHAREABLE)) {
												if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1) <= m.getInventory().getTotalPages()) {
													m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() + 1);
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
								case BUTTON_BACK:
									if (menu instanceof PaginatedMenu) {
										PaginatedMenu m = (PaginatedMenu) menu;

										if (menu.getProperties().contains(Property.SAVABLE)) {

											if (!menu.getProperties().contains(Property.SHAREABLE)) {
												if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) >= 0) {
													m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1);
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), true);
													}
												} else {
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), false);
													}
												}
											} else {
												if ((m.getInventory().getGlobalSlot().toNumber() - 1) >= 0) {
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
											if (!menu.getProperties().contains(Property.SHAREABLE)) {
												if ((m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) < m.getInventory().getTotalPages() && (m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1) >= 1) {
													m.getInventory().getViewer(clickElement.getElement()).setPage(m.getInventory().getViewer(clickElement.getElement()).getPage().toNumber() - 1);
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), true);
													}
												} else {
													if (consumer != null) {
														consumer.accept(clickElement.getElement(), false);
													}
												}
											} else {
												if ((m.getInventory().getGlobalSlot().toNumber() - 1) < m.getInventory().getTotalPages() && (m.getInventory().getGlobalSlot().toNumber() - 1) >= 0) {
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
						return;
					}

					if (!e.isCancelled()) {

						ItemElement<?> element1 = new ItemElement<>().setPlayerAdded(true).setParent(menu.getInventory()).setElement(e.getCurrentItem());
						MenuClickEvent event = new Vent.Call<>(new MenuClickEvent(p, menu, element1)).run();
						if (event.isCancelled()) {
							e.setCancelled(true);
							return;
						}
						if (menu.getProperties().contains(Property.SHAREABLE)) {
							if (menu.getInventory().isPaginated()) {
								InventoryElement.Paginated inv = (InventoryElement.Paginated) menu.getInventory();
								element1.setPage(inv.getGlobalSlot());
							}

						} else {
							element1.setPage(menu.getInventory().getViewer((Player) e.getWhoClicked()).getPage());
						}

						if (menu.click != null) {
							ClickElement element3 = new ClickElement((Player) e.getWhoClicked(), e.getRawSlot(), e.getAction(), e.getClick(), element1, e.getCursor(), e.getView());
							menu.click.apply(element3);

							if (element3.getResult() != null) {
								e.setResult(element3.getResult());
							}

							if (e.getHotbarButton() != -1) {
								if (!element3.isHotbarAllowed()) {
									e.setCancelled(true);
								}
							}

							if (element3.isCancelled()) {
								e.setCancelled(true);
							}
						}

					}

				}

			}

			if (!e.isCancelled()) {

				ItemElement<?> el = new ItemElement<>().setPlayerAdded(true).setParent(menu.getInventory()).setElement(e.getCursor());
				MenuClickEvent event = new Vent.Call<>(new MenuClickEvent(p, menu, el)).run();
				if (event.isCancelled()) {
					e.setCancelled(true);
					return;
				}
				if (menu.getProperties().contains(Property.SHAREABLE)) {
					if (menu.getInventory().isPaginated()) {
						InventoryElement.Paginated inv = (InventoryElement.Paginated) menu.getInventory();
						el.setPage(inv.getGlobalSlot());
					}

				} else {
					el.setPage(menu.getInventory().getViewer((Player) e.getWhoClicked()).getPage());
				}

				if (menu.click != null) {
					ClickElement element3 = new ClickElement((Player) e.getWhoClicked(), e.getRawSlot(), e.getAction(), e.getClick(), el, e.getCursor(), e.getView());
					menu.click.apply(element3);

					if (element3.getResult() != null) {
						e.setResult(element3.getResult());
					}

					if (e.getHotbarButton() != -1) {
						if (!element3.isHotbarAllowed()) {
							e.setCancelled(true);
						}
					}

					if (element3.isCancelled()) {
						e.setCancelled(true);
					}
				}

			}

		}

	}

}
