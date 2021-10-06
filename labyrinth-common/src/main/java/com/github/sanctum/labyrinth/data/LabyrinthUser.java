package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.interfacing.Nameable;
import com.github.sanctum.labyrinth.library.AFK;
import com.github.sanctum.labyrinth.library.VaultPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

	public LabyrinthUser(String name) {
		this.name = name;
		if (file == null) {
			file = FileList.search(LabyrinthProvider.getInstance().getPluginInstance()).get("user-data", "Persistent", FileType.JSON);
		}
		this.id = getID(name);
		getPlayer();
	}

	public static LabyrinthUser get(@NotNull String name) {
		LabyrinthUser user = users.get(name);
		if (user == null) {
			LabyrinthUser n = new LabyrinthUser(name);
			if (n.isValid()) {
				users.put(name, n);
				return n;
			}
		}
		return user;
	}

	public UUID getId() {
		return this.id;
	}

	@Override
	public @NotNull String getName() {
		return this.name;
	}

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
		return Arrays.stream(Bukkit.getOfflinePlayers()).anyMatch(p -> this.name.equals(p.getName()));
	}

	public VaultPlayer toVault() {
		return VaultPlayer.wrap(this);
	}

	public List<String> getKnownNames() {
		return names;
	}

	@Note("This is just a utility method, you should never use it!!!")
	public boolean correctId(Player player) {
		if (checked) return false;
		checked = true;
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
						this.id = player.getUniqueId();
						users.save();

					}
					doubleCheckName(player);
					return true;
				}
				doubleCheckName(player);
			}
		}
		return false;
	}

	private void doubleCheckName(Player player) {
		Node users = file.read(c -> c.getNode("users"));
		Node user = users.getNode(this.id.toString());
		List<String> list = user.getNode("names").toPrimitive().getStringList();
		if (!list.contains(player.getName())) {
			list.add(player.getName());

		}
	}

	private void getPlayer() {
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			if (getName().equals(p.getName())) {
				target = p;
				break;
			}
		}
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
					}
					break;
				}
			}
			if (id == null) {
				id = UUID.randomUUID();
			}
			users.getNode(id.toString()).getNode("names").set(Collections.singletonList(name));
			users.save();
		}
		return id;
	}

}
