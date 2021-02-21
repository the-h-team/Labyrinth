package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.event.ItemStackBuildEvent;
import com.github.sanctum.labyrinth.event.NewItemCreationEvent;
import com.google.common.collect.MapMaker;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class Item implements Serializable {

	private static final long serialVersionUID = 794224011026322910L;

	private static final LinkedList<Item> cache = new LinkedList<>();

	private final ConcurrentMap<Character, Material> recipeMap = new MapMaker().
			weakKeys().
			weakValues().
			makeMap();

	private final ConcurrentMap<Character, ItemStack> recipeStackMap = new MapMaker().
			weakKeys().
			weakValues().
			makeMap();

	private final Material mat;

	private final String name;

	private NamespacedKey key;

	private ShapedRecipe recipe;

	private ItemStack item;

	public Item(Material appearance, String itemName) {
		this.mat = appearance;
		this.name = itemName;
		Item.cache.add(this);
		NewItemCreationEvent event = new NewItemCreationEvent(appearance, name);
		Bukkit.getPluginManager().callEvent(event);
	}

	public Item setKey(String key) {
		this.key = new NamespacedKey(Labyrinth.getInstance(), key);
		return this;
	}

	public Item setItem(Character key, Material item) {
		recipeMap.put(key, item);
		return this;
	}

	public Item setItem(Character key, ItemStack item) {
		recipeStackMap.put(key, item);
		return this;
	}

	public Item buildStack() {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.translate(name));
		item.setItemMeta(meta);
		ItemStackBuildEvent event = new ItemStackBuildEvent(name, item);
		Bukkit.getPluginManager().callEvent(event);
		this.item = event.getItem();
		return this;
	}

	public Item attachLore(List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore.stream().map(StringUtils::translate).collect(Collectors.toList()));
		item.setItemMeta(meta);
		return this;
	}

	public Item addEnchant(Enchantment e, int level) {
		ItemStack i = item;
		i.addUnsafeEnchantment(e, level);
		this.item = i;
		return this;
	}

	@SuppressWarnings("deprecation")
	public Item shapeRecipe(String... shape) {
		ShapedRecipe recipe = new ShapedRecipe(key, item);
		List<String> list = Arrays.asList(shape);
		recipe.shape(list.get(0), list.get(1), list.get(2));
		if (!this.recipeStackMap.isEmpty()) {
			if (this.recipeStackMap.get(list.get(0).charAt(0)) != null) {
				recipe.setIngredient((list.get(0)).charAt(0), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(0).charAt(0))));
			}
			if (this.recipeStackMap.get(list.get(0).charAt(1)) != null) {
				recipe.setIngredient((list.get(0)).charAt(1), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(0).charAt(1))));
			}
			if (this.recipeStackMap.get(list.get(0).charAt(2)) != null) {
				recipe.setIngredient((list.get(0)).charAt(2), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(0).charAt(2))));
			}
			if (this.recipeStackMap.get(list.get(1).charAt(0)) != null) {
				recipe.setIngredient((list.get(1)).charAt(0), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(1).charAt(0))));
			}
			if (this.recipeStackMap.get(list.get(1).charAt(1)) != null) {
				recipe.setIngredient((list.get(1)).charAt(1), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(1).charAt(1))));
			}
			if (this.recipeStackMap.get(list.get(1).charAt(2)) != null) {
				recipe.setIngredient((list.get(1)).charAt(2), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(1).charAt(2))));
			}
			if (this.recipeStackMap.get(list.get(2).charAt(0)) != null) {
				recipe.setIngredient((list.get(2)).charAt(0), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(2).charAt(0))));
			}
			if (this.recipeStackMap.get(list.get(2).charAt(1)) != null) {
				recipe.setIngredient((list.get(2)).charAt(1), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(2).charAt(1))));
			}
			if (this.recipeStackMap.get(list.get(2).charAt(2)) != null) {
				recipe.setIngredient((list.get(2)).charAt(2), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(2).charAt(2))));
			}
		}
		if (!this.recipeMap.isEmpty()) {
			if (this.recipeMap.get(list.get(0).charAt(0)) != null) {
				recipe.setIngredient((list.get(0)).charAt(0), this.recipeMap.get(list.get(0).charAt(0)));
			}
			if (this.recipeMap.get(list.get(0).charAt(1)) != null) {
				recipe.setIngredient((list.get(0)).charAt(1), this.recipeMap.get(list.get(0).charAt(1)));
			}
			if (this.recipeMap.get(list.get(0).charAt(2)) != null) {
				recipe.setIngredient((list.get(0)).charAt(2), this.recipeMap.get(list.get(0).charAt(2)));
			}
			if (this.recipeMap.get(list.get(1).charAt(0)) != null) {
				recipe.setIngredient((list.get(1)).charAt(0), this.recipeMap.get(list.get(1).charAt(0)));
			}
			if (this.recipeMap.get(list.get(1).charAt(1)) != null) {
				recipe.setIngredient((list.get(1)).charAt(1), this.recipeMap.get(list.get(1).charAt(1)));
			}
			if (this.recipeMap.get(list.get(1).charAt(2)) != null) {
				recipe.setIngredient((list.get(1)).charAt(2), this.recipeMap.get(list.get(1).charAt(2)));
			}
			if (this.recipeMap.get(list.get(2).charAt(0)) != null) {
				recipe.setIngredient((list.get(2)).charAt(0), this.recipeMap.get(list.get(2).charAt(0)));
			}
			if (this.recipeMap.get(list.get(2).charAt(1)) != null) {
				recipe.setIngredient((list.get(2)).charAt(1), this.recipeMap.get(list.get(2).charAt(1)));
			}
			if (this.recipeMap.get(list.get(2).charAt(2)) != null) {
				recipe.setIngredient((list.get(2)).charAt(2), this.recipeMap.get(list.get(2).charAt(2)));
			}
		}
		this.recipe = recipe;
		return this;
	}

	public void register() {
		Bukkit.addRecipe(recipe);
	}

	public static LinkedList<Item> getCache() {
		return cache;
	}

	public static void removeDefault(Material m) {

		Iterator<Recipe> it = Bukkit.recipeIterator();
		Recipe recipe;
		while(it.hasNext())
		{
			recipe = it.next();
			if (recipe != null && recipe.getResult().getType() == m)
			{
				it.remove();
			}
		}
	}

	public static void removeEntry(Item item) {

		Iterator<Recipe> it = Bukkit.recipeIterator();
		Recipe recipe;
		while(it.hasNext())
		{
			recipe = it.next();
			if (recipe != null && recipe.getResult().equals(item.item))
			{
				Item.cache.remove(item);
				it.remove();
			}
		}
	}

	public static Item getRegistration(Material type) {
		return Item.cache.stream().filter(i -> i.item.getType() == type).findFirst().orElse(null);
	}

	public static Item getRegistration(String name) {
		return Item.cache.stream().filter(i -> Objects.requireNonNull(i.item.getItemMeta()).getDisplayName().equals(name)).findFirst().orElse(null);
	}


}
