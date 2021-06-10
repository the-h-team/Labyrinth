package com.github.sanctum.labyrinth.library;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.permissions.Permission;

/**
 * @author Hempfest
 */
public class Group {

	private final String name;
	private final String world;

	protected Group(String name, String world) {
		this.name = name;
		this.world = world;
	}

	public String getName() {
		return this.name;
	}

	public World getWorld() {
		return Bukkit.getWorld(this.world);
	}

	public boolean has(Permission permission) {
		return VaultPlayer.PERMS.groupHas(getWorld(), getName(), permission.getName());
	}

	public boolean has(String permission) {
		return VaultPlayer.PERMS.groupHas(getWorld(), getName(), permission);
	}

	public boolean give(String permission) {
		return VaultPlayer.PERMS.groupAdd(getWorld(), getName(), permission);
	}

	public boolean give(Permission permission) {
		return VaultPlayer.PERMS.groupAdd(getWorld(), getName(), permission.getName());
	}

	public boolean take(String permission) {
		return VaultPlayer.PERMS.groupRemove(getWorld(), getName(), permission);
	}

	public boolean take(Permission permission) {
		return VaultPlayer.PERMS.groupRemove(getWorld(), getName(), permission.getName());
	}

	public Permission[] getKnownPermissions() {
		List<Permission> perms = new LinkedList<>();
		for (String cmd : CommandUtils.getServerCommandListing()) {
			Command command = CommandUtils.getCommandByLabel(cmd);
			if (command != null) {
				if (command.getPermission() != null) {
					if (VaultPlayer.PERMS.groupHas(getWorld(), getName(), command.getPermission())) {
						perms.add(new Permission(command.getPermission(), command.getDescription()));
					}
				}
			}
		}
		return perms.toArray(new Permission[0]);
	}

}
