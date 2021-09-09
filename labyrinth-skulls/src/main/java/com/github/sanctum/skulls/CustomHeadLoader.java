package com.github.sanctum.skulls;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.MemorySpace;
import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

public final class CustomHeadLoader {

	private final MemorySpace memory;
	private boolean loaded;

	private final Map<HeadText, OnlineHeadSearch> que;
	private final Map<HeadText, ItemStack> additions;

	protected CustomHeadLoader(MemorySpace memory) {
		this.memory = memory;
		this.que = new HashMap<>();
		this.additions = new HashMap<>();
	}

	protected CustomHeadLoader(Plugin plugin, String fileName, String directory) {
		this.memory = FileList.search(plugin).get(fileName, directory).getRoot();
		this.que = new HashMap<>();
		this.additions = new HashMap<>();
	}

	/**
	 * Search through a specific section in your config for the heads.
	 * <p>
	 * Example:
	 * <pre>ConfigHeader.My_heads</pre>
	 *
	 * @param section the key of a {@link org.bukkit.configuration.ConfigurationSection} from your file to use
	 * @return this head loader instance with attempted values
	 */
	public CustomHeadLoader look(String section) {
		if (memory.isNode(section)) {
			for (String id : memory.getNode(section).getKeys(false)) {
				boolean custom = memory.getNode(section).getNode(id).getNode("custom").toPrimitive().getBoolean();
				String name = memory.getNode(section).getNode(id).getNode("name").toPrimitive().getString();
				if (name == null) continue;
				if (custom) {
					String category = memory.getNode(section).getNode(id).getNode("category").toPrimitive().getString();
					String value = null;

					if (memory.getNode(section).getNode(id).getNode("value").toPrimitive().isString()) {
						value = memory.getNode(section).getNode(id).getNode("value").toPrimitive().getString();
					}

					if (value != null) {
						additions.put(new HeadText(name, category), provide(value));
					} else {
						LabyrinthProvider.getInstance().getLogger().severe("- Custom head #" + id + " has no value to use.");
					}


				} else {
					String category = memory.getNode(section).getNode(id).getNode("category").toPrimitive().getString();
					String user = memory.getNode(section).getNode(id).getNode("user").toPrimitive().getString();

					boolean isID = user != null && user.contains("-");

					if (isID) {
						que.put(new HeadText(name, category), new OnlineHeadSearch(UUID.fromString(user)));
					} else {
						que.put(new HeadText(name, category), new OnlineHeadSearch(user));
					}
				}
			}
		}
		return this;
	}

	/**
	 * @return Optionally pre-load all player related requests. (Handled internally)
	 */
	public CustomHeadLoader load() {
		this.loaded = true;
		for (Map.Entry<HeadText, OnlineHeadSearch> entry : this.que.entrySet()) {
			ItemStack result = entry.getValue().getResult();
			if (result != null) {
				this.additions.put(entry.getKey(), result);
			} else {
				LabyrinthProvider.getInstance().getLogger().severe("- Custom named head '" + entry.getKey().getName() + "' unable to load.");
			}
		}
		return this;
	}

	/**
	 * Complete the requirements to load the desired head database into cache.
	 */
	public void complete() {
		if (!getHeads().isEmpty()) {
			CustomHead.Manager.load(this);
		} else {
			LabyrinthProvider.getInstance().getLogger().warning("- No heads were loaded from memory space " + memory.getPath());
		}
	}

	protected Map<HeadText, ItemStack> getHeads() {
		return this.additions;
	}

	protected boolean isLoaded() {
		return this.loaded;
	}


	// TODO: decide nullity (does not seem to be Nullable); maybe add throws
	/**
	 * Apply Base64 data for a custom skin value.
	 *
	 * *NOTE: Not cached.
	 *
	 * @param headValue the target head value to apply to a skull item
	 * @return the specified custom head
	 */
	public static ItemStack provide(String headValue) {
		boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
		Material type = Material.matchMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");
		Preconditions.checkNotNull(type);
		ItemStack skull;
		if (isNew) {
			skull = new ItemStack(type);
		} else {
			//noinspection deprecation
			skull = new ItemStack(type, 1, (short) 3);
		}
		if (headValue != null) {

			SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
			GameProfile profile = new GameProfile(UUID.randomUUID(), null);

			profile.getProperties().put("textures", new Property("textures", headValue));

			if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12") || Bukkit.getVersion().contains("1.13")) {

				try {
					//noinspection ConstantConditions
					Field profileField = skullMeta.getClass().getDeclaredField("profile");
					profileField.setAccessible(true);
					profileField.set(skullMeta, profile);
				} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
					e1.printStackTrace();
				}

			} else {
				try {
					//noinspection ConstantConditions
					Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
					mtd.setAccessible(true);
					mtd.invoke(skullMeta, profile);
				} catch (IllegalAccessException | java.lang.reflect.InvocationTargetException | NoSuchMethodException ex) {
					ex.printStackTrace();
				}
			}

			skull.setItemMeta(skullMeta);
			return skull;
		}
		return skull;
	}

}
