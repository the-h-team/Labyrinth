package com.github.sanctum.labyrinth.gui.builder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * A fully built Paginated Menu object. Fully interfaced with custom specified logic
 */
public final class PaginatedMenu {

	private final PaginatedBuilder builder;

	protected PaginatedMenu(PaginatedBuilder builder) {
		this.builder = builder;
	}

	/**
	 * Open the menu for a specified player
	 *
	 * @param p The player to open the menu for.
	 */
	public void open(Player p) {
		builder.inv = Bukkit.createInventory(null, 54, builder.title.replace("{PAGE}", "" + (builder.page + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		p.openInventory(builder.adjust().getInventory());
	}

	/**
	 * Open the menu @ a specified page for a specified player
	 *
	 * @param p The player to open the menu for.
	 * @param page The page to open the menu @.
	 */
	public void open(Player p, int page) {
		builder.inv = Bukkit.createInventory(null, 54, builder.title.replace("{PAGE}", "" + (builder.page + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		p.openInventory(builder.adjust(page).getInventory());
	}

	/**
	 * Update the collection used within the menu.
	 *
	 * @param collection The string collection to update with.
	 */
	public void recollect(Collection<String> collection) {
		builder.collection = new LinkedList<>(collection);
	}

	/**
	 * Clear cache, remove un-used handlers.
	 */
	public void unregister() {
		HandlerList.unregisterAll(builder.getListener());
	}

	/**
	 * Get the unique ID of the menu object.
	 *
	 * @return The menu objects unique ID.
	 */
	public UUID getId() {
		return builder.getId();
	}

}
