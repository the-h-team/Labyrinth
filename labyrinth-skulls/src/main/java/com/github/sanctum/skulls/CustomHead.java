package com.github.sanctum.skulls;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.service.LabyrinthOptions;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Items;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom skinned player skull with an attached reference to its category and possible owner. If no owner is present then this head is considered {@link SkullType#CUSTOM}
 *
 * All base64 head values found from "https://minecraft-heads.com/custom-heads" are supported.
 */
public abstract class CustomHead implements SkullObject {

	private static final boolean LOADED;
	private static final List<CustomHead> HEADS;

	protected UUID owner;

	protected CustomHead() {
	}

	protected CustomHead(UUID owner) {
		this.owner = owner;
	}

	@Override
	public abstract @NotNull ItemStack get();

	@Override
	public abstract @NotNull String name();

	@Override
	public abstract @NotNull String category();

	@Override
	public Optional<UUID> id() {
		return Optional.ofNullable(this.owner);
	}

	@Override
	public SkullType getType() {
		return this.id().isPresent() ? SkullType.PLAYER : SkullType.CUSTOM;
	}

	public static class Manager {

		/**
		 * Load a custom head object into cache.
		 *
		 * @param object the custom head object to cache
		 */
		public static void load(CustomHead object) {
			boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
			Material type = Items.getMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");
			if (object.get().getType() != type)
				throw new IllegalStateException(object.get().getType().name() + " is not a direct representation of " + type.name());
			HEADS.add(object);
		}

		protected static List<CustomHead> loadOffline() {
			List<CustomHead> list = new LinkedList<>();
			if (!LOADED) {
				for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
					OnlineHeadSearch search = new OnlineHeadSearch(player.getUniqueId());
					if (search.getResult() != null) {
						if (player.getName() != null) {
							list.add(new LabyrinthHeadImpl(player.getName(), "Human", search.getResult(), player.getUniqueId()));
						}
					} else {
						LabyrinthProvider.getInstance().getLogger().severe("- " + player.getName() + " has no information provided by mojang. Cracked accounts are not supported for custom heads.");

						OnlineHeadSearch search2 = new OnlineHeadSearch(player.getName());
						if (search2.getResult() != null) {
							if (player.getName() != null) {
								list.add(new LabyrinthHeadImpl(player.getName(), "Deceased", search2.getResult()));
							}
						}
					}
				}
			}
			return list;
		}

		/**
		 * Using {@link CustomHead.Manager#newLoader(FileConfiguration)} or {@link CustomHead.Manager#newLoader(Plugin, String, String)} load configured head values
		 * from your specified file location.
		 *
		 * @param loader the head loader instance
		 */
		public static void load(CustomHeadLoader loader) {
			if (loader.isLoaded()) {
				for (Map.Entry<HeadText, ItemStack> entry : loader.getHeads().entrySet()) {
					load(entry.getKey().getName(), entry.getKey().getCategory(), entry.getValue());
				}
			} else {
				for (Map.Entry<HeadText, ItemStack> entry : loader.load().getHeads().entrySet()) {
					load(entry.getKey().getName(), entry.getKey().getCategory(), entry.getValue());
				}
			}
		}

		/**
		 * Get a local player's head.
		 * <p>
		 * Results are cached. If a result is already found within the query it is instead returned.
		 *
		 * @param player the player to fetch
		 * @return the head of the specified player or null if not found
		 */
		public static @Nullable ItemStack get(OfflinePlayer player) {
			return HEADS.stream().filter(h -> h.id().isPresent() && h.id().get().equals(player.getUniqueId())).map(CustomHead::get).findFirst().orElseGet(() -> {

				OnlineHeadSearch search = new OnlineHeadSearch(player.getUniqueId());
				if (search.getResult() != null) {
					CustomHead head = new LabyrinthHeadImpl(player.getName(), "Human", search.getResult(), player.getUniqueId());
					HEADS.add(head);
					return head.get();
				}
				return null;
			});
		}

		/**
		 * Get the head of either a local player or one that has <strong>never</strong> logged onto the server before.
		 * <p>
		 * As long as the id has a valid link to a player from the mojang api results are cached.
		 *
		 * @param id a valid player id
		 * @return the head of the specified user or null if not found
		 */
		public static @Nullable ItemStack get(UUID id) {
			return HEADS.stream().filter(h -> h.id().isPresent() && h.id().get().equals(id)).map(CustomHead::get).findFirst().orElseGet(() -> {

				OnlineHeadSearch search = new OnlineHeadSearch(id);
				if (search.getResult() != null) {
					CustomHead head = new LabyrinthHeadImpl(Bukkit.getOfflinePlayer(id).getName(), "Human", search.getResult(), id);
					HEADS.add(head);
					return head.get();
				}
				return null;
			});
		}

		/**
		 * Get the head of either a local player or one that has <strong>never</strong> logged onto the server before.
		 * <p>
		 * As long as the id has a valid link to a player from the mojang api results are cached.
		 *
		 * @param name the name of the player
		 * @return the head of the specified user or null if not found
		 */
		public static @Nullable ItemStack get(String name) {

			return HEADS.stream().filter(h -> h.name().equals(name)).map(CustomHead::get).findFirst().orElseGet(() -> {

				OnlineHeadSearch search = new OnlineHeadSearch(name);
				if (search.getResult() != null) {
					CustomHead head = new LabyrinthHeadImpl(name, "Human", search.getResult());
					HEADS.add(head);
					return head.get();
				}
				return null;
			});
		}

		/**
		 * Get the head of either a local player or one that has <strong>never</strong> logged onto the server before.
		 * <p>
		 * As long as the id has a valid link to a player from the mojang api results are cached.
		 *
		 * @param name the name of the player
		 * @return the head of the specified user or null if not found
		 */
		public static @Nullable CustomHead pick(String name) {
			return HEADS.stream().filter(h -> h.name().equals(name)).findFirst().orElseGet(() -> {

				OnlineHeadSearch search = new OnlineHeadSearch(name);
				if (search.getResult() != null) {
					CustomHead head = new LabyrinthHeadImpl(name, "Human", search.getResult());
					HEADS.add(head);
					return head;
				}
				return null;
			});
		}

		/**
		 * Get the head of either a local player or one that has <strong>never</strong> logged onto the server before.
		 * <p>
		 * As long as the id has a valid link to a player from the mojang api results are cached.
		 *
		 * @param id a valid player id
		 * @return the head of the specified user or null if not found
		 */
		public static @Nullable CustomHead pick(UUID id) {
			return HEADS.stream().filter(h -> h.id().isPresent() && h.id().get().equals(id)).findFirst().orElseGet(() -> {

				OnlineHeadSearch search = new OnlineHeadSearch(id);
				if (search.getResult() != null) {
					CustomHead head = new LabyrinthHeadImpl(Bukkit.getOfflinePlayer(id).getName(), "Human", search.getResult(), id);
					HEADS.add(head);
					return head;
				}
				return null;
			});
		}

		/**
		 * Get a local player's head.
		 * <p>
		 * Results are cached. If a result is already found within the query it is instead returned.
		 *
		 * @param player the player to fetch
		 * @return the head of the specified player or null if not found
		 */
		public static @Nullable CustomHead pick(OfflinePlayer player) {
			return HEADS.stream().filter(h -> h.id().isPresent() && h.id().get().equals(player.getUniqueId())).findFirst().orElseGet(() -> {

				OnlineHeadSearch search = new OnlineHeadSearch(player.getUniqueId());
				if (search.getResult() != null) {
					CustomHead head = new LabyrinthHeadImpl(player.getName(), "Human", search.getResult(), player.getUniqueId());
					HEADS.add(head);
					return head;
				}
				return null;
			});
		}

		/**
		 * Get all known custom head categories.
		 *
		 * @return a list of every known custom head category
		 */
		public static List<String> getCategories() {
			return HEADS.stream().map(CustomHead::category).collect(Collectors.toList());
		}

		/**
		 * Get all known custom heads as an {@link ItemStack} specified by a category.
		 * <p>
		 * If the specified category isn't found an empty list is returned.
		 *
		 * @param category the valid category to search
		 * @return all categorized heads--or an empty list if you are ill-informed
		 */
		public static List<ItemStack> getCategory(String category) {
			if (!getCategories().contains(category)) {
				return new ArrayList<>();
			}
			return HEADS.stream().filter(h -> h.category().equals(category)).map(CustomHead::get).collect(Collectors.toList());
		}

		/**
		 * Get all currently cached custom heads.
		 *
		 * @return a list of <strong>ALL</strong> known custom heads
		 */
		public static List<CustomHead> getHeads() {
			return Collections.unmodifiableList(HEADS);
		}

		/**
		 * Assign the loading of additional configured head elements.
		 *
		 * @param plugin    the source plugin
		 * @param fileName  the name of the file to use
		 * @param directory the directory of the file
		 * @return a new head loader instance
		 */
		public static CustomHeadLoader newLoader(Plugin plugin, String fileName, String directory) {
			return new CustomHeadLoader(plugin, fileName, directory);
		}

		/**
		 * Assign the loading of additional configured head elements.
		 *
		 * @param configuration the config to source the additions from
		 * @return a new head loader instance
		 */
		public static CustomHeadLoader newLoader(FileConfiguration configuration) {
			return new CustomHeadLoader(configuration);
		}

		/**
		 * For internal use only!!
		 *
		 * @deprecated Use {@link CustomHead.Manager#load(CustomHead)} to load already verified items.
		 * @param name the name of the custom head
		 * @param category the category of the custom head
		 * @param item the skull item
		 */
		@Deprecated
		protected static void load(String name, String category, ItemStack item) {
			boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
			Material type = Items.getMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");

			if (item.getType() != type)
				throw new IllegalStateException(item.getType().name() + " is not a direct representation of " + type.name());

			load(new LabyrinthHeadImpl(name, category, item));
		}

		/**
		 * Copy all lore from a targeted player head and change the owner to one specified.
		 *
		 * @param item the item to modify
		 * @param name the name of the new owner
		 * @return the modified ItemStack
		 */
		public static ItemStack modifyItemStack(ItemStack item, String name) {
			boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
			Material type = Items.getMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");

			if (item.getType() != type)
				throw new IllegalStateException(item.getType().name() + " is not a direct representation of " + type.name());

			CustomHead head = pick(name);
			return head != null ? new Item.Edit(head.get()).setTitle(item.getItemMeta() != null ? item.getItemMeta().getDisplayName() : head.name()).setLore(item.getItemMeta() != null && item.getItemMeta().getLore() != null ? item.getItemMeta().getLore().toArray(new String[0]) : new String[]{""}).build() : item;

		}

	}

	static {

		if (LabyrinthOptions.HEAD_PRE_CACHE.enabled()) {

			HEADS = new LinkedList<>(CustomHead.Manager.loadOffline());

		} else {

			HEADS = new LinkedList<>();

		}

		LOADED = true;

	}

}
