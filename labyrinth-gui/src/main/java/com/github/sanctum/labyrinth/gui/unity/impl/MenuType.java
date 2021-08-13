package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.PaginatedMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.PrintableMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.SingularMenu;

public class MenuType<T extends Menu, V extends Menu.Builder<T>> {

	public static final MenuType<PaginatedMenu, PaginatedBuilder> PAGINATED = new MenuType<>(new PaginatedBuilder.Factory());

	public static final MenuType<SingularMenu, SingularBuilder> SINGULAR = new MenuType<>(new SingularBuilder.Factory());

	public static final MenuType<PrintableMenu, PrintableBuilder> PRINTABLE = new MenuType<>(new PrintableBuilder.Factory());

	private final Menu.BuilderFactory<V, T> factory;

	MenuType(Menu.BuilderFactory<V, T> factory) {
		this.factory = factory;
	}

	public Menu.Builder<T> build() {
		return factory.createBuilder();
	}

}
