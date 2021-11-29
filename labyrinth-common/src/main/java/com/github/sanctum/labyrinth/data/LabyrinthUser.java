package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.annotation.Ordinal;
import com.github.sanctum.labyrinth.interfacing.Nameable;
import com.github.sanctum.labyrinth.library.AFK;
import com.github.sanctum.labyrinth.library.VaultPlayer;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LabyrinthUser implements Nameable {

	private static final Map<String, LabyrinthUser> users = new HashMap<>();
	private static FileManager file;
	private boolean checked;
	private OfflinePlayer target = null;
	private final String name;
	private final List<String> names = new ArrayList<>();
	private UUID id;

	// The problem with serving offline (cracked) users is its near impossible to keep track of conversions etc. But im trying :/
	public LabyrinthUser(String name) {
		this.name = name;
		if (file == null) {
			file = FileList.search(LabyrinthProvider.getInstance().getPluginInstance()).get("user-data", "Persistent", FileType.JSON);
		}
		this.id = getID(name);
		if (toBukkit() != null) {
			users.put(name, this);
		}
	}

	public LabyrinthUser(OfflinePlayer pl) {
		this.name = pl.getName();
		if (file == null) {
			file = FileList.search(LabyrinthProvider.getInstance().getPluginInstance()).get("user-data", "Persistent", FileType.JSON);
		}
		this.target = pl;
		this.id = getID(name);
		if (toBukkit() != null) {
			users.put(name, this);
		}
	}

	public static LabyrinthUser get(@NotNull String name) {
		return Optional.ofNullable(users.get(name)).orElse(new LabyrinthUser(name));
	}

	public static LabyrinthUser get(@NotNull OfflinePlayer player) {
		return Optional.ofNullable(users.get(player.getName())).orElse(new LabyrinthUser(player));
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
		if (target == null) {
			getPlayer();
		}
		return target;
	}

	public boolean isOnline() {
		return toBukkit().getPlayer() != null;
	}

	public boolean isValid() {
		return Bukkit.getOfflinePlayer(getId()).hasPlayedBefore();
	}

	public VaultPlayer toVault() {
		return VaultPlayer.wrap(this);
	}

	public List<String> getKnownNames() {
		return names;
	}

	@Ordinal(5)
	boolean correctId(OfflinePlayer player) {
		if (checked) return false;
		if (this.id != null) {
			if (player.getUniqueId() != null) {
				if (!player.getUniqueId().equals(id)) {
					Node users = file.read(c -> c.getNode("users"));
					Node us = null;
					for (String u : users.getKeys(false)) {
						Node user = users.getNode(u);
						for (String known : user.getNode("names").toPrimitive().getStringList()) {
							if (known.equalsIgnoreCase(name)) {
								us = user;
								break;
							}
						}
					}
					if (us != null) {
						List<String> names = us.getNode("names").toPrimitive().getStringList();
						users.getNode(player.getUniqueId().toString()).getNode("names").set(names);
						us.delete();
						LabyrinthProvider.getInstance().getLogger().info("- Adjusted user " + getName() + "'s id to match the current online status.");
						LabyrinthProvider.getInstance().getLogger().info("- From " + this.id.toString() + " to " +  player.getUniqueId());
						this.id = player.getUniqueId();
						users.save();

					}
					doubleCheckName(player);
					return true;
				}
				doubleCheckName(player);
			}
		}
		checked = true;
		return false;
	}

	@Ordinal(4)
	boolean correctId(Player player) {
		if (checked) return false;
		if (this.id != null) {
			if (player.getUniqueId() != null) {
				if (!player.getUniqueId().equals(id)) {
					Node users = file.read(c -> c.getNode("users"));
					Node us = null;
					for (String u : users.getKeys(false)) {
						Node user = users.getNode(u);
						for (String known : user.getNode("names").toPrimitive().getStringList()) {
							if (known.equalsIgnoreCase(name)) {
								us = user;
								break;
							}
						}
					}
					if (us != null) {
						List<String> names = us.getNode("names").toPrimitive().getStringList();
						users.getNode(player.getUniqueId().toString()).getNode("names").set(names);
						us.delete();
						LabyrinthProvider.getInstance().getLogger().info("- Adjusted user " + getName() + "'s id to match the current online status.");
						LabyrinthProvider.getInstance().getLogger().info("- From " + this.id.toString() + " to " +  player.getUniqueId());
						this.id = player.getUniqueId();
						users.save();

					}
					doubleCheckName(player);
					return true;
				}
				doubleCheckName(player);
			}
		}
		checked = true;
		return false;
	}

	private void doubleCheckName(OfflinePlayer player) {
		Node users = file.read(c -> c.getNode("users"));
		Node user = users.getNode(this.id.toString());
		List<String> list = user.getNode("names").toPrimitive().getStringList();
		if (!list.contains(player.getName())) {
			list.add(player.getName());

		}
	}

	private void getPlayer() {
		CompletableFuture.runAsync(() -> {
			for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
				if (getName().equals(p.getName())) {
					target = p;
					break;
				}
			}
		}).join();
	}

	private UUID getID(String name) {
		Node users = file.read(c -> c.getNode("users"));
		UUID id = null;
		for (String u : users.getKeys(false)) {
			Node user = users.getNode(u);
			List<String> names = user.getNode("names").toPrimitive().getStringList();
			for (String known : names) {
				if (known.equalsIgnoreCase(name)) {
					this.names.addAll(names);
					id = UUID.fromString(u);
					break;
				}
			}
		}
		if (id == null) {
			for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
				if (name.equals(offline.getName())) {
					if (offline.getUniqueId() != null) {
						id = offline.getUniqueId();
						if (this.target == null) {
							this.target = offline;
						}
					}
					break;
				}
			}
			if (id == null) {
				LabyrinthProvider.getInstance().getLogger().warning("- User " + getName() + "doesn't seem to have a valid unique id.");
				id = UUID.randomUUID();
				LabyrinthProvider.getInstance().getLogger().info("- A new labyrinth persistent id has been issued (" + id.toString() + ")");
			}
			users.getNode(id.toString()).getNode("names").set(Collections.singletonList(name));
			users.save();
		}
		return id;
	}

}
