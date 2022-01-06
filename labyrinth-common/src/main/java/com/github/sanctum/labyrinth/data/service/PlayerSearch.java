package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.FileType;
import com.github.sanctum.labyrinth.data.container.ImmutableLabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthEntryMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;
import com.github.sanctum.labyrinth.formatting.string.SpecialID;
import com.github.sanctum.labyrinth.interfacing.Nameable;
import com.github.sanctum.labyrinth.library.Deployable;
import com.github.sanctum.labyrinth.library.TimeWatch;
import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
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

	public static PlayerSearch of(@NotNull OfflinePlayer player) {
		String name = player.getName();
		if (name == null) {
			LabyrinthProvider.getInstance().getLogger().severe("- Attempted and failed to register corrupt player data (" + player + "), this is not the fault of labyrinth.");
			return null;
		}
		return lookups.computeIfAbsent(name, s -> new PlayerSearch() {

			final OfflinePlayer parent;
			final String name;
			final UUID reference;
			final boolean online;
			{
				this.parent = validate(player);
				this.online = Bukkit.getOnlineMode();
				this.name = s;
				reference = player.getUniqueId();
			}

			<T> T validate(T t) {
				if (t != null) return t;
				throw new IllegalArgumentException("User cannot be null, this is not a fault of labyrinth!");
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
		});
	}

	public static PlayerSearch of(String name) {
		return lookups.get(name);
	}

	public static LabyrinthCollection<PlayerSearch> values() {
		return ImmutableLabyrinthCollection.of(lookups.values());
	}

	public static Deployable<Void> reload() {

		return Deployable.of(null, unused -> {
			PlayerSearch.lookups.clear();
			Arrays.stream(Bukkit.getOfflinePlayers()).forEach(PlayerSearch::of);
		});
	}

}
