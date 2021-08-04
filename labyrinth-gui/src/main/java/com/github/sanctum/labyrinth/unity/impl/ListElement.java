package com.github.sanctum.labyrinth.unity.impl;

import com.github.sanctum.labyrinth.unity.construct.Menu;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListElement<T> extends Menu.Element<Menu.Populate<?>, Set<ItemElement<?>>> {

	private final List<T> list;

	private boolean propagate;

	private int max;

	private Menu.Populate<T> populator;

	private InventoryElement parent;

	public ListElement(List<T> list) {
		this.list = list;
	}

	@Override
	public Menu.Populate<?> getElement() {
		return this.populator;
	}

	public ListElement<T> setLimit(int max) {
		this.max = max;
		return this;
	}

	public ListElement<T> setPropagate(boolean propagate) {
		this.propagate = propagate;
		return this;
	}

	public ListElement<T> setParent(InventoryElement parent) {
		this.parent = parent;
		return this;
	}

	public InventoryElement getParent() {
		return parent;
	}

	@Override
	public Set<ItemElement<?>> getAttachment() {
		Set<ItemElement<?>> elements = new HashSet<>();
		if (propagate) {
			int slot = 0;
			for (T t : list) {
				if (slot + 1 > max) {
					slot = 0;
				}
				ItemElement<?> element = new ItemElement<>().setParent(getParent()).setSlot(slot);
				populator.accept(t, element);
				elements.add(element);
				slot++;
			}
		} else {
			for (T t : list) {
				ItemElement<?> element = new ItemElement<>().setParent(getParent());
				populator.accept(t, element);
				elements.add(element);
			}
		}
		return elements;
	}

	public ListElement<T> setPopulate(Menu.Populate<T> populator) {
		this.populator = populator;
		return this;
	}

}
