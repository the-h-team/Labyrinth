package com.github.sanctum.labyrinth.library;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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
	 * @deprecated Use {@link Items#findMaterial(String)} instead!
	 * @return a material from query or null
	 */
	@Deprecated
	public static @Nullable Material getMaterial(String name) {
		return findMaterial(name);
	}

	public static @Nullable Material findMaterial(String name) {
		return Optional.ofNullable(MATERIAL_ALIAS.get(name.toLowerCase().replaceAll("_", ""))).orElseGet(() -> MATERIAL_ALIAS.entrySet().stream().filter(e -> StringUtils.use(e.getKey()).containsIgnoreCase(name)).map(Map.Entry::getValue).findFirst().orElse(null));
	}

	/**
	 * Build and convert and itemstack all in one function.
	 *
	 * @param fun The item builder to stack conversion.
	 * @return An item modification object.
	 */
	public static @NotNull ItemStack edit(Function<Item.Edit, ItemStack> fun) {
		return fun.apply(edit());
	}

	/**
	 * Build and convert and itemstack all in one function.
	 *
	 * @return An item modification object.
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
	@Deprecated
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
