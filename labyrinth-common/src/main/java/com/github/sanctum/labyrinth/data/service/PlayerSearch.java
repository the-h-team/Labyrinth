package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.data.container.CollectionTask;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthEntryMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;
import com.github.sanctum.labyrinth.formatting.string.ImageBreakdown;
import com.github.sanctum.labyrinth.formatting.string.BlockChar;
import com.github.sanctum.labyrinth.formatting.string.SpecialID;
import com.github.sanctum.labyrinth.interfacing.Nameable;
import com.github.sanctum.labyrinth.library.Deployable;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * A replacement to the old provision, provide consistent UUID allocation to target usernames!
 */
public abstract class PlayerSearch implements Nameable {

	static final LabyrinthMap<String, PlayerSearch> lookups = new LabyrinthEntryMap<>();

	@Override
	public abstract @NotNull String getName();

	public abstract @NotNull OfflinePlayer getPlayer();

	@Deprecated
	public abstract @NotNull UUID getRecordedId();

	public final UUID getId() {
		return getRecordedId();
	}

	public abstract @NotNull SpecialID getSpecialId();

	public final TimeWatch.Recording getPlaytime() {
		return TimeWatch.Recording.subtract(getPlayer().getFirstPlayed());
	}

	public abstract ImageBreakdown getHeadImage();

	public static PlayerSearch of(@NotNull OfflinePlayer player) {
		String name = player.getName();
		if (name == null) {
			LabyrinthProvider.getInstance().getLogger().severe("- Attempted and failed to register corrupt player data (" + player + "), this is not the fault of labyrinth.");
			return null;
		}
		return lookups.computeIfAbsent(name, s -> new PlayerSearch() {

			final OfflinePlayer parent;
			ImageBreakdown image;
			final String name;
			final UUID reference;
			final boolean online;
			{
				this.parent = player;
				this.online = Bukkit.getOnlineMode();
				this.name = s;
				this.reference = player.getUniqueId();
				TaskScheduler.of(() -> this.image = new ImageBreakdown("https://minotar.net/avatar/" + s + ".png", 8, BlockChar.SOLID){}).scheduleLaterAsync(1L);
			}

			@Override
			public @NotNull String getName() {
				return name;
			}

			@Override
			public @NotNull OfflinePlayer getPlayer() {
				return online ? parent : Bukkit.getOfflinePlayer(getName());
			}

			@Override
			public @NotNull UUID getRecordedId() {
				return online ? reference : getPlayer().getUniqueId();
			}

			@Override
			@Note("Length of 12 (HUID)")
			public @NotNull SpecialID getSpecialId() {
				return SpecialID.builder().setLength(12).build(name);
			}

			@Override
			public ImageBreakdown getHeadImage() {
				return new ImageBreakdown(image){};
			}
		});
	}

	public static PlayerSearch of(String name) {
		return lookups.get(name);
	}

	public static LabyrinthCollection<PlayerSearch> values() {
		return lookups.values();
	}

	public static Deployable<Void> reload() {
		return Deployable.of(null, unused -> {
			PlayerSearch.lookups.clear();
			OfflinePlayer[] players = Bukkit.getOfflinePlayers();
			if (players.length >= 500) {
				CollectionTask<OfflinePlayer> cacher = CollectionTask.process(players, "USER-CACHE", 20, PlayerSearch::of);
				LabyrinthAPI api = LabyrinthProvider.getInstance();
				api.getLogger().warning("- Whoa large amounts of people, splitting the workload...");
				api.getScheduler(TaskService.ASYNCHRONOUS).repeat(cacher, 0, 50); // repeat the task every 1 tick therefore loading only 20 users every tick instead of all at once.
				while (cacher.getCompletion() < 100) {
					try {
						Thread.sleep(1L);
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
