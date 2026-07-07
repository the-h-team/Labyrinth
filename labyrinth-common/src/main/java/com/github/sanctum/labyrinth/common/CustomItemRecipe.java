package com.github.sanctum.labyrinth.common;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.container.LegacyContainer;
import com.github.sanctum.panther.annotation.Note;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherEntry;
import com.github.sanctum.panther.util.Check;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
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
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Use of this direct builder {@link CustomItemRecipe} demands instantiation/execution to be done ONLY on your
 * server's onEnable method.
 *
 * @author Hempfest
 */
public class CustomItemRecipe {

	private static final LinkedList<CustomItemRecipe> cache = new LinkedList<>();

	private final ConcurrentMap<Character, Material> recipeMap = new MapMaker().
			makeMap();
	private final ConcurrentMap<Character, ItemStack> recipeStackMap = new MapMaker().
			makeMap();

	private Material mat;

	private String name;

	private NamespacedKey key;

	private ShapedRecipe recipe;

	private ItemStack item;

	public CustomItemRecipe(Material appearance, String displayName) {
		this();
		this.mat = appearance;
		this.name = displayName;
	}

	public CustomItemRecipe(String displayName) {
		this();
		this.name = displayName;
	}

	public CustomItemRecipe() {
		CustomItemRecipe.cache.add(this);
	}

	@Deprecated
	@Note("Internal usage only!")
	public CustomItemRecipe setKey(String key) {
		this.key = new NamespacedKey(LabyrinthProvider.getInstance().getPluginInstance(), key);
		return this;
	}

	public CustomItemRecipe setKey(String key, Plugin holder) {
		this.key = new NamespacedKey(holder, key);
		return this;
	}

	public CustomItemRecipe setItem(char key, Material item) {
		recipeMap.put(key, item);
		return this;
	}

	public CustomItemRecipe setItem(char key, ItemStack item) {
		recipeStackMap.put(key, item);
		return this;
	}

	public CustomItemRecipe process() {
		ItemStack item = new ItemStack(Check.forNull(mat, "Material cannot but null!"));
		ItemMeta meta = item.getItemMeta();
		if (name != null) {
			meta.setDisplayName(StringUtils.use(name).translate());
		}
		item.setItemMeta(meta);
		this.item = item;
		return this;
	}

	public CustomItemRecipe.Edit edit() {
		Edit edit = new Edit(this);
		if (this.item != null) edit.setItem(item);
		if (mat != null) edit.setType(mat);
		if (name != null) edit.setTitle(name);
		return edit;
	}

	public CustomItemRecipe shape(Workbench workbench) {
		StringBuilder top = new StringBuilder();
		StringBuilder middle = new StringBuilder();
		StringBuilder bottom = new StringBuilder();
		PantherCollection<PantherEntry.Modifiable<WorkbenchSlot, Character>> set = workbench.get();
		for (int i = 0; i < 9; i++) {
			if (i <= set.size()) {
				PantherEntry.Modifiable<WorkbenchSlot, Character> slot = set.get(i);
				int key = slot.getKey().toInt();
				if (key == 0 || key == 1 || key == 2) {
					top.append(slot.getValue());
				}
				if (key == 3 || key == 4 || key == 5) {
					middle.append(slot.getValue());
				}
				if (key == 6 || key == 7 || key == 8) {
					bottom.append(slot.getValue());
				}
			}
		}
		Object tes = null;
		Check.forNull(tes);
		ShapedRecipe recipe = new ShapedRecipe(key, Check.forNull(item, "ItemStack not built yet!"));
		recipe.shape(top.toString(), middle.toString(), bottom.toString());
		if (!this.recipeStackMap.isEmpty()) {
			useItemStack(recipe, top.toString(), middle.toString(), bottom.toString());
		} else {
			if (recipeMap.isEmpty()) throw new IllegalStateException("Missing material based recipe map!");
			useMaterial(recipe, top.toString(), middle.toString(), bottom.toString());
		}
		this.recipe = recipe;
		return this;
	}

	public CustomItemRecipe shape(Consumer<Workbench> consumer) {
		LabyrinthWorkbench workbench = new LabyrinthWorkbench();
		consumer.accept(workbench);
		return shape(workbench);
	}

	void useItemStack(ShapedRecipe recipe, String top, String middle, String bottom) {
		for (int i = 0; i <= 2; i++) {
			if (this.recipeStackMap.get(top.charAt(i)) != null) recipe.setIngredient(top.charAt(i), new RecipeChoice.ExactChoice(this.recipeStackMap.get(top.charAt(i))));
			if (this.recipeStackMap.get(middle.charAt(i)) != null) recipe.setIngredient(middle.charAt(i), new RecipeChoice.ExactChoice(this.recipeStackMap.get(middle.charAt(i))));
			if (this.recipeStackMap.get(bottom.charAt(i)) != null) recipe.setIngredient(bottom.charAt(i), new RecipeChoice.ExactChoice(this.recipeStackMap.get(bottom.charAt(i))));
		}
	}

	void useMaterial(ShapedRecipe recipe, String top, String middle, String bottom) {
		for (int i = 0; i <= 2; i++) {
			if (this.recipeMap.get(top.charAt(i)) != null) recipe.setIngredient(top.charAt(i), this.recipeMap.get(top.charAt(i)));
			if (this.recipeMap.get(middle.charAt(i)) != null) recipe.setIngredient(middle.charAt(i), this.recipeMap.get(middle.charAt(i)));
			if (this.recipeMap.get(bottom.charAt(i)) != null) recipe.setIngredient(bottom.charAt(i), this.recipeMap.get(bottom.charAt(i)));
		}
	}

	public void register() {
		Bukkit.addRecipe(recipe);
	}

	public static List<CustomItemRecipe> getRegistered() {
		return Collections.unmodifiableList(cache);
	}

	public static void removeEntry(CustomItemRecipe customItemRecipe) {

		Iterator<Recipe> it = Bukkit.recipeIterator();
		Recipe recipe;
		while (it.hasNext()) {
			recipe = it.next();
			if (recipe != null && recipe.getResult().equals(customItemRecipe.item)) {
				CustomItemRecipe.cache.remove(customItemRecipe);
				it.remove();
			}
		}
	}

	public static CustomItemRecipe getRegistration(Material type) {
		return CustomItemRecipe.cache.stream().filter(i -> i.item.getType() == type).findFirst().orElse(null);
	}

	public static CustomItemRecipe getRegistration(String key) {
		return CustomItemRecipe.cache.stream().filter(i -> i.key.getKey().equals(key.toLowerCase(Locale.ROOT))).findFirst().orElse(null);
	}

	/**
	 * Used to encapsulate already-made ItemStacks or to make new ones
	 * and edit them.
	 */
	public static class Edit {

		private CustomItemRecipe parent;
		private List<ItemStack> LIST;
		private ItemStack ITEM;
		private boolean LISTED = false;
		private List<String> LORE;
		private List<ItemFlag> FLAGS;
		private Consumer<LegacyContainer> consumer;
		private Map<Enchantment, Integer> ENCHANTS;
		private String TITLE;

		Edit(CustomItemRecipe parent) {
			this(Material.AIR);
			this.parent = parent;
		}

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

		/**
		 * Set the title of this item.
		 *
		 * @param text The text to title with.
		 * @return The same item builder.
		 */
		public Edit setTitle(String text) {
			this.TITLE = StringUtils.use(text).translate();
			return this;
		}

		/**
		 * Set the stack size for this item.
		 *
		 * @param amount The amount for this item.
		 * @return The same item builder.
		 */
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

		/**
		 * Set the item to be used with this edit.
		 *
		 * @param i The item to use with this edit.
		 * @return The same item builder.
		 */
		public Edit setItem(ItemStack i) {
			if (!LISTED) {
				this.ITEM = new ItemStack(i);
			}
			return this;
		}

		/**
		 * Set the items to be used with this edit.
		 *
		 * @param i The items to use with this edit.
		 * @return The same item builder.
		 */
		public Edit setItem(ItemStack... i) {
			if (LISTED) {
				this.LIST = new ArrayList<>(Arrays.asList(i));
			}
			return this;
		}

		/**
		 * Set the items to be used with this edit.
		 *
		 * @param i The items to use with this edit.
		 * @return The same item builder.
		 */
		public Edit setItem(Collection<ItemStack> i) {
			if (LISTED) {
				this.LIST = new ArrayList<>(i);
			}
			return this;
		}

		/**
		 * Change the appearance of this item.
		 *
		 * @param type The type to change it to.
		 * @return The same item builder.
		 */
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

		/**
		 * Change the lore of this item.
		 *
		 * @param collection The lore to change it to.
		 * @return The same item builder.
		 */
		public Edit setLore(Collection<String> collection) {
			List<String> list = new LinkedList<>();
			for (String s : collection) {
				list.add(StringUtils.use(s).translate());
			}
			this.LORE = list;
			return this;
		}

		/**
		 * Change the lore of this item.
		 *
		 * @param text The lore to change it to.
		 * @return The same item builder.
		 */
		public Edit setLore(String... text) {
			List<String> list = new LinkedList<>();
			for (String s : text) {
				list.add(StringUtils.use(s).translate());
			}
			this.LORE = list;
			return this;
		}

		/**
		 * Set item flags.
		 *
		 * @param flag The flags to use.
		 * @return The same item builder.
		 */
		public Edit setFlags(ItemFlag... flag) {
			this.FLAGS = new LinkedList<>(Arrays.asList(flag));
			return this;
		}

		/**
		 * Add an enchantment to this item, accepts unsafe enchanting levels!
		 *
		 * @param enchant The enchantment to use.
		 * @param level   The level to set on the enchantment.
		 * @return The same item builder.
		 */
		public Edit addEnchantment(Enchantment enchant, int level) {
			if (this.ENCHANTS == null) {
				this.ENCHANTS = new HashMap<>();
			}
			this.ENCHANTS.put(enchant, level);
			return this;
		}

		/**
		 * Configure the item's persistent data container.
		 * <p>
		 * IF legacy no container will be present.
		 *
		 * @param container The persistent data container for the item.
		 * @return The same item builder.
		 * @deprecated Use {@link Edit#editContainer(Consumer)} instead!!
		 */
		@Deprecated
		public Edit getContainer(Consumer<LegacyContainer> container) {
			return editContainer(container);
		}

		/**
		 * Configure the item's persistent data container.
		 * <p>
		 * IF legacy no container will be present.
		 *
		 * @param container The persistent data container for the item.
		 * @return The same item builder.
		 */
		public Edit editContainer(Consumer<LegacyContainer> container) {
			this.consumer = container;
			return this;
		}

		/**
		 * Finish the item modification by editing the item saturation.
		 *
		 * @param damage The damageable inheritance to modify before retrieving
		 * @return The list of fully customized item's
		 */
		public List<ItemStack> finish(Consumer<Damageable> damage) {
			if (!LISTED) {
				return Collections.emptyList();
			}
			for (ItemStack it : this.LIST) {
				ItemMeta meta = it.getItemMeta();
				damage.accept((Damageable) meta);

				if (this.consumer != null) {
					if (!LabyrinthProvider.getInstance().isLegacy()) {
						this.consumer.accept(new LegacyContainer.Impl(meta.getPersistentDataContainer()));
					}
				}

				if (this.TITLE != null) {
					meta.setDisplayName(this.TITLE);
				}

				if (this.LORE != null) {
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

		/**
		 * Finish the item modification by editing the item saturation & meta.
		 *
		 * @param damage  The damageable inheritance to modify before retrieving
		 * @param options The item meta to configure.
		 * @return The list of fully customized item's
		 */
		public List<ItemStack> finish(Consumer<Damageable> damage, Consumer<ItemMeta> options) {
			if (!LISTED) {
				return Collections.emptyList();
			}
			for (ItemStack it : this.LIST) {
				ItemMeta meta = it.getItemMeta();
				damage.accept((Damageable) meta);
				options.accept(meta);

				if (this.consumer != null) {
					if (!LabyrinthProvider.getInstance().isLegacy()) {
						this.consumer.accept(new LegacyContainer.Impl(meta.getPersistentDataContainer()));
					}
				}

				if (this.TITLE != null) {
					meta.setDisplayName(this.TITLE);
				}

				if (this.LORE != null) {
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

		/**
		 * Finish the item modification.
		 *
		 * @return The list of fully customized item's
		 */
		public List<ItemStack> finish() {
			if (!LISTED) {
				return Collections.emptyList();
			}
			for (ItemStack it : this.LIST) {
				ItemMeta meta = it.getItemMeta();

				if (this.consumer != null) {
					if (!LabyrinthProvider.getInstance().isLegacy()) {
						this.consumer.accept(new LegacyContainer.Impl(meta.getPersistentDataContainer()));
					}
				}

				if (this.TITLE != null) {
					meta.setDisplayName(this.TITLE);
				}

				if (this.LORE != null) {
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

		/**
		 * Finish the item modification by editing the item saturation.
		 *
		 * @param damage The inheritance of damageable
		 * @return The fully built item
		 */
		public ItemStack build(Consumer<Damageable> damage) {
			if (LISTED) {
				return this.LIST.get(0);
			}
			ItemMeta meta = this.ITEM.getItemMeta();
			damage.accept((Damageable) meta);

			if (this.consumer != null) {
				if (!LabyrinthProvider.getInstance().isLegacy()) {
					this.consumer.accept(new LegacyContainer.Impl(meta.getPersistentDataContainer()));
				}
			}

			if (this.TITLE != null) {
				meta.setDisplayName(this.TITLE);
			}

			if (this.LORE != null) {
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

		/**
		 * Finish the item modification by editing the item saturation & meta.
		 *
		 * @param damage  The inheritance of damageable
		 * @param options The item meta.
		 * @return The fully built item
		 */
		public ItemStack build(Consumer<Damageable> damage, Consumer<ItemMeta> options) {
			if (LISTED) {
				return this.LIST.get(0);
			}
			ItemMeta meta = this.ITEM.getItemMeta();
			damage.accept((Damageable) meta);
			options.accept(meta);

			if (this.consumer != null) {
				if (!LabyrinthProvider.getInstance().isLegacy()) {
					this.consumer.accept(new LegacyContainer.Impl(meta.getPersistentDataContainer()));
				}
			}

			if (this.TITLE != null) {
				meta.setDisplayName(this.TITLE);
			}

			if (this.LORE != null) {
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

		/**
		 * Finish the item modification.
		 *
		 * @return The fully built item
		 */
		public ItemStack build() {
			if (LISTED) {
				return this.LIST.get(0);
			}
			ItemMeta meta = this.ITEM.getItemMeta();


			if (this.consumer != null) {
				if (!LabyrinthProvider.getInstance().isLegacy()) {
					this.consumer.accept(new LegacyContainer.Impl(meta.getPersistentDataContainer()));
				}
			}

			if (this.TITLE != null) {
				meta.setDisplayName(this.TITLE);
			}

			if (this.LORE != null) {
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

		public @Note("If this is a normal item edit this will return null")
		CustomItemRecipe complete() {
			if (this.parent != null) {
				this.parent.item = build();
				return this.parent;
			} else return null;
		}

		public @Note("If this is a normal item edit this will return null")
		CustomItemRecipe complete(Consumer<Damageable> consumer) {
			if (this.parent != null) {
				this.parent.item = build(consumer);
				return this.parent;
			} else return null;
		}

		public @Note("If this is a normal item edit this will return null")
		CustomItemRecipe complete(Consumer<Damageable> consumer, Consumer<ItemMeta> consumer2) {
			if (this.parent != null) {
				this.parent.item = build(consumer, consumer2);
				return this.parent;
			} else return null;
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

		/**
		 * Modify firework effects.
		 *
		 * @param operation The firework effect builder.
		 * @return The same custom firework.
		 */
		public CustomFirework addEffects(Consumer<FireworkEffect.Builder> operation) {
			FireworkEffect.Builder builder = FireworkEffect.builder();
			operation.accept(builder);
			this.EFFECTS = builder;
			return this;
		}

		/**
		 * Build and fire off the firework.
		 *
		 * @return Get the firework.
		 */
		public Firework build() {
			Firework f = (Firework) LOCATION.getWorld().spawnEntity(LOCATION, Entities.getEntity("firework"));
			FireworkMeta meta = f.getFireworkMeta();
			if (this.EFFECTS != null) {
				meta.addEffect(this.EFFECTS.build());
			}
			f.setFireworkMeta(meta);
			return f;
		}

		/**
		 * Build and fire off the firework and edit its meta.
		 *
		 * @param operation The firework meta operation
		 * @return Get the firework.
		 */
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
