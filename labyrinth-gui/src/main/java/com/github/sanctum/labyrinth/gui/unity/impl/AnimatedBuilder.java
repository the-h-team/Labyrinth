package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.SingularMenu;

class AnimatedBuilder extends Menu.Builder<SingularMenu, InventoryElement.Animated> {


	public AnimatedBuilder(Menu.Type type) {
		super(type);
		properties.add(Menu.Property.ANIMATED);
	}

	static class Factory implements Menu.BuilderFactory<AnimatedBuilder, SingularMenu, InventoryElement.Animated>{

		@Override
		public AnimatedBuilder createBuilder() {
			return new AnimatedBuilder(Menu.Type.SINGULAR);
		}
	}
}
