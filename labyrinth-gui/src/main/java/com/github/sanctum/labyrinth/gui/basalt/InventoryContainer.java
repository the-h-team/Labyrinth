package com.github.sanctum.labyrinth.gui.basalt;

import com.github.sanctum.labyrinth.data.service.AnvilMechanics;
import com.github.sanctum.panther.container.PantherEntryMap;
import com.github.sanctum.panther.container.PantherMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class InventoryContainer {

	private final Inventory inventory;
	private final InventoryProperties properties;
	private final PantherMap<UUID, Inventory> inventoryMap = new PantherEntryMap<>();

	public InventoryContainer(Inventory inventory, InventoryProperties properties) {
		this.inventory = inventory;
		this.properties = properties;
	}

	public @Nullable Inventory getInventory() {
		return inventory;
	}

	public @Nullable Inventory getInventory(@NotNull Player player) {
		return inventoryMap.get(player.getUniqueId());
	}

	public @Nullable InventorySnapshot newSnapshot() {
		if (inventory == null) return null;
		return new InventorySnapshot(inventory);
	}

	public @Nullable InventorySnapshot newSnapshot(@NotNull Player player) {
		if (inventory == null) return null;
		return new InventorySnapshot(inventoryMap.computeIfAbsent(player.getUniqueId(), () -> {
			// use properties to create copy of menu.
			switch (properties.getFormat()) {
				case ANVIL:
					AnvilMechanics mechanics = AnvilMechanics.getInstance();
					final AnvilMechanics.Container container = mechanics.newContainer(player, properties.getTitle(), false);
					Inventory ne = container.getBukkitInventory();
					for (int i = 0; i < this.inventory.getSize() + 1; i++) {
						ne.setItem(i, this.inventory.getItem(i));
					}
					return ne;
				case NORMAL:
				case PAGINATED:
					Inventory inv = Bukkit.createInventory(null, 54, properties.getTitle());
					for (int i = 0; i < this.inventory.getSize() + 1; i++) {
						inv.setItem(i, this.inventory.getItem(i));
					}
					return inv;
			}
			// this should never happen.
			return null;
		}));
	}

}
