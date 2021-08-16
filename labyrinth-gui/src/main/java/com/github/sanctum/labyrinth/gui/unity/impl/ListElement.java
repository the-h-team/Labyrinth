package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ListElement<T> extends Menu.Element<Menu.Populate<?>, Set<ItemElement<?>>> {

	protected Comparator<? super ItemElement<?>> comparator = Comparator.comparing(ItemElement::getName);
	protected Predicate<? super ItemElement<?>> predicate = itemElement -> true;
	private final List<T> list;
	private int max = 5;
	private Menu.Populate<T> populator;
	private InventoryElement parent;

	public ListElement(List<T> list) {
		this.list = list;
	}

	@Override
	public Menu.Populate<?> getElement() {
		return this.populator;
	}

	public int getLimit() {
		return max;
	}

	public ListElement<T> setLimit(int max) {
		this.max = max;
		return this;
	}

	public ListElement<T> setParent(InventoryElement parent) {
		this.parent = parent;
		return this;
	}

	public ListElement<T> setFilter(Predicate<? super ItemElement<?>> predicate) {
		this.predicate = predicate;
		return this;
	}

	public ListElement<T> setComparator(Comparator<? super ItemElement<?>> comparator) {
		this.comparator = comparator;
		return this;
	}

	public InventoryElement getParent() {
		return parent;
	}

	@Override
	public Set<ItemElement<?>> getAttachment() {
		Set<ItemElement<?>> elements = new HashSet<>();
		for (T t : list) {
			ItemElement<T> element = new ItemElement<>(t).setParent(getParent());
			populator.accept(t, element);
			elements.add(element);
		}
		return elements;
	}

	public ListElement<T> setPopulate(Menu.Populate<T> populator) {
		this.populator = populator;
		return this;
	}

}
