package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Hempfest
 */
public final class Items {
	private static final Map<String, Material> MATERIAL_ALIAS = new HashMap<>();

	static {
		for (Material material : Material.values()) {
			MATERIAL_ALIAS.put(material.name().toLowerCase().replace("_", ""), material);
		}
	}

	/**
	 * Query for a material by its name ignoring case and underscores.
	 *
	 * @param name the material to search for disregarding caps and underscores
	 * @return a material from query or null
	 */
	public static @Nullable Material getMaterial(String name) {
		return MATERIAL_ALIAS.get(name.toLowerCase().replaceAll("_", ""));
	}

	/**
	 * @param i
	 * @return
	 */
	public static @NotNull Item.Edit edit(ItemStack i) {
		return new Item.Edit(i);
	}

	/**
	 * @param mat
	 * @return
	 */
	public static @NotNull Item.Edit edit(Material mat) {
		return new Item.Edit(mat);
	}

	/**
	 * @param i
	 * @return
	 */
	public static @NotNull Item.Edit edit(ItemStack... i) {
		return new Item.Edit(i);
	}

	/**
	 * @param i
	 * @return
	 */
	public static @NotNull Item.Edit edit(Collection<ItemStack> i) {
		return new Item.Edit(i);
	}

	/**
	 * @return
	 */
	public static @NotNull Item.Edit edit() {
		return new Item.Edit(getMaterial("dirt"));
	}

	/**
	 * Query for a material by its name or alias, if instead a base 64 value is provided
	 * the value will attempt to be applied, the resulting {@link ItemStack}
	 * if any operations failed to run will be a basic {@link Material#PLAYER_HEAD}.
	 *
	 * @param value the material alias or base64 value
	 * @return an improvised item based on demand
	 */
	public static @NotNull ItemStack improvise(String value) {
		Material mat = getMaterial(value);
		if (mat != null) {
			return new ItemStack(mat);
		}
		mat = getMaterial("skullitem");

		if (mat == null) {
			mat = getMaterial("playerhead");
		}
		assert mat != null;
		return new ItemStack(mat);
	}

	private Items() {
	}
}
