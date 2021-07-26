package com.github.sanctum.labyrinth.library;

import com.google.common.base.Preconditions;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Encapsulate a player object and get the properties of the permissible inheritance.
 *
 * @author Hempfest
 */
public class VaultPlayer {

	private static final List<VaultPlayer> CACHE = new LinkedList<>();

	protected static Permission PERMS;

	private final OfflinePlayer player;

	static {
		PERMS = Bukkit.getServicesManager().load(Permission.class);
	}

	protected VaultPlayer(OfflinePlayer player) {
		Preconditions.checkArgument(PERMS != null, "No vault permission implementation found.");
		this.player = player;
	}

	public static VaultPlayer wrap(OfflinePlayer player) {
		VaultPlayer play = CACHE.stream().filter(p -> p.player.getUniqueId().equals(player.getUniqueId())).findFirst().orElse(null);
		if (play != null) {
			return play;
		}
		VaultPlayer pl = new VaultPlayer(player);
		CACHE.add(pl);
		return pl;
	}

	public Group getGroup(String world) {
		return new Group(PERMS.getPrimaryGroup(world, player), world);
	}

	public Group[] getGroups(String world) {
		return Arrays.stream(PERMS.getPlayerGroups(world, this.player)).map(s -> new Group(s, world)).toArray(Group[]::new);
	}

	public boolean has(org.bukkit.permissions.Permission permission, String world) {
		return PERMS.playerHas(world, this.player, permission.getName());
	}

	public boolean give(org.bukkit.permissions.Permission permission, String world) {
		return PERMS.playerAdd(world, this.player, permission.getName());
	}

	public boolean take(org.bukkit.permissions.Permission permission, String world) {
		return PERMS.playerRemove(world, this.player, permission.getName());
	}

	public boolean has(String permission, String world) {
		return PERMS.playerHas(world, this.player, permission);
	}

	public boolean give(String permission, String world) {
		return PERMS.playerAdd(world, this.player, permission);
	}

	public boolean take(String permission, String world) {
		return PERMS.playerRemove(world, this.player, permission);
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
