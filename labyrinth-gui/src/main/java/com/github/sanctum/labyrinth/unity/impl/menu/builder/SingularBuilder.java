package com.github.sanctum.labyrinth.unity.impl.menu.builder;

import com.github.sanctum.labyrinth.unity.construct.Menu;
import com.github.sanctum.labyrinth.unity.impl.menu.SingularMenu;

class SingularBuilder extends Menu.Builder<SingularMenu> {


	public SingularBuilder(Menu.Type type) {
		super(type);
	}

	static class Factory implements Menu.BuilderFactory<SingularBuilder, SingularMenu>{

		@Override
		public SingularBuilder createBuilder() {
			return new SingularBuilder(Menu.Type.SINGULAR);
		}
	}
}
