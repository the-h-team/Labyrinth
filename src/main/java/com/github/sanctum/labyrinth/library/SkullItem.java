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

	public String getHolder() {
		return holder;
	}

	public ItemStack getItem() {
		return head;
	}

	public static LinkedList<SkullItem> getLog() {
		return log;
	}

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

		public static ItemStack find(String name) {
			return SkullItem.getLog().stream().filter(s -> Bukkit.getOfflinePlayer(UUID.fromString(s.getHolder())).getName().equals(name)).map(SkullItem::getItem).findFirst().orElse(null);
		}

		public static ItemStack find(UUID id) {
			return SkullItem.getLog().stream().filter(s -> UUID.fromString(s.getHolder()).equals(id)).map(SkullItem::getItem).findFirst().orElse(null);
		}
	}

}
