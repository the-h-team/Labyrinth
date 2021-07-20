package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Use of this direct builder {@link Item} demands instantiation/execution to be done ONLY on your
 * server's onEnable method.
 *
 * @author Hempfest
 */
@SuppressWarnings("ConstantConditions")
public class Item {

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
	}

	public Item setKey(String key) {
		this.key = new NamespacedKey(LabyrinthProvider.getInstance().getPluginInstance(), key);
		return this;
	}

	public Item setItem(char key, Material item) {
		recipeMap.put(key, item);
		return this;
	}

	public Item setItem(char key, ItemStack item) {
		recipeStackMap.put(key, item);
		return this;
	}

	public Item buildStack() {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.use(name).translate());
		item.setItemMeta(meta);
		this.item = item;
		return this;
	}

	public Item attachLore(List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore.stream().map(s -> StringUtils.use(s).translate()).collect(Collectors.toList()));
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
		while (it.hasNext()) {
			recipe = it.next();
			if (recipe != null && recipe.getResult().getType() == m) {
				it.remove();
			}
		}
	}

	public static void removeEntry(Item item) {

		Iterator<Recipe> it = Bukkit.recipeIterator();
		Recipe recipe;
		while (it.hasNext()) {
			recipe = it.next();
			if (recipe != null && recipe.getResult().equals(item.item)) {
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

	/**
	 * Used to easily color and customize a leather armor piece of choice
	 */
	public static class ColoredArmor {

		private final ItemStack BASE;
		private String TITLE;
		private Color COLOR;
		private List<String> LORE;

		protected ColoredArmor(@NotNull Piece piece) {
			switch (piece) {
				case HEAD:
					this.BASE = new ItemStack(Material.LEATHER_HELMET);
					break;
				case TORSO:
					this.BASE = new ItemStack(Material.LEATHER_CHESTPLATE);
					break;
				case LEGS:
					this.BASE = new ItemStack(Material.LEATHER_LEGGINGS);
					break;
				case FEET:
					this.BASE = new ItemStack(Material.LEATHER_BOOTS);
					break;
				default:
					throw new IllegalArgumentException(piece + " is not a valid armor piece.");
			}
		}

		/**
		 * Select a piece of armor to start customizing!
		 *
		 * @param piece The piece of armor to customize
		 * @return A coloredArmor instance.
		 */
		public static ColoredArmor select(@NotNull Piece piece) {
			return new ColoredArmor(piece);
		}

		/**
		 * Customize an entire set of leather armor.
		 *
		 * @return A coloredArmor instance.
		 */
		public static ColoredArmor prepare() {
			return new ColoredArmor(Piece.HEAD);
		}

		/**
		 * Change the title of this armor piece.
		 * <p>
		 * (Automatically color translated)
		 *
		 * @param text The title of the item.
		 * @return The same colored armor builder.
		 */
		public ColoredArmor setTitle(@NotNull String text) {
			this.TITLE = StringUtils.use(text).translate();
			return this;
		}

		/**
		 * Select a color for this armor piece
		 *
		 * @param c The {@link Color} of choice.
		 * @return The same colored armor builder.
		 */
		public ColoredArmor setColor(@NotNull Color c) {
			this.COLOR = c;
			return this;
		}

		/**
		 * Select a color for this armor piece via hexadecimal (Ex. 0x0000f)
		 *
		 * @param rgb The hex RGB value to use.
		 * @return The same colored armor builder.
		 */
		public ColoredArmor setColor(int rgb) {
			this.COLOR = Color.fromRGB(rgb);
			return this;
		}

		/**
		 * Select a color for this armor piece via R, G, B format.
		 *
		 * @param red   The red value to use
		 * @param green The green value to use
		 * @param blue  The blue value to use
		 * @return The same colored armor builder.
		 */
		public ColoredArmor setColor(int red, int green, int blue) {
			this.COLOR = Color.fromRGB(red, green, blue);
			return this;
		}

		/**
		 * Add some lore to this armor piece.
		 * <p>
		 * (Automatically color translated)
		 *
		 * @param text The line(s) of lore to add to this armor piece
		 * @return The same colored armor builder.
		 */
		public ColoredArmor setLore(@NotNull String... text) {
			List<String> list = new LinkedList<>();
			for (String t : text) {
				list.add(StringUtils.use(t).translate());
			}
			this.LORE = list;
			return this;
		}

		/**
		 * Build an entire set of armor based off your options.
		 *
		 * @return The finished armor set fully customized.
		 */
		public ItemStack[] buildAll() {
			List<ItemStack> i = new LinkedList<>(Arrays.asList(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS)));
			for (ItemStack it : i) {
				if (it.getItemMeta() instanceof LeatherArmorMeta) {
					LeatherArmorMeta meta = (LeatherArmorMeta) it.getItemMeta();
					if (this.COLOR != null) {
						meta.setColor(this.COLOR);
					}
					if (this.LORE != null && !this.LORE.isEmpty()) {
						meta.setLore(this.LORE);
					}
					if (this.TITLE != null) {
						meta.setDisplayName(this.TITLE);
					}
					it.setItemMeta(meta);
				} else
					throw new IllegalStateException("An invalid item type was found present, yell at the devs!!");
			}
			return i.toArray(new ItemStack[0]);
		}

		/**
		 * Build your piece of armor! Stare at yourself in awe in the mirror!
		 *
		 * @return The finished armor piece fully customized.
		 */
		public ItemStack build() {
			if (this.BASE.getItemMeta() instanceof LeatherArmorMeta) {
				LeatherArmorMeta meta = (LeatherArmorMeta) this.BASE.getItemMeta();
				if (this.COLOR != null) {
					meta.setColor(this.COLOR);
				}
				if (this.LORE != null && !this.LORE.isEmpty()) {
					meta.setLore(this.LORE);
				}
				if (this.TITLE != null) {
					meta.setDisplayName(this.TITLE);
				}
				this.BASE.setItemMeta(meta);
				return this.BASE;
			} else
				throw new IllegalStateException("An invalid item type was found present, yell at the devs!!");
		}

		/**
		 * An enum encapsulating bodily positioning
		 */
		public enum Piece {
			HEAD, TORSO, LEGS, FEET
		}

	}

	/**
	 * Used to encapsulate already-made ItemStacks or to make new ones
	 * and edit them.
	 */
	public static class Edit {

		private List<ItemStack> LIST;
		private ItemStack ITEM;
		private boolean LISTED = false;
		private List<String> LORE;
		private List<ItemFlag> FLAGS;
		private Map<Enchantment, Integer> ENCHANTS;
		private String TITLE;

		public Edit(Collection<ItemStack> items) {
			this.LISTED = true;
			List<ItemStack> temp = new LinkedList<>();
			for (ItemStack it : items) {
				temp.add(new ItemStack(it));
			}
			this.LIST = temp;
		}

		public Edit(ItemStack... i) {
			this.LISTED = true;
			List<ItemStack> temp = new LinkedList<>();
			for (ItemStack it : i) {
				temp.add(new ItemStack(it));
			}
			this.LIST = temp;
		}

		public Edit(ItemStack i) {
			Preconditions.checkArgument(i != null, "ItemStack cannot be null");
			this.ITEM = new ItemStack(i);
		}

		public Edit(Material mat) {
			if (mat == null) mat = Material.PAPER;
			this.ITEM = new ItemStack(mat);
		}

		public Edit setTitle(String text) {
			this.TITLE = StringUtils.use(text).translate();
			return this;
		}

		public Edit setAmount(int amount) {
			if (LISTED) {
				for (ItemStack i : LIST) {
					i.setAmount(amount);
				}
			} else {
				this.ITEM.setAmount(amount);
			}
			return this;
		}

		public Edit setType(Material type) {
			if (LISTED) {
				for (ItemStack i : LIST) {
					i.setType(type);
				}
			} else {
				this.ITEM.setType(type);
			}
			return this;
		}

		public Edit setLore(String... text) {
			List<String> list = new LinkedList<>();
			for (String s : text) {
				list.add(StringUtils.use(s).translate());
			}
			this.LORE = list;
			return this;
		}

		public Edit addEnchantment(Enchantment enchant, int level) {
			if (this.ENCHANTS == null) {
				this.ENCHANTS = new HashMap<>();
			}
			this.ENCHANTS.put(enchant, level);
			return this;
		}

		public Edit setFlags(ItemFlag... flag) {
			this.FLAGS = new LinkedList<>(Arrays.asList(flag));
			return this;
		}

		public List<ItemStack> finish(Consumer<Damageable> damage) {
			if (!LISTED) {
				return Collections.emptyList();
			}
			for (ItemStack it : this.LIST) {
				ItemMeta meta = it.getItemMeta();
				damage.accept((Damageable) meta);
				if (this.TITLE != null) {
					meta.setDisplayName(this.TITLE);
				}

				if (this.LORE != null && !this.LORE.isEmpty()) {
					meta.setLore(this.LORE);
				}

				if (this.FLAGS != null && !this.FLAGS.isEmpty()) {
					meta.addItemFlags(this.FLAGS.toArray(new ItemFlag[0]));
				}
				it.setItemMeta(meta);
				if (this.ENCHANTS != null && !this.ENCHANTS.isEmpty()) {
					for (Map.Entry<Enchantment, Integer> e : ENCHANTS.entrySet()) {
						it.addUnsafeEnchantment(e.getKey(), e.getValue());
					}
				}
			}

			return this.LIST;
		}

		public List<ItemStack> finish(Consumer<Damageable> damage, Consumer<ItemMeta> options) {
			if (!LISTED) {
				return Collections.emptyList();
			}
			for (ItemStack it : this.LIST) {
				ItemMeta meta = it.getItemMeta();
				damage.accept((Damageable) meta);
				options.accept(meta);
				if (this.TITLE != null) {
					meta.setDisplayName(this.TITLE);
				}

				if (this.LORE != null && !this.LORE.isEmpty()) {
					meta.setLore(this.LORE);
				}

				if (this.FLAGS != null && !this.FLAGS.isEmpty()) {
					meta.addItemFlags(this.FLAGS.toArray(new ItemFlag[0]));
				}

				it.setItemMeta(meta);
				if (this.ENCHANTS != null && !this.ENCHANTS.isEmpty()) {
					for (Map.Entry<Enchantment, Integer> e : ENCHANTS.entrySet()) {
						it.addUnsafeEnchantment(e.getKey(), e.getValue());
					}
				}
			}

			return this.LIST;
		}

		public List<ItemStack> finish() {
			if (!LISTED) {
				return Collections.emptyList();
			}
			for (ItemStack it : this.LIST) {
				ItemMeta meta = it.getItemMeta();
				if (this.TITLE != null) {
					meta.setDisplayName(this.TITLE);
				}

				if (this.LORE != null && !this.LORE.isEmpty()) {
					meta.setLore(this.LORE);
				}

				if (this.FLAGS != null && !this.FLAGS.isEmpty()) {
					meta.addItemFlags(this.FLAGS.toArray(new ItemFlag[0]));
				}

				it.setItemMeta(meta);
				if (this.ENCHANTS != null && !this.ENCHANTS.isEmpty()) {
					for (Map.Entry<Enchantment, Integer> e : ENCHANTS.entrySet()) {
						it.addUnsafeEnchantment(e.getKey(), e.getValue());
					}
				}
			}

			return this.LIST;
		}

		public ItemStack build(Consumer<Damageable> damage) {
			if (LISTED) {
				return this.LIST.get(0);
			}
			ItemMeta meta = this.ITEM.getItemMeta();
			damage.accept((Damageable) meta);
			if (this.TITLE != null) {
				meta.setDisplayName(this.TITLE);
			}

			if (this.LORE != null && !this.LORE.isEmpty()) {
				meta.setLore(this.LORE);
			}

			if (this.FLAGS != null && !this.FLAGS.isEmpty()) {
				meta.addItemFlags(this.FLAGS.toArray(new ItemFlag[0]));
			}

			this.ITEM.setItemMeta(meta);

			if (this.ENCHANTS != null && !this.ENCHANTS.isEmpty()) {
				for (Map.Entry<Enchantment, Integer> e : ENCHANTS.entrySet()) {
					this.ITEM.addUnsafeEnchantment(e.getKey(), e.getValue());
				}
			}

			return this.ITEM;
		}

		public ItemStack build(Consumer<Damageable> damage, Consumer<ItemMeta> options) {
			if (LISTED) {
				return this.LIST.get(0);
			}
			ItemMeta meta = this.ITEM.getItemMeta();
			damage.accept((Damageable) meta);
			options.accept(meta);
			if (this.TITLE != null) {
				meta.setDisplayName(this.TITLE);
			}

			if (this.LORE != null && !this.LORE.isEmpty()) {
				meta.setLore(this.LORE);
			}

			if (this.FLAGS != null && !this.FLAGS.isEmpty()) {
				meta.addItemFlags(this.FLAGS.toArray(new ItemFlag[0]));
			}

			this.ITEM.setItemMeta(meta);

			if (this.ENCHANTS != null && !this.ENCHANTS.isEmpty()) {
				for (Map.Entry<Enchantment, Integer> e : ENCHANTS.entrySet()) {
					this.ITEM.addUnsafeEnchantment(e.getKey(), e.getValue());
				}
			}

			return this.ITEM;
		}

		public ItemStack build() {
			if (LISTED) {
				return this.LIST.get(0);
			}
			ItemMeta meta = this.ITEM.getItemMeta();

			if (this.TITLE != null) {
				meta.setDisplayName(this.TITLE);
			}

			if (this.LORE != null && !this.LORE.isEmpty()) {
				meta.setLore(this.LORE);
			}

			if (this.FLAGS != null && !this.FLAGS.isEmpty()) {
				meta.addItemFlags(this.FLAGS.toArray(new ItemFlag[0]));
			}

			this.ITEM.setItemMeta(meta);

			if (this.ENCHANTS != null && !this.ENCHANTS.isEmpty()) {
				for (Map.Entry<Enchantment, Integer> e : ENCHANTS.entrySet()) {
					this.ITEM.addUnsafeEnchantment(e.getKey(), e.getValue());
				}
			}

			return this.ITEM;
		}


	}

	public static class CustomFirework {

		private final Location LOCATION;
		private FireworkEffect.Builder EFFECTS;

		protected CustomFirework(@NotNull Location location) {
			this.LOCATION = location;
		}

		public static CustomFirework from(Location location) {
			return new CustomFirework(location);
		}

		public CustomFirework addEffects(Consumer<FireworkEffect.Builder> operation) {
			FireworkEffect.Builder builder = FireworkEffect.builder();
			operation.accept(builder);
			this.EFFECTS = builder;
			return this;
		}

		public Firework build() {
			Firework f = (Firework) LOCATION.getWorld().spawnEntity(LOCATION, Entities.getEntity("firework"));
			FireworkMeta meta = f.getFireworkMeta();
			if (this.EFFECTS != null) {
				meta.addEffect(this.EFFECTS.build());
			}
			f.setFireworkMeta(meta);
			return f;
		}

		public Firework build(Consumer<FireworkMeta> operation) {
			Firework f = (Firework) LOCATION.getWorld().spawnEntity(LOCATION, Entities.getEntity("firework"));
			FireworkMeta meta = f.getFireworkMeta();
			operation.accept(meta);
			if (this.EFFECTS != null) {
				meta.addEffect(this.EFFECTS.build());
			}
			f.setFireworkMeta(meta);
			return f;
		}


	}


}
