package com.github.sanctum.labyrinth.unity.construct;

import com.github.sanctum.labyrinth.unity.impl.InventoryElement;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PagedPlayer {

	private int page;

	private final UUID id;

	private final InventoryElement element;

	public PagedPlayer(UUID id, InventoryElement element) {
		this.element = element;
		this.page = 1;
		this.id = id;
	}

	public InventoryElement getElement() {
		return element;
	}

	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(this.id);
	}

	public InventoryElement.Page getPage() {
		return getElement().getPage(page);
	}

	public void setPage(int page) {
		this.page = page;
	}
}
