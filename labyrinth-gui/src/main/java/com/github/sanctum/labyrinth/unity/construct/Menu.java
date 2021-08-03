package com.github.sanctum.labyrinth.unity.construct;

import com.github.sanctum.labyrinth.task.Asynchronous;
import com.github.sanctum.labyrinth.unity.impl.ClickElement;
import com.github.sanctum.labyrinth.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.unity.impl.ItemElement;
import com.github.sanctum.labyrinth.unity.impl.OpeningElement;
import com.github.sanctum.labyrinth.unity.impl.menu.PaginatedMenu;
import com.github.sanctum.labyrinth.unity.impl.menu.PrintableMenu;
import com.github.sanctum.labyrinth.unity.impl.menu.SingularMenu;
import com.github.sanctum.labyrinth.unity.impl.ClosingElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

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

	protected Open open;
	protected Click click;
	protected Close close;

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

	public static <T extends Menu> MenuOptional<T> get(Class<T> type, Predicate<Menu> predicate) {
		T menu = null;
		for (Menu m : menus) {
			if (type.isAssignableFrom(m.getClass())) {
				if (predicate.test(m)) {
					menu = (T) m;
				}
			}
		}
		return MenuOptional.ofNullable(menu).supplyClass(type);
	}

	public abstract InventoryElement getInventory();



	public final Optional<String> getKey() {
		return Optional.ofNullable(this.key);
	}

	public final Type getType() {
		return type;
	}

	public final Rows getSize() {
		return rows;
	}

	public final Set<Property> getProperties() {
		return properties;
	}

	protected final void registerController() {
		if (this.controller == null) {
			this.controller = new Controller();
			Bukkit.getPluginManager().registerEvents(this.controller, host);
		}
	}

	public final <T, R> T addElement(Element<T, R> element) {
		elements.add(element);
		return element.getElement();
	}

	public final <R> R getElement(Predicate<Element<?, ?>> function) {
		for (Element<?, ?> e : elements) {
			if (function.test(e)) {
				return (R) e;
			}
		}
		return null;
	}

	public final <T extends Menu> T addAction(Click click) {
		this.click = click;
		return (T) this;
	}

	@FunctionalInterface
	public interface Click {

		void apply(ClickElement element);

	}

	@FunctionalInterface
	public interface Close {

		void apply(ClosingElement element);

	}

	@FunctionalInterface
	public interface Open {

		void apply(OpeningElement element);

	}

	@FunctionalInterface
	public interface Populate<T> {

		void accept(T value, ItemElement<?> element);

	}

	public static abstract class Element<T, V> {

		public abstract T getElement();

		public abstract V getAttachment();

	}

	public enum Property {

		ANIMATED,
		CACHEABLE,
		LIVE_META,
		SHAREABLE;

	}

	public enum Type {

		PAGINATED,
		PRINTABLE,
		SINGULAR,


	}

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

		public int getSlots() {
			return slots;
		}
	}

	public static final class Builder<T extends Menu> {

		private final Class<T> tClass;

		private Plugin host;

		private String title;

		private Close close;

		private Consumer<InventoryElement> inventoryEdit;

		private Open open;

		private String key;

		private final Set<Property> properties = new HashSet<>();

		private Rows size;

		protected Builder(Class<T> tClass) {
			this.tClass = tClass;
		}

		public static <T extends Menu> Builder<T> using(Class<T> menuClass) {
			return new Builder<>(menuClass);
		}

		public Builder<T> setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder<T> setHost(Plugin plugin) {
			this.host = plugin;
			return this;
		}

		public Builder<T> setSize(Rows rows) {
			this.size = rows;
			return this;
		}

		public Builder<T> setKey(String key) {
			this.key = key;
			return this;
		}

		public Builder<T> setProperty(Property... properties) {
			this.properties.addAll(Arrays.asList(properties));
			return this;
		}

		public Builder<T> editInventory(Consumer<InventoryElement> edit) {
			this.inventoryEdit = edit;
			return this;
		}

		public Builder<T> setCloseEvent(Close close) {
			this.close = close;
			return this;
		}

		public Builder<T> setOpenEvent(Open open) {
			this.open = open;
			return this;
		}

		public T initialize() {
			T menu;
			switch (tClass.getSimpleName().toLowerCase()) {
				case "paginatedmenu":
					menu = (T) new PaginatedMenu(this.host, this.title, this.size, Type.PAGINATED);
					break;
				case "printablemenu":
					menu = (T) new PrintableMenu(this.host, this.title, this.size, Type.PRINTABLE);
					break;
				default:
					menu = (T) new SingularMenu(this.host, this.title, this.size, Type.SINGULAR);
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
			menu.properties.addAll(properties);
			menu.registerController();
			return menu;
		}


	}

	private class Controller implements Listener {

		@EventHandler(priority = EventPriority.NORMAL)
		public void onClose(InventoryCloseEvent e) {
			if (!(e.getPlayer() instanceof Player))
				return;
			if (e.getView().getTopInventory().getSize() < rows.slots)
				return;
			if (e.getInventory().equals(getInventory().getElement())) {

				Player p = (Player) e.getPlayer();

				if (getProperties().contains(Property.LIVE_META) || getProperties().contains(Property.ANIMATED)) {
					// TODO: Shutdown logic for running tasks
					Asynchronous task = getInventory().getTask(p);
					if (task != null) {
						task.cancelTask();
					}
				}

				if (close != null) {
					ClosingElement element = new ClosingElement((Player) e.getPlayer(), e.getView());

					close.apply(element);

					if (element.isCancelled()) {
						getInventory().open(p);
					}

				}

				if (Menu.this instanceof PaginatedMenu) {
					PaginatedMenu paginatedMenu = (PaginatedMenu) Menu.this;
					paginatedMenu.getInventory().setPage(0);
				}


				p.updateInventory();
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onMove(InventoryMoveItemEvent e) {
			if (e.getSource().equals(getInventory().getElement())) {
				e.setCancelled(true);
			}
		}

		@EventHandler(priority = EventPriority.NORMAL)
		public void onDrag(InventoryDragEvent e) {
			if (!(e.getWhoClicked() instanceof Player))
				return;
			if (e.getView().getTopInventory().getSize() < rows.slots)
				return;
			if (!e.getInventory().equals(getInventory().getElement())) return;

			if (e.getInventory().equals(getInventory().getElement())) {
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

			if (e.getHotbarButton() != -1) {
				e.setCancelled(true);
				return;
			}

			if (!e.getInventory().equals(getInventory().getElement())) return;

			if (e.getClickedInventory() == e.getInventory()) {

				Player p = (Player) e.getWhoClicked();

				switch (e.getAction()) {
					case HOTBAR_MOVE_AND_READD:
					case HOTBAR_SWAP:
					case MOVE_TO_OTHER_INVENTORY:
						e.setResult(Event.Result.DENY);
						break;
				}
				if (e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();

					ItemElement<?> element = getInventory().match(i -> i.getSlot().isPresent() && e.getRawSlot() == i.getSlot().get() && item.getType() == i.getElement().getType());
					if (element != null) {
						Click click = element.getAttachment();
						ClickElement clickElement = new ClickElement(p, e.getRawSlot(), element, e.getView());
						click.apply(clickElement);

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
										if ((m.getInventory().getPage() + 1) <= m.getInventory().getPageCount()) {
											m.getInventory().setPage(m.getInventory().getPage() + 1);
											if (consumer != null) {
												consumer.accept(clickElement.getElement(), true);
											}
										} else {
											if (consumer != null) {
												consumer.accept(clickElement.getElement(), false);
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
										if ((m.getInventory().getPage() - 1) < m.getInventory().getPageCount() && (m.getInventory().getPage() - 1) >= 1) {
											m.getInventory().setPage(m.getInventory().getPage() - 1);
											if (consumer != null) {
												consumer.accept(clickElement.getElement(), true);
											}
										} else {
											if (consumer != null) {
												consumer.accept(clickElement.getElement(), false);
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
							ClickElement clickElement = new ClickElement(p, e.getRawSlot(), element2, e.getView());
							click.apply(clickElement);

							if (element2.getNavigation() != null) {
								ClickElement.Consumer consumer = clickElement.getConsumer();
								switch (element2.getNavigation()) {
									case Back:
										if (consumer != null) {
											consumer.accept(clickElement.getElement(), true);
										}
										break;
									case Next:
										if (Menu.this instanceof PaginatedMenu) {
											PaginatedMenu m = (PaginatedMenu) Menu.this;
											if ((m.getInventory().getPage() + 1) <= m.getInventory().getPageCount()) {
												m.getInventory().setPage(m.getInventory().getPage() + 1);
											}
										}
										if (consumer != null) {
											consumer.accept(clickElement.getElement(), true);
										}
										break;
									case Previous:
										if (Menu.this instanceof PaginatedMenu) {
											PaginatedMenu m = (PaginatedMenu) Menu.this;
											if ((m.getInventory().getPage() - 1) < m.getInventory().getPageCount() && (m.getInventory().getPage() - 1) >= 1) {
												m.getInventory().setPage(m.getInventory().getPage() - 1);
											}
										}
										if (consumer != null) {
											consumer.accept(clickElement.getElement(), true);
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
						ClickElement element = new ClickElement((Player) e.getWhoClicked(), e.getRawSlot(), new ItemElement<>().setElement(e.getCurrentItem()), e.getView());
						Menu.this.click.apply(element);

						if (element.isCancelled()) {
							e.setCancelled(true);
						}
					}

				}

			}

		}

	}

}
