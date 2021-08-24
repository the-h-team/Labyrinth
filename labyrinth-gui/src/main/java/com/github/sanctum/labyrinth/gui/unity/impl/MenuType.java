package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.PaginatedMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.PrintableMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.SingularMenu;

public class MenuType<T extends Menu, K extends InventoryElement, V extends Menu.Builder<T, K>> {

	public static final MenuType<PaginatedMenu, InventoryElement.Paginated, PaginatedBuilder> PAGINATED = new MenuType<>(new PaginatedBuilder.Factory());

	public static final MenuType<SingularMenu, InventoryElement.Normal, SingularBuilder> SINGULAR = new MenuType<>(new SingularBuilder.Factory());

	public static final MenuType<PrintableMenu, InventoryElement.Printable, PrintableBuilder> PRINTABLE = new MenuType<>(new PrintableBuilder.Factory());

	private final Menu.BuilderFactory<V, T, K> factory;

	MenuType(Menu.BuilderFactory<V, T, K> factory) {
		this.factory = factory;
	}

	public Menu.Builder<T, K> build() {
		return factory.createBuilder();
	}

}
