package com.github.sanctum.labyrinth.library;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullItem {

	private static final LinkedList<SkullItem> log = new LinkedList<>();

	private final String holder;

	private ItemStack head;

	public SkullItem(String holder, ItemStack head) {
		this.holder = holder;
		this.head = head;
		SkullItem.log.add(this);
	}

	/**
	 * Get's the player's UUID as a string.
	 * @return The player's UUID string
	 */
	public String getHolder() {
		return holder;
	}

	/**
	 * Get's the player head object.
	 * @return The player's head skinned.
	 */
	public ItemStack getItem() {
		return head;
	}

	/**
	 * Get the entire cached list of player head objects.
	 * @return Get's the full log of cached player heads.
	 */
	public static LinkedList<SkullItem> getLog() {
		return log;
	}

	/**
	 * Use this if the player's head is not automatically cached (May cause slight lag temporarily in loading)
	 * You may also use it to update the players head in the instance of a skin change.
	 */
	public void updateHead() {
		boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			Material type = Material.matchMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");
			ItemStack item = new ItemStack(type, 1);

			if (!isNew) {
				item.setDurability((short) 3);
			}

			SkullMeta meta = (SkullMeta) item.getItemMeta();
			if (!meta.hasOwner()) {
				meta.setOwningPlayer(p);
			}
			item.setItemMeta(meta);
			this.head = item;
		}
	}

	public static class Head {

		/**
		 * Query for a specified player head by username.
		 * @param name The player name to query for.
		 * @return The specified player's head if not null
		 */
		public static ItemStack find(String name) {
			return SkullItem.getLog().stream().filter(s -> Bukkit.getOfflinePlayer(UUID.fromString(s.getHolder())).getName().equals(name)).map(SkullItem::getItem).findFirst().orElse(null);
		}

		/**
		 * Query for a specified player UUID head.
		 * @param id The player UUID to query for.
		 * @return The specified player's head if not null.
		 */
		public static ItemStack find(UUID id) {
			return SkullItem.getLog().stream().filter(s -> UUID.fromString(s.getHolder()).equals(id)).map(SkullItem::getItem).findFirst().orElse(null);
		}
	}

}
