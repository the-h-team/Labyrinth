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
		return new Item.Edit(findMaterial("dirt"));
	}

	private Items() {
	}
}
