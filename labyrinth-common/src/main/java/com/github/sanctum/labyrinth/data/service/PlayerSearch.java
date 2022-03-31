package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.data.JsonAdapter;
import com.github.sanctum.labyrinth.data.LabyrinthUser;
import com.github.sanctum.labyrinth.data.container.CollectionTask;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthEntryMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthList;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;
import com.github.sanctum.labyrinth.formatting.string.BlockChar;
import com.github.sanctum.labyrinth.formatting.string.ImageBreakdown;
import com.github.sanctum.labyrinth.formatting.string.SpecialID;
import com.github.sanctum.labyrinth.library.Deployable;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.google.gson.JsonArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * A replacement to the old provision, provide consistent UUID allocation to target usernames!
 */
public abstract class PlayerSearch implements LabyrinthUser {

	static final LabyrinthMap<String, PlayerSearch> lookups = new LabyrinthEntryMap<>();

	@Override
	public abstract @NotNull String getName();

	public abstract @NotNull OfflinePlayer getPlayer();

	public abstract @NotNull UUID getId();

	public abstract @NotNull SpecialID getSpecialId();

	public final TimeWatch.Recording getPlaytime() {
		return TimeWatch.Recording.subtract(getPlayer().getFirstPlayed());
	}

	public abstract ImageBreakdown getHeadImage();

	public static PlayerSearch of(@NotNull OfflinePlayer player) {
		String name = player.getName();
		if (name == null || name.contains("CMI") || name.contains(".") || name.contains(",") || name.contains("!") || name.contains("?")) {
			LabyrinthProvider.getInstance().getLogger().severe("- Attempted and failed to register corrupt player data (" + player + "), this is not the fault of labyrinth.");
			return null;
		}
		return lookups.computeIfAbsent(name, s -> new PlayerSearch() {

			final OfflinePlayer parent;
			ImageBreakdown image;
			final String name;
			String[] names;
			final UUID reference;
			final boolean online;

			{
				this.parent = player;
				this.online = Bukkit.getOnlineMode();
				this.name = s;
				this.reference = player.getUniqueId();
				TaskScheduler.of(() -> this.image = new ImageBreakdown("https://minotar.net/avatar/" + s + ".png", 8, BlockChar.SOLID) {
				}).scheduleAsync();
				TaskScheduler.of(() -> {
					URL url;
					BufferedReader in = null;
					StringBuilder sb = new StringBuilder();
					try {
						url = new URL("https://api.mojang.com/user/profiles/" + reference.toString() + "/names");
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
					if (sb.length() > 0) {
						JsonArray array = JsonAdapter.getJsonBuilder().create().fromJson(sb.toString(), JsonArray.class);
						LabyrinthCollection<String> names = new LabyrinthList<>();
						array.forEach(element -> {
							if (element.isJsonObject()) {
								names.add(element.getAsJsonObject().get("name").getAsString());
							}
						});
						this.names = names.stream().toArray(String[]::new);
					}
				}).scheduleAsync();
			}

			@Override
			public @NotNull String getName() {
				return name;
			}

			@Override
			public String[] getPreviousNames() {
				return names;
			}

			@Override
			public @NotNull OfflinePlayer getPlayer() {
				return online ? parent : Bukkit.getOfflinePlayer(getName());
			}

			@Override
			public @NotNull UUID getId() {
				return online ? reference : getPlayer().getUniqueId();
			}

			@Override
			@Note("Length of 12 (HUID)")
			public @NotNull SpecialID getSpecialId() {
				return SpecialID.builder().setLength(12).build(name);
			}

			@Override
			public ImageBreakdown getHeadImage() {
				if (image == null) return null;
				return new ImageBreakdown(image) {
				};
			}
		});
	}

	public static PlayerSearch of(String name) {
		return lookups.get(name);
	}

	public static void register(@NotNull PlayerSearch search) {
		lookups.put(search.getName(), search);
	}

	public static LabyrinthCollection<PlayerSearch> values() {
		return lookups.values();
	}

	public static Deployable<Void> reload() {
		return Deployable.of(null, unused -> {
			PlayerSearch.lookups.clear();
			final OfflinePlayer[] players = Bukkit.getOfflinePlayers();
			final LabyrinthAPI api = LabyrinthProvider.getInstance();
			if (players.length >= 500) {
				CollectionTask<OfflinePlayer> cache = CollectionTask.process(players, "USER-CACHE", 20, PlayerSearch::of);
				api.getLogger().warning("- Whoa large amounts of people, splitting the workload...");
				api.getScheduler(TaskService.ASYNCHRONOUS).repeat(cache, 0, 50); // repeat the task every 1 tick therefore loading only 20 users every tick instead of all at once.
				while (cache.getCompletion() < 100) {
					try {
						Thread.sleep(1L); // make main thread wait to continue after all users are loaded.
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				for (OfflinePlayer op : players) PlayerSearch.of(op);
			}
		});
	}

}
