package com.github.sanctum.labyrinth.unity.impl.menu.builder;

import com.github.sanctum.labyrinth.unity.construct.Menu;
import com.github.sanctum.labyrinth.unity.impl.menu.PaginatedMenu;

class PaginatedBuilder extends Menu.Builder<PaginatedMenu> {


	public PaginatedBuilder(Menu.Type type) {
		super(type);
	}

	static class Factory implements Menu.BuilderFactory<PaginatedBuilder, PaginatedMenu>{

		@Override
		public PaginatedBuilder createBuilder() {
			return new PaginatedBuilder(Menu.Type.PAGINATED);
		}
	}
}
