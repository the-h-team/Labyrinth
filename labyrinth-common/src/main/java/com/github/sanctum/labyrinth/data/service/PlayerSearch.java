package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
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

	static final Plugin access = LabyrinthProvider.getInstance().getPluginInstance();
	static final FileManager users = FileList.search(access).get("users", "Persistent", FileType.JSON);
	static final LabyrinthMap<String, PlayerSearch> lookups = new LabyrinthEntryMap<>();

	@Override
	public abstract @NotNull String getName();

	public abstract @NotNull OfflinePlayer getPlayer();

	public abstract @NotNull UUID getRecordedId();

	public abstract @NotNull SpecialID getSpecialId();

	public static PlayerSearch of(@NotNull OfflinePlayer player) {
		return lookups.computeIfAbsent(player.getName(), s -> new PlayerSearch() {

			final OfflinePlayer parent;
			final String name;
			final UUID reference;
			{
				this.parent = validate(player);
				this.name = validate(player.getName());
				if (users.read(c -> c.isNode(name))) {
					reference = users.read(c -> UUID.fromString(c.getNode(name).getNode("id").toPrimitive().getString()));
				} else {
					reference = player.getUniqueId();
					users.write(t -> t.set(name + ".id", reference.toString()));
				}
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
				return parent;
			}

			@Override
			public @NotNull UUID getRecordedId() {
				return reference;
			}

			@Override
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
