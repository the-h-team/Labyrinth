package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.data.container.LabyrinthEntryMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;
import com.github.sanctum.labyrinth.data.service.PlayerSearch;
import com.github.sanctum.labyrinth.interfacing.Nameable;
import com.github.sanctum.labyrinth.library.AFK;
import com.github.sanctum.labyrinth.library.VaultPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class LabyrinthUser implements Nameable {

	static final LabyrinthMap<String, LabyrinthUser> users = new LabyrinthEntryMap<>();
	private OfflinePlayer target = null;
	private final String name;
	private final List<String> names = new ArrayList<>();
	private final UUID id;

	// The problem with serving offline (cracked) users is its near impossible to keep track of conversions etc. But im trying :/
	public LabyrinthUser(String name) {
		this.name = name;
		PlayerSearch lookup = PlayerSearch.of(name);
		this.id = lookup.getRecordedId();
		this.target = lookup.getPlayer();
	}

	public LabyrinthUser(OfflinePlayer pl) {
		this.name = pl.getName();
		PlayerSearch lookup = PlayerSearch.of(pl);
		this.target = lookup.getPlayer();
		this.id = lookup.getRecordedId();
	}

	@Deprecated
	public static LabyrinthUser get(@NotNull String name) {
		return users.computeIfAbsent(name, LabyrinthUser::new);
	}

	@Deprecated
	public static LabyrinthUser get(@NotNull OfflinePlayer player) {
		return Optional.ofNullable(users.get(player.getName())).orElseGet(() -> {
			LabyrinthUser n = new LabyrinthUser(player);
			users.put(player.getName(), n);
			return n;
		});
	}

	public UUID getId() {
		return this.id;
	}

	@Override
	public @NotNull String getName() {
		return this.name;
	}

	@Note("May provide unwanted effects (If you don't want multiple afk plugins running), an active AFK impl will be supplied if not found.")
	public AFK toAFK() {
		return AFK.supply(toBukkit().getPlayer());
	}

	public OfflinePlayer toBukkit() {
		return target;
	}

	public boolean isOnline() {
		return toBukkit().getPlayer() != null;
	}

	public boolean isValid() {
		return id != null;
	}

	public VaultPlayer toVault() {
		return VaultPlayer.wrap(this);
	}

	public List<String> getKnownNames() {
		return names;
	}

}
