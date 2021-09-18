package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.PaginatedMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.PrintableMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.SingularMenu;
import java.util.function.Predicate;

public class MenuType<T extends Menu, K extends InventoryElement, V extends Menu.Builder<T, K>> {

	/**
	 * <h3>Meta:</h3> A menu with multiple pages.
	 * <pre></pre>
	 * <h3>Example:</h3>
	 * <pre>{@code MenuType.PAGINATED.build()
	 *                 .setTitle("&6&lHeads {0}/{1}")
	 *                 .setSize(Menu.Rows.ONE)
	 *                 .setHost(Plugin)
	 *                 .setKey("test")
	 *                 .setProperty(Menu.Property.CACHEABLE, Menu.Property.RECURSIVE)
	 *                 .setStock(i -> i.addItem(new ListElement<>(CustomHead.Manager.getHeads()).setLimit(2).setPopulate((value, element) -> {
	 *                     element.setElement(value.get());
	 *                     element.setElement(edit -> edit.setTitle(value.name()).build());
	 *                     element.setClick(c -> {
	 *                         c.setCancelled(true);
	 *                         c.setHotbarAllowed(false);
	 *                     });
	 *                 })).addItem(b -> b.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle("&5Next").build()).setType(ItemElement.ControlType.BUTTON_NEXT).setSlot(4))
	 *                         .addItem(b -> b.setElement(it -> it.setItem(SkullType.ARROW_BLUE_LEFT.get()).setTitle("&5Previous").build()).setType(ItemElement.ControlType.BUTTON_BACK).setSlot(3)))
	 *                 .orGet(me -> me instanceof PaginatedMenu && me.getKey().isPresent() && me.getKey().get().equals("test"));}</pre>
	 */
	public static final MenuType<PaginatedMenu, InventoryElement.Paginated, PaginatedBuilder> PAGINATED = new MenuType<>(new PaginatedBuilder.Factory());

	/**
	 * <h3>Meta:</h3> A menu with a singular known page.
	 * <pre></pre>
	 * <h3>Example:</h3>
	 * <pre>{@code MenuType.SINGULAR.build()
	 *                 .setTitle("&6&lMenu")
	 *                 .setSize(Menu.Rows.ONE)
	 *                 .setHost(Plugin)
	 *                 .setKey("test")
	 *                 .setProperty(Menu.Property.CACHEABLE, Menu.Property.RECURSIVE)
	 *                 .setStock(i -> i.addItem(b -> b.setElement(it ->
	 *                         it.setItem(SkullType.ARROW_BLUE_RIGHT.get())
	 *                                 .setTitle("&eLogs")
	 *                                 .setLore("========",
	 *                                     "Test text.",
	 *                                     "Hello world.",
	 *                                     "========")
	 *                                 .build())
	 *                         .setSlot(4)
	 *                         .setClick(click -> {
	 *                             click.setCancelled(true);
	 *
	 *                             Player p = click.getElement();
	 *                             // Menu#open(p); <- open our menu
	 *
	 *                         }))
	 *                         .addItem(b -> b.setElement(it ->
	 *                                 it.setItem(SkullType.ARROW_BLUE_LEFT.get())
	 *                                         .setTitle("&5Players")
	 *                                         .addEnchantment(Enchantment.ARROW_DAMAGE, 1000)
	 *                                         .setFlags(ItemFlag.HIDE_ENCHANTS)
	 *                                         .build())
	 *                                 .setSlot(3)
	 *                                 .setClick(click -> {
	 *                                     click.setCancelled(true);
	 *
	 *                                     Player p = click.getElement();
	 *                                     // Menu#open(p); <- open our menu
	 *
	 *                                 })))
	 *                 .orGet(me -> me instanceof SingularMenu && me.getKey().isPresent() && me.getKey().get().equals("test"));}</pre>
	 */
	public static final MenuType<SingularMenu, InventoryElement.Normal, SingularBuilder> SINGULAR = new MenuType<>(new SingularBuilder.Factory());

	/**
	 * <h3>Meta:</h3> A type-writer like menu.
	 * <pre></pre>
	 * <h3>Example:</h3>
	 * <pre>{@code MenuType.PRINTABLE.build()
	 *                 .setTitle("&6&lPrinter")
	 *                 .setSize(Menu.Rows.ONE)
	 *                 .setHost(Plugin)
	 *                 .setKey("test")
	 *                 .setStock(i -> i.addItem(b -> b.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).build()).setSlot(0).setClick(c -> {
	 *                     c.setCancelled(true);
	 *                     c.setHotbarAllowed(false);
	 *                 }))).join()
	 *                 .addAction(click -> {
	 *                     click.setCancelled(true);
	 *                     click.setHotbarAllowed(false);
	 *                     if (click.getSlot() == 2) {
	 *                         String[] arguments = click.getParent().getName().split(" ");
	 *                         click.getElement().sendMessage(String.join(" ", arguments));
	 *                     }
	 *
	 *                 });}</pre>
	 */
	public static final MenuType<PrintableMenu, InventoryElement.Printable, PrintableBuilder> PRINTABLE = new MenuType<>(new PrintableBuilder.Factory());

	private final Menu.BuilderFactory<V, T, K> factory;

	MenuType(Menu.BuilderFactory<V, T, K> factory) {
		this.factory = factory;
	}

	public Menu.Builder<T, K> build() {
		return factory.createBuilder();
	}

	public boolean exists(String key) {
		return Menu.getHistory().stream().anyMatch(m -> m.getKey().map(key::equals).orElse(false));
	}

	public T get(Predicate<Menu> predicate) {
		for (Menu m : Menu.getHistory()) {
			if (predicate.test(m)) {
				return (T) m;
			}
		}
		return null;
	}

}
