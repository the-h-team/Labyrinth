package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.SingularMenu;

class SingularBuilder extends Menu.Builder<SingularMenu, InventoryElement.Normal> {


	public SingularBuilder(Menu.Type type) {
		super(type);
	}

	static class Factory implements Menu.BuilderFactory<SingularBuilder, SingularMenu, InventoryElement.Normal>{

		@Override
		public SingularBuilder createBuilder() {
			return new SingularBuilder(Menu.Type.SINGULAR);
		}
	}
}
