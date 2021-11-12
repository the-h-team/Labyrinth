package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.data.LabyrinthUser;
import com.github.sanctum.labyrinth.data.service.Check;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;

/**
 * Encapsulate a player object and get the properties of the permissible inheritance.
 *
 * @author Hempfest
 */
public class VaultPlayer {

	private static final List<VaultPlayer> CACHE = new LinkedList<>();

	private final LabyrinthUser player;

	static Permission getPerms() {
		return Bukkit.getServicesManager().load(Permission.class);
	}

	protected VaultPlayer(@NotNull OfflinePlayer player) {
		this.player = LabyrinthUser.get(Check.forNull(player.getName(), "Invalid user provided! Name cannot be null"));
	}

	protected VaultPlayer(@NotNull LabyrinthUser player) {
		this.player = player;
	}

	public static VaultPlayer wrap(OfflinePlayer player) {
		VaultPlayer play = CACHE.stream().filter(p -> p.player.getName().equals(player.getName())).findFirst().orElse(null);
		if (play != null) {
			return play;
		}
		VaultPlayer pl = new VaultPlayer(player);
		CACHE.add(pl);
		return pl;
	}

	public static VaultPlayer wrap(LabyrinthUser player) {
		VaultPlayer play = CACHE.stream().filter(p -> p.player.getId().equals(player.getId())).findFirst().orElse(null);
		if (play != null) {
			return play;
		}
		VaultPlayer pl = new VaultPlayer(player);
		CACHE.add(pl);
		return pl;
	}

	public Group getGroup(String world) {
		if (getPerms() == null) {
			return new Group("no-vault", world);
		}
		return new Group(getPerms().getPrimaryGroup(world, player.toBukkit()), world);
	}

	public Group[] getGroups(String world) {
		if (getPerms() == null) {
			return new Group[]{new Group("no-vault", world)};
		}
		return Arrays.stream(getPerms().getPlayerGroups(world, this.player.toBukkit())).map(s -> new Group(s, world)).toArray(Group[]::new);
	}

	public boolean has(org.bukkit.permissions.Permission permission, String world) {
		if (getPerms() == null) {
			return false;
		}
		return getPerms().playerHas(world, this.player.toBukkit(), permission.getName());
	}

	public boolean give(org.bukkit.permissions.Permission permission, String world) {
		if (getPerms() == null) {
			return false;
		}
		return getPerms().playerAdd(world, this.player.toBukkit(), permission.getName());
	}

	public boolean take(org.bukkit.permissions.Permission permission, String world) {
		if (getPerms() == null) {
			return false;
		}
		return getPerms().playerRemove(world, this.player.toBukkit(), permission.getName());
	}

	public boolean has(String permission, String world) {
		if (getPerms() == null) {
			return false;
		}
		return getPerms().playerHas(world, this.player.toBukkit(), permission);
	}

	public boolean give(String permission, String world) {
		if (getPerms() == null) {
			return false;
		}
		return getPerms().playerAdd(world, this.player.toBukkit(), permission);
	}

	public boolean take(String permission, String world) {
		if (getPerms() == null) {
			return false;
		}
		return getPerms().playerRemove(world, this.player.toBukkit(), permission);
	}

	public org.bukkit.permissions.Permission[] getKnownPermissions(String world) {
		List<org.bukkit.permissions.Permission> perms = new LinkedList<>();
		for (String cmd : CommandUtils.getServerCommandListing()) {
			Command command = CommandUtils.getCommandByLabel(cmd);
			if (command != null) {
				if (command.getPermission() != null) {
					if (has(command.getPermission(), world)) {
						perms.add(new org.bukkit.permissions.Permission(command.getPermission(), command.getDescription()));
					}
				}
			}
		}
		return perms.toArray(new org.bukkit.permissions.Permission[0]);
	}


}
