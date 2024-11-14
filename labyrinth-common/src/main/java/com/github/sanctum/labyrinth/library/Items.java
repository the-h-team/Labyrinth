package com.github.sanctum.labyrinth.library;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.github.sanctum.panther.container.PantherEntry;
import com.github.sanctum.panther.container.PantherEntryMap;
import com.github.sanctum.panther.container.PantherMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Hempfest
 */
public final class Items {
	static final PantherMap<String, Material> cache = new PantherEntryMap<>();
	static {
		for (Material m : Material.values()) {
			cache.put(m.name().toLowerCase().replace("_", ""), m);
		}
	}

	public static @Nullable Material findMaterial(String name) {
		return Optional.ofNullable(cache.get(name.toLowerCase().replace("_", ""))).orElse(cache.stream().filter(m -> StringUtils.use(m.getValue().name()).containsIgnoreCase(name.toLowerCase().replace("_", ""))).findFirst().map(PantherEntry.Modifiable::getValue).orElse(null));
	}


	/**
	 * Build and convert an item-stack all in one function.
	 *
	 * @return An item modification object.
	 */
	public static @NotNull Item.Edit edit() {
		return new Item.Edit(findMaterial("dirt"));
	}

	/**
	 * Build and convert an item-stack all in one function.
	 *
	 * @param fun The item builder to stack conversion.
	 * @return An item modification object.
	 */
	public static @NotNull ItemStack edit(Function<Item.Edit, ItemStack> fun) {
		return fun.apply(edit());
	}

	/**
	 * Build and convert an item-stack all in one function.
	 *
	 * @param fun The item builder to stack conversion.
	 * @return An item modification object.
	 */
	public static @NotNull ItemStack edit(ItemStack toEdit, Function<Item.Edit, ItemStack> fun) {
		return fun.apply(edit().setItem(toEdit));
	}
}
