package com.github.sanctum.labyrinth.unity.construct;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PagedPlayer {

	private int page;

	private final UUID id;

	public PagedPlayer(UUID id) {
		this.page = 1;
		this.id = id;
	}

	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(this.id);
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
