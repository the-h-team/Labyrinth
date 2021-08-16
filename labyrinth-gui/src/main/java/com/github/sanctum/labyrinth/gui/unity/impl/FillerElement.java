package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class FillerElement<T> extends Menu.Element<InventoryElement, Set<ItemElement<?>>> {

	private T t;
	private final Set<ItemElement<?>> elements = new HashSet<>();
	private final InventoryElement element;

	public FillerElement(InventoryElement element) {
		this.element = element;
		this.t = null;
	}

	public <R> FillerElement<R> setData(R t) {
		this.t = (T) t;
		return (FillerElement<R>) this;
	}

	public FillerElement<T> add(Consumer<ItemElement<?>> edit) {
		for (int i : getElement().getParent().getSize().getSlots(Menu.Panel.MIDDLE)) {
			ItemElement<T> e = new ItemElement<>(t).setType(ItemElement.ControlType.ITEM_BORDER).setParent(element).setSlot(i);
			edit.accept(e);
			elements.add(e);
		}
		return this;
	}


	@Override
	public InventoryElement getElement() {
		return this.element;
	}

	@Override
	public Set<ItemElement<?>> getAttachment() {
		return this.elements;
	}

}
