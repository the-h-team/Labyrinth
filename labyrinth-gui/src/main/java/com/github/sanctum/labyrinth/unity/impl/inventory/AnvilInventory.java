package com.github.sanctum.labyrinth.unity.impl.inventory;


import com.github.sanctum.labyrinth.data.service.AnvilMechanics;
import com.github.sanctum.labyrinth.unity.construct.Menu;
import com.github.sanctum.labyrinth.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.unity.impl.ItemElement;
import java.util.Set;
import org.bukkit.entity.Player;

public class AnvilInventory extends InventoryElement {

	private final AnvilMechanics nms;

	private int containerId;

	private boolean visible;

	public AnvilInventory(String title, AnvilMechanics mechanics, Menu.Type type, Set<Menu.Property> properties, Menu.Rows rows) {
		super(title, type, properties, rows, true);
		this.nms = mechanics;
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public void open(Player player) {
		nms.handleInventoryCloseEvent(player);
		nms.setActiveContainerDefault(player);

		final Object container = nms.newContainerAnvil(player, this.getTitle());

		setElement(nms.toBukkitInventory(container));

		for (ItemElement<?> it : getAttachment()) {
			if (it.getSlot().isPresent()) {
				int slot = it.getSlot().get();
				if (slot == 0) {
					getElement().setItem(0, it.getElement());
				}
				if (slot == 1) {
					getElement().setItem(1, it.getElement());
				}
				if (slot == 2) {
					getElement().setItem(2, it.getElement());
				}
			}
		}

		containerId = nms.getNextContainerId(player, container);
		nms.sendPacketOpenWindow(player, containerId, this.getTitle());
		nms.setActiveContainer(player, container);
		nms.setActiveContainerId(container, containerId);
		nms.addActiveContainerSlotListener(container, player);

		visible = true;
	}

	public void close(Player player, boolean sendPacket) {
		if (!visible)
			throw new IllegalArgumentException("You can't close an inventory that isn't open!");
		visible = false;

		if (!sendPacket) {
			nms.handleInventoryCloseEvent(player);
		}
		nms.setActiveContainerDefault(player);
		nms.sendPacketCloseWindow(player, containerId);
	}

}
