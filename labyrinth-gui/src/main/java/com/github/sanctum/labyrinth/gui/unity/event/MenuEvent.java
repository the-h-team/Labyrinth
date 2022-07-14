package com.github.sanctum.labyrinth.gui.unity.event;

import com.github.sanctum.labyrinth.event.DefaultEvent;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import org.jetbrains.annotations.NotNull;

public abstract class MenuEvent extends DefaultEvent {
	final Menu menu;

	public MenuEvent(@NotNull Menu menu, boolean isAsync) {
		super(isAsync);
		this.menu = menu;
	}

	public MenuEvent(@NotNull Menu menu) {
		this(menu, false);
	}

	public Menu getMenu() {
		return menu;
	}
}
