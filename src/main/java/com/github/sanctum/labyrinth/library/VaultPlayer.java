package com.github.sanctum.labyrinth.library;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;

/**
 * Encapsulate a player object and get the properties of the permissible inheritance.
 */
public class VaultPlayer {

	private static final List<VaultPlayer> CACHE = new LinkedList<>();

	private final OfflinePlayer player;

	protected VaultPlayer(OfflinePlayer player) {
		Preconditions.checkArgument(Bukkit.getServicesManager().load(Permission.class) != null, "No vault permission implementation found.");
		this.player = player;
		CACHE.add(this);
	}

	public static VaultPlayer wrap(OfflinePlayer player) {
		return CACHE.stream().filter(p -> p.player.getName().equals(player.getName())).findFirst().orElse(new VaultPlayer(player));
	}

	protected static Permission PERMS = Bukkit.getServicesManager().load(Permission.class);

	public Group getGroup(String world) {
		return new Group(PERMS.getPrimaryGroup(world, this.player), world);
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
