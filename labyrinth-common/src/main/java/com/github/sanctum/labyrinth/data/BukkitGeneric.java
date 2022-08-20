package com.github.sanctum.labyrinth.data;

import com.github.sanctum.panther.annotation.Note;
import com.github.sanctum.panther.annotation.Ordinal;
import com.github.sanctum.panther.file.Generic;
import com.github.sanctum.panther.file.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BukkitGeneric implements Generic {

	// set a node variable
	Node node;

	public boolean isItemStack() {
		ItemStack itemStack = node.get(ItemStack.class);
		if (itemStack == null) {
			Object o = node.get();
			if (o instanceof Map) {
				Map<String, Object> map = (Map<String, Object>) o;
				return map.containsKey("org.bukkit.inventory.ItemStack");
			}
		}
		return itemStack != null;
	}

	public ItemStack getItemStack() {
		return node.get(ItemStack.class);
	}

	public boolean isItemStackList() {
		ItemStack[] itemStack = node.get(ItemStack[].class);
		if (itemStack == null) {
			Object o = node.get();
			if (o instanceof List) {
				List<Map<String, Object>> list = (List<Map<String, Object>>) o;
				return list.get(0).containsKey("org.bukkit.inventory.ItemStack");
			}
		}
		return itemStack != null;
	}

	public List<ItemStack> getItemStackList() {
		if (!isItemStackList()) return new ArrayList<>();
		return new ArrayList<>(Arrays.asList(node.get(ItemStack[].class)));
	}

	public boolean isLocation() {
		Location location = node.get(Location.class);
		if (location == null) {
			Object o = node.get();
			if (o instanceof Map) {
				Map<String, Object> map = (Map<String, Object>) o;
				return map.containsKey("org.bukkit.Location");
			}
		}
		return location != null;
	}

	public Location getLocation() {
		return node.get(Location.class);
	}

	public boolean isLocationList() {
		Location[] locations = node.get(Location[].class);
		if (locations == null) {
			Object o = node.get();
			if (o instanceof List) {
				List<Map<String, Object>> list = (List<Map<String, Object>>) o;
				return list.get(0).containsKey("org.bukkit.Location");
			}
		}
		return locations != null;
	}

	public List<Location> getLocationList() {
		if (!isLocationList()) return new ArrayList<>();
		return new ArrayList<>(Arrays.asList(node.get(Location[].class)));
	}

	@Ordinal(20)
	@Note("It's imperative that you have this in your class!")
	protected void setNode(@NotNull Node node) {
		this.node = node;
	}

}
