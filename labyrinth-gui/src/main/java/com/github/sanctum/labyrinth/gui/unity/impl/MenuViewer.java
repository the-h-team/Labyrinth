package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.RenderedTask;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

public class MenuViewer {

	private int page;

	private Inventory inventory;

	private RenderedTask task;

	private final UUID id;

	private final InventoryElement element;

	public MenuViewer(UUID id, InventoryElement element) {
		this.element = element;
		this.page = 1;
		this.id = id;
	}

	void setTask(RenderedTask task) {
		this.task = task;
	}

	public InventoryElement getInventory() {
		return element;
	}

	public void setElement(Inventory inventory) {
		this.inventory = inventory;
	}

	public Inventory getElement() {
		if (inventory == null) {
			int total = 0;
			if (getInventory().isPaginated()) {
				total = ((InventoryElement.Paginated)getInventory()).getTotalPages();
			}
			this.inventory = Bukkit.createInventory(Menu.Instance.of(element.menu, getPlayer().getPlayer()), getInventory().getParent().getSize().getSize(), StringUtils.use(MessageFormat.format(getInventory().title, getPage().toNumber(), total)).translate());
		}
		if (getInventory().getParent().getProperties().contains(Menu.Property.REFILLABLE)) {
			if (!UniformedComponents.accept(Arrays.asList(inventory.getContents())).filter(Objects::nonNull).findAny().isPresent()) {
				if (getInventory().isPaginated()) {
					if (!getInventory().getParent().getProperties().contains(Menu.Property.LIVE_META)) {
					BorderElement<?> border = (BorderElement<?>) getInventory().getElement(e -> e instanceof BorderElement);
					if (border != null) {
						for (ItemElement<?> element : border.getAttachment()) {
							Optional<Integer> i = element.getSlot();
							i.ifPresent(integer -> this.inventory.setItem(integer, element.getElement()));
						}
					}
						for (ItemElement<?> element : getPage().getAttachment()) {
							if (!inventory.contains(element.getElement())) {
								inventory.addItem(element.getElement());
							}
						}
						for (ItemElement<?> element : getInventory().getAttachment()) {
							Optional<Integer> i = element.getSlot();
							i.ifPresent(integer -> inventory.setItem(integer, element.getElement()));
						}
						FillerElement<?> filler = (FillerElement<?>) getInventory().getElement(e -> e instanceof FillerElement);
						if (filler != null) {
							for (ItemElement<?> el : filler.getAttachment()) {
								int slot = el.getSlot().orElse(0);
								if (this.inventory.getItem(slot) == null) {
									this.inventory.setItem(slot, el.getElement());
								}
							}
						}
					}
				} else {
					BorderElement<?> border = (BorderElement<?>) getInventory().getElement(e -> e instanceof BorderElement);
					if (border != null) {
						for (ItemElement<?> element : border.getAttachment()) {
							Optional<Integer> i = element.getSlot();
							i.ifPresent(integer -> this.inventory.setItem(integer, element.getElement()));
						}
					}
					for (ItemElement<?> element : getInventory().getContents()) {
						if (!inventory.contains(element.getElement())) {
							inventory.addItem(element.getElement());
						}
					}
					for (ItemElement<?> element : getInventory().getAttachment()) {
						Optional<Integer> i = element.getSlot();
						i.ifPresent(integer -> inventory.setItem(integer, element.getElement()));
					}
					FillerElement<?> filler = (FillerElement<?>) getInventory().getElement(e -> e instanceof FillerElement);
					if (filler != null) {
						for (ItemElement<?> el : filler.getAttachment()) {
							int slot = el.getSlot().orElse(0);
							if (this.inventory.getItem(slot) == null) {
								this.inventory.setItem(slot, el.getElement());
							}
						}
					}
				}
			}
		}
		return inventory;
	}

	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(this.id);
	}

	public RenderedTask getTask() {
		return task;
	}

	public InventoryElement.Page getPage() {
		if (getInventory().isPaginated()) {
			InventoryElement.Paginated i = (InventoryElement.Paginated) getInventory();
			return i.getPage(page);
		}
		return new InventoryElement.Page(1, getInventory());
	}

	public void setPage(int page) {
		this.page = page;
	}
}
