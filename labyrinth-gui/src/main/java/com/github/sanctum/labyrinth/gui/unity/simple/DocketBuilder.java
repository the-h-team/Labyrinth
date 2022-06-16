package com.github.sanctum.labyrinth.gui.unity.simple;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.PaginatedMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.PrintableMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.SingularMenu;
import com.github.sanctum.labyrinth.gui.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.gui.unity.impl.MenuType;
import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class DocketBuilder<T, V extends InventoryElement> implements Docket<T> {

	final Plugin plugin = LabyrinthProvider.getInstance().getPluginInstance();
	Consumer<V> inventoryModifier;
	Menu.Type type;
	String title;
	String key;
	Menu.Rows rows;
	Menu.Open open;
	Menu.Close close;

	public DocketBuilder<T, V> setRows(Menu.Rows rows) {
		this.rows = rows;
		return this;
	}

	public DocketBuilder<T, V> setTitle(String title) {
		this.title = title;
		return this;
	}

	public DocketBuilder<T, V> setType(Menu.Type type) {
		this.type = type;
		return this;
	}

	public DocketBuilder<T, V> setStock(Consumer<V> consumer) {
		this.inventoryModifier = consumer;
		return this;
	}

	public DocketBuilder<T, V> setOpenEvent(Menu.Open open) {
		this.open = open;
		return this;
	}

	public DocketBuilder<T, V> setCloseEvent(Menu.Close close) {
		this.close = close;
		return this;
	}

	@Override
	public @NotNull Menu toMenu() {
		switch (type) {
			case PAGINATED:
				Menu.Builder<PaginatedMenu, InventoryElement.Paginated> paginated = MenuType.PAGINATED.build().setHost(plugin).setTitle(title).setSize(rows);
				if (key != null) paginated.setKey(key).setProperty(Menu.Property.CACHEABLE);
				if (open != null) paginated.setOpenEvent(open);
				if (close != null) paginated.setCloseEvent(close);
				if (inventoryModifier != null) paginated.setStock((Consumer<InventoryElement.Paginated>) inventoryModifier);
				return paginated.join();
			case PRINTABLE:
				Menu.Builder<PrintableMenu, InventoryElement.Printable> printable = MenuType.PRINTABLE.build().setHost(plugin).setTitle(title).setSize(rows);
				if (key != null) printable.setKey(key).setProperty(Menu.Property.CACHEABLE);
				if (open != null) printable.setOpenEvent(open);
				if (close != null) printable.setCloseEvent(close);
				if (inventoryModifier != null) printable.setStock((Consumer<InventoryElement.Printable>) inventoryModifier);
				return printable.join();
			case SINGULAR:
				Menu.Builder<SingularMenu, InventoryElement.Normal> singular = MenuType.SINGULAR.build().setHost(plugin).setTitle(title).setSize(rows);
				if (key != null) singular.setKey(key).setProperty(Menu.Property.CACHEABLE);
				if (open != null) singular.setOpenEvent(open);
				if (close != null) singular.setCloseEvent(close);
				if (inventoryModifier != null) singular.setStock((Consumer<InventoryElement.Normal>) inventoryModifier);
				return singular.join();
			default:
				throw new IllegalStateException("Unexpected value: " + type);
		}
	}

	@Override
	public @NotNull Type getType() {
		return Type.BUILDER;
	}
}
