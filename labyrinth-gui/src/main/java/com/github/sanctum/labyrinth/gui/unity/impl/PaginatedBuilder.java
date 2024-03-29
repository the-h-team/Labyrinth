package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.PaginatedMenu;

class PaginatedBuilder extends Menu.Builder<PaginatedMenu, InventoryElement.Paginated> {


	public PaginatedBuilder(Menu.Type type) {
		super(type);
	}

	static class Factory implements Menu.BuilderFactory<PaginatedBuilder, PaginatedMenu, InventoryElement.Paginated>{

		@Override
		public PaginatedBuilder createBuilder() {
			return new PaginatedBuilder(Menu.Type.PAGINATED);
		}
	}
}
