package com.github.sanctum.labyrinth.unity.impl.menu.builder;

import com.github.sanctum.labyrinth.unity.construct.Menu;
import com.github.sanctum.labyrinth.unity.impl.menu.PaginatedMenu;
import com.github.sanctum.labyrinth.unity.impl.menu.PrintableMenu;
import com.github.sanctum.labyrinth.unity.impl.menu.SingularMenu;
import java.util.function.Function;
import java.util.function.Predicate;

public class MenuType<T extends Menu, V extends Menu.Builder<T>> {

	public static final MenuType<PaginatedMenu, PaginatedBuilder> PAGINATED = new MenuType<>(new PaginatedBuilder.Factory());

	public static final MenuType<SingularMenu, SingularBuilder> SINGULAR = new MenuType<>(new SingularBuilder.Factory());

	public static final MenuType<PrintableMenu, PrintableBuilder> PRINTABLE = new MenuType<>(new PrintableBuilder.Factory());

	private final Menu.BuilderFactory<V, T> factory;

	MenuType(Menu.BuilderFactory<V, T> factory) {
		this.factory = factory;
	}

	public <R extends Menu> R get(Predicate<Menu> pred, Function<Menu, R> function) {
		return Menu.get(pred, function);
	}

	public Menu.Builder<T> build() {
		return factory.createBuilder();
	}

}
