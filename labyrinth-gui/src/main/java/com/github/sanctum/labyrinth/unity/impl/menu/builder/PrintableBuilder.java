package com.github.sanctum.labyrinth.unity.impl.menu.builder;

import com.github.sanctum.labyrinth.unity.construct.Menu;
import com.github.sanctum.labyrinth.unity.impl.menu.PrintableMenu;

class PrintableBuilder extends Menu.Builder<PrintableMenu> {


	public PrintableBuilder(Menu.Type type) {
		super(type);
	}

	static class Factory implements Menu.BuilderFactory<PrintableBuilder, PrintableMenu>{

		@Override
		public PrintableBuilder createBuilder() {
			return new PrintableBuilder(Menu.Type.PRINTABLE);
		}
	}
}
