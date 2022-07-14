package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.panther.annotation.Experimental;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ListElement<T> extends Menu.Element<Menu.Populate<?>, Set<ItemElement<?>>> {

	protected Comparator<? super ItemElement<?>> comparator = Comparator.comparing(ItemElement::getName);
	protected Predicate<? super ItemElement<?>> predicate = itemElement -> true;
	private final List<T> list;
	private Supplier<List<T>> supplier;
	private int max = 5;
	private Menu.Populate<T> populator;
	private InventoryElement parent;

	public ListElement(List<T> list) {
		this.list = list;
	}

	@Experimental(dueTo = "This option might actually allow you to cache a menu instance while maintaining list reference updates.")
	public ListElement(Supplier<List<T>> supplier) {
		this.list = null;
		this.supplier = supplier;
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

	public ListElement<T> setFilter(Predicate<? super ItemElement<T>> predicate) {
		this.predicate = (Predicate<? super ItemElement<?>>) predicate;
		return this;
	}

	public ListElement<T> setComparator(Comparator<? super ItemElement<T>> comparator) {
		this.comparator = (Comparator<? super ItemElement<?>>) comparator;
		return this;
	}

	public InventoryElement getParent() {
		return parent;
	}

	@Override
	public Set<ItemElement<?>> getAttachment() {
		Set<ItemElement<?>> elements = new HashSet<>();
		if (list != null) {
			for (T t : list) {
				ItemElement<T> element = new ItemElement<>(t).setParent(getParent());
				populator.accept(t, element);
				elements.add(element);
			}
		} else {
			for (T t : supplier.get()) {
				ItemElement<T> element = new ItemElement<>(t).setParent(getParent());
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
