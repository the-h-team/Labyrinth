package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.PrintableMenu;

class PrintableBuilder extends Menu.Builder<PrintableMenu, InventoryElement.Printable> {


	public PrintableBuilder(Menu.Type type) {
		super(type);
	}

	static class Factory implements Menu.BuilderFactory<PrintableBuilder, PrintableMenu, InventoryElement.Printable>{

		@Override
		public PrintableBuilder createBuilder() {
			return new PrintableBuilder(Menu.Type.PRINTABLE);
		}
	}
}
