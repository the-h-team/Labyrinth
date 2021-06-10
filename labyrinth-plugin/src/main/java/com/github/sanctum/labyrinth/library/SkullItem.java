package com.github.sanctum.labyrinth.library;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * @author Hempfest
 */
public class SkullItem {

	public static String COMMAND_BLOCK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWY0YzIxZDE3YWQ2MzYzODdlYTNjNzM2YmZmNmFkZTg5NzMxN2UxMzc0Y2Q1ZDliMWMxNWU2ZTg5NTM0MzIifX19";
	public static String ARROW_CYAN_LEFT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjc2OGVkYzI4ODUzYzQyNDRkYmM2ZWViNjNiZDQ5ZWQ1NjhjYTIyYTg1MmEwYTU3OGIyZjJmOWZhYmU3MCJ9fX0=";
	public static String ARROW_CYAN_RIGHT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZmNTVmMWIzMmMzNDM1YWMxYWIzZTVlNTM1YzUwYjUyNzI4NWRhNzE2ZTU0ZmU3MDFjOWI1OTM1MmFmYzFjIn19fQ==";
	public static String ARROW_CYAN_UP = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGIyMjFjYjk2MDdjOGE5YmYwMmZlZjVkNzYxNGUzZWIxNjljYzIxOWJmNDI1MGZkNTcxNWQ1ZDJkNjA0NWY3In19fQ==";
	public static String ARROW_CYAN_DOWN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDhhYWI2ZDlhMGJkYjA3YzEzNWM5Nzg2MmU0ZWRmMzYzMTk0Mzg1MWVmYzU0NTQ2M2Q2OGU3OTNhYjQ1YTNkMyJ9fX0=";
	public static String ARROW_BLACK_LEFT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=";
	public static String ARROW_BLACK_RIGHT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19";
	public static String ARROW_BLACK_UP = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNjYmY5ODgzZGQzNTlmZGYyMzg1YzkwYTQ1OWQ3Mzc3NjUzODJlYzQxMTdiMDQ4OTVhYzRkYzRiNjBmYyJ9fX0=";
	public static String ARROW_BLACK_DOWN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzI0MzE5MTFmNDE3OGI0ZDJiNDEzYWE3ZjVjNzhhZTQ0NDdmZTkyNDY5NDNjMzFkZjMxMTYzYzBlMDQzZTBkNiJ9fX0=";

	private static final LinkedList<SkullItem> log = new LinkedList<>();

	private final String holder;

	private ItemStack head;

	public SkullItem(String holder, ItemStack head) {
		this.holder = holder;
		this.head = head;
		if (Head.find(UUID.fromString(holder)) == null) {
			SkullItem.log.add(this);
		}
	}

	/**
	 * Get's the player's UUID as a string.
	 *
	 * @return The player's UUID string
	 */
	public String getHolder() {
		return holder;
	}

	/**
	 * Get's the player head object.
	 *
	 * @return The player's head skinned.
	 */
	public ItemStack getItem() {
		return head;
	}

	/**
	 * Get the entire cached list of player head objects.
	 *
	 * @return Get's the full log of cached player heads.
	 */
	public static LinkedList<SkullItem> getLog() {
		return log;
	}

	/**
	 * Use this if the player's head is not automatically cached (May cause slight lag temporarily in loading)
	 * You may also use it to update the players head in the instance of a skin change.
	 */
	public void updateHead() {
		boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			Material type = Material.matchMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");
			ItemStack item = new ItemStack(type, 1);

			if (!isNew) {
				item.setDurability((short) 3);
			}

			SkullMeta meta = (SkullMeta) item.getItemMeta();
			if (!meta.hasOwner()) {
				meta.setOwningPlayer(p);
			}
			item.setItemMeta(meta);
			this.head = item;
		}
	}

	/**
	 * Encapsulate data and search online for results.
	 */
	public static class Search {

		private static final LinkedList<Search> log = new LinkedList<>();

		private String name;
		private String id = null;
		private String value = null;

		public Search(String name) {
			this.name = name;
			try {
				Gson g = new Gson();
				String signature = getNonSessionContent(name);
				JsonObject obj = g.fromJson(signature, JsonObject.class);
				this.id = obj.get("id").toString().replace("\"", "");
				String sessionContent = getSessionContent(this.id);
				obj = g.fromJson(sessionContent, JsonObject.class);
				String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
				String decoded = new String(Base64.getDecoder().decode(value));
				obj = g.fromJson(decoded, JsonObject.class);
				String skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
				byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();
				this.value = new String(Base64.getEncoder().encode(skinByte));
			} catch (Exception ignored) {
			}
		}

		public Search(UUID id) {
			try {
				Gson g = new Gson();
				String signature = getSessionContent(id.toString());
				JsonObject obj = g.fromJson(signature, JsonObject.class);

				this.id = obj.get("id").toString().replace("\"", "");
				this.name = obj.get("name").toString().replace("\"", "");
				String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
				String decoded = new String(Base64.getDecoder().decode(value));
				obj = g.fromJson(decoded, JsonObject.class);
				String skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
				byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();
				this.value = new String(Base64.getEncoder().encode(skinByte));
			} catch (Exception ignored) {
			}
		}

		/**
		 * @return The name belonging to the search.
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return The user id for this search.
		 */
		public UUID getUUID() {
			return id != null ? UUID.fromString(id) : null;
		}

		private String getSessionContent(String string) {
			URL url;
			BufferedReader in = null;
			StringBuilder sb = new StringBuilder();
			try {
				url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + string);
				in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
				String str;
				while ((str = in.readLine()) != null) {
					sb.append(str);
				}
			} catch (Exception ignored) {
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ignored) {
				}
			}
			return sb.toString();
		}

		private String getNonSessionContent(String string) {
			URL url;
			BufferedReader in = null;
			StringBuilder sb = new StringBuilder();
			try {
				url = new URL("https://api.mojang.com/users/profiles/minecraft/" + string);
				in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
				String str;
				while ((str = in.readLine()) != null) {
					sb.append(str);
				}
			} catch (Exception ignored) {
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ignored) {
				}
			}
			return sb.toString();
		}

		/**
		 * @return The head value for this skin.
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @return The desired skin applied to a player skull or null.
		 */
		public ItemStack getHead() {
			boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
			Material type = Material.matchMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");
			assert type != null;
			ItemStack skull = new ItemStack(type);
			if (value != null) {
				UUID hashAsId = new UUID(value.hashCode(), value.hashCode());
				ItemStack result = Bukkit.getUnsafe().modifyItemStack(skull,
						"{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}"
				);
				if (log.stream().noneMatch(s -> s.name.equals(name))) {
					log.add(this);
				}
				return result;
			}
			return null;
		}


	}

	/**
	 * Used for all inquiries about custom/player head data.
	 */
	public static class Head {

		/**
		 * Apply Base64 data for a custom skin value.
		 * <p>
		 * *NOTE: Not cached.
		 *
		 * @param headValue The target head value to apply to a skull item.
		 * @return The specified custom head if not null
		 */
		public static ItemStack provide(String headValue) {
			boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
			Material type = Material.matchMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");
			assert type != null;
			ItemStack skull = new ItemStack(type);
			UUID hashAsId = new UUID(headValue.hashCode(), headValue.hashCode());
			return Bukkit.getUnsafe().modifyItemStack(skull,
					"{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + headValue + "\"}]}}}"
			);
		}

		/**
		 * Query for a skin result online and apply it to a skull item for picking.
		 * <p>
		 * Results cached. (Fastest)
		 *
		 * @param playerName The player name to look up online.
		 * @return The specified player's head if not null
		 */
		public static ItemStack search(String playerName) {
			return Search.log.stream().filter(s -> s.name.equalsIgnoreCase(playerName)).map(Search::getHead).findFirst().orElseGet(() -> {
				Search search = new Search(playerName);
				if (search.getValue() != null) {
					return search.getHead();
				}
				return null;
			});
		}

		/**
		 * Query for a skin result online and apply it to a skull item for picking.
		 * <p>
		 * Results cached. (Fastest)
		 *
		 * @param player The player to look up online.
		 * @return The specified player's head if not null
		 */
		public static ItemStack search(OfflinePlayer player) {
			return Search.log.stream().filter(s -> s.name.equalsIgnoreCase(player.getName())).map(Search::getHead).findFirst().orElseGet(() -> {
				Search search = new Search(player.getName());
				if (search.getValue() != null) {
					return search.getHead();
				}
				return null;
			});
		}

		/**
		 * Query for a skin result online and apply it to a skull item for picking.
		 * <p>
		 * Results cached. (Fastest)
		 *
		 * @param id The player id to look up online.
		 * @return The specified player's head if not null
		 */
		public static ItemStack search(UUID id) {
			return Search.log.stream().filter(s -> s.id.equals(id.toString())).map(Search::getHead).findFirst().orElseGet(() -> {
				Search search = new Search(id);
				if (search.getValue() != null) {
					return search.getHead();
				}
				return null;
			});
		}

		/**
		 * Query for a specified player head by username.
		 * <p>
		 * Results cached (Slower than {@link Head#search(String)}, auto-updating)
		 *
		 * @param name The player name to query for.
		 * @return The specified player's head if not null
		 */
		public static ItemStack find(String name) {
			return SkullItem.getLog().stream().filter(s -> Bukkit.getOfflinePlayer(UUID.fromString(s.getHolder())).getName().equals(name)).map(SkullItem::getItem).findFirst().orElse(null);
		}

		/**
		 * Query for a specified player head by username.
		 * <p>
		 * Results cached (Slower than {@link Head#search(String)}, auto-updating)
		 *
		 * @param player The player to query for.
		 * @return The specified player's head if not null
		 */
		public static ItemStack find(OfflinePlayer player) {
			return SkullItem.getLog().stream().filter(s -> Bukkit.getOfflinePlayer(UUID.fromString(s.getHolder())).getName().equals(player.getName())).map(SkullItem::getItem).findFirst().orElse(null);
		}

		/**
		 * Query for a specified player UUID head.
		 * <p>
		 * Results cached (Slower than {@link Head#search(String)}, auto-updating)
		 *
		 * @param id The player UUID to query for.
		 * @return The specified player's head if not null.
		 */
		public static ItemStack find(UUID id) {
			return SkullItem.getLog().stream().filter(s -> UUID.fromString(s.getHolder()).equals(id)).map(SkullItem::getItem).findFirst().orElse(null);
		}
	}

}
