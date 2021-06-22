package com.github.sanctum.skulls;

import com.github.sanctum.labyrinth.Labyrinth;
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

public abstract class CustomHead {

	protected static boolean LOADED;
	protected UUID owner;
	protected static final List<CustomHead> HEADS;

	protected CustomHead() {
	}

	protected CustomHead(UUID owner) {
		this.owner = owner;
	}

	abstract @NotNull ItemStack get();

	abstract @NotNull String name();

	abstract @NotNull String category();

	public Optional<UUID> id() {
		return Optional.ofNullable(this.owner);
	}

	public static class Manager {

		public static void load(CustomHead object) {
			HEADS.add(object);
		}

		protected static List<CustomHead> loadOffline() {
			List<CustomHead> list = new LinkedList<>();
			if (!LOADED) {
				for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
					OnlineHeadSearch search = new OnlineHeadSearch(player.getUniqueId());
					if (search.getResult() != null) {
						list.add(new LabyrinthHeadImpl(player.getName(), "Human", search.getResult(), player.getUniqueId()));
					} else {
						Labyrinth.getInstance().getLogger().severe("- " + player.getName() + " has no information provided by mojang. Cracked accounts are not supported for custom heads.");
					}
				}
			}
			return list;
		}

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

		public static @Nullable ItemStack get(OfflinePlayer player) {

			CustomHead ch = HEADS.stream().filter(h -> h.id().isPresent() && h.id().get().equals(player.getUniqueId())).findFirst().orElseGet(() -> {

				OnlineHeadSearch search = new OnlineHeadSearch(player.getUniqueId());
				if (search.getResult() != null) {
					CustomHead head = new LabyrinthHeadImpl(player.getName(), "Human", search.getResult(), player.getUniqueId());
					HEADS.add(head);
					return head;
				}
				return null;
			});

			return ch != null ? ch.get() : null;
		}

		public static @Nullable ItemStack get(UUID id) {

			CustomHead ch = HEADS.stream().filter(h -> h.id().isPresent() && h.id().get().equals(id)).findFirst().orElseGet(() -> {

				OnlineHeadSearch search = new OnlineHeadSearch(id);
				if (search.getResult() != null) {
					CustomHead head = new LabyrinthHeadImpl(Bukkit.getOfflinePlayer(id).getName(), "Human", search.getResult(), id);
					HEADS.add(head);
					return head;
				}
				return null;
			});

			return ch != null ? ch.get() : null;
		}

		public static @Nullable ItemStack get(String name) {

			CustomHead ch = HEADS.stream().filter(h -> h.name().equals(name)).findFirst().orElseGet(() -> {

				OnlineHeadSearch search = new OnlineHeadSearch(name);
				if (search.getResult() != null) {
					CustomHead head = new LabyrinthHeadImpl(name, "Human", search.getResult());
					HEADS.add(head);
					return head;
				}
				return null;
			});

			return ch != null ? ch.get() : null;
		}

		public static @Nullable CustomHead find(String name) {
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

		public static List<String> getCategories() {
			return HEADS.stream().map(CustomHead::category).collect(Collectors.toList());
		}

		public static List<ItemStack> getCategory(String category) {
			if (!getCategories().contains(category)) {
				return new ArrayList<>();
			}
			return HEADS.stream().filter(h -> h.category().equals(category)).map(CustomHead::get).collect(Collectors.toList());
		}

		public static List<ItemStack> getGallery() {
			return Collections.unmodifiableList(HEADS.stream().map(CustomHead::get).collect(Collectors.toList()));
		}

		public static CustomHeadLoader newLoader(Plugin plugin, String fileName, String directory) {
			return new CustomHeadLoader(plugin, fileName, directory);
		}

		public static CustomHeadLoader newLoader(FileConfiguration configuration) {
			return new CustomHeadLoader(configuration);
		}

		/**
		 * For internal use only!!
		 *
		 * @param name The name of the custom head.
		 * @param item The skull item.
		 * @deprecated Use {@link CustomHead.Manager#load(CustomHead)} to load already verified items.
		 */
		@Deprecated
		protected static void load(String name, String category, ItemStack item) {
			boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
			Material type = Items.getMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");

			if (item.getType() != type)
				throw new IllegalStateException(item.getType().name() + " is not a direct representation of " + type.name());

			load(new LabyrinthHeadImpl(name, category, item));
		}

	}

	static {

		HEADS = new LinkedList<>(CustomHead.Manager.loadOffline());

		LOADED = true;

	}

}
