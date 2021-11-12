package com.github.sanctum.labyrinth.permissions.impl;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.LabyrinthUser;
import com.github.sanctum.labyrinth.permissions.Permissions;
import com.github.sanctum.labyrinth.permissions.entity.Group;
import com.github.sanctum.labyrinth.permissions.entity.Inheritance;
import com.github.sanctum.labyrinth.permissions.entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * This vault permission bridge isn't meant to be a very powerful alternative, it merely acts as a port to our self proclaimed better interface.
 */
public class VaultImplementation implements Permissions {

	static RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> api;
	final Map<String, User> users = new HashMap<>();
	final Map<String, Group> groups = new HashMap<>();

	public VaultImplementation() {
		api = Bukkit.getServicesManager().getRegistrations(net.milkbowl.vault.permission.Permission.class).stream().min(RegisteredServiceProvider::compareTo).orElse(null);
	}

	@Override
	public Plugin getProvider() {
		return api.getPlugin();
	}

	@Override
	public boolean isMultiWorld() {
		return false;
	}

	@Override
	public boolean isSuperPerms() {
		return api.getProvider().hasSuperPermsCompat();
	}

	@Override
	public boolean isGroupsAllowed() {
		return api.getProvider().hasGroupSupport();
	}

	@Override
	public User getUser(OfflinePlayer player) {
		return users.computeIfAbsent(player.getName(), UserImpl::new);
	}

	@Override
	public User getUser(LabyrinthUser user) {
		return users.computeIfAbsent(user.getName(), UserImpl::new);
	}

	@Override
	public Group getGroup(String name) {
		return groups.computeIfAbsent(name, GroupImpl::new);
	}

	@Override
	public User[] getUsers() {
		if (users.isEmpty()) {
			for (LabyrinthUser user : LabyrinthProvider.getOfflinePlayers()) {
				getUser(user);
			}
		}
		return users.values().toArray(new User[0]);
	}

	@Override
	public Group[] getGroups() {
		if (groups.isEmpty()) {
			for (String g : api.getProvider().getGroups()) {
				getGroup(g);
			}
		}
		return groups.values().toArray(new Group[0]);
	}

	class UserImpl implements User {

		private final LabyrinthUser user;
		private final Inheritance groupInheritance;

		UserImpl(String name) {
			this.user = LabyrinthUser.get(name);
			this.groupInheritance = new UserInheritance(user);
		}

		@Override
		public LabyrinthUser getLabyrinth() {
			return this.user;
		}

		@Override
		public Group getGroup() {
			return VaultImplementation.this.getGroup(api.getProvider().getPrimaryGroup(Bukkit.getWorlds().get(0).getName(), user.toBukkit()));
		}

		@Override
		public Group getGroup(String world) {
			return VaultImplementation.this.getGroup(api.getProvider().getPrimaryGroup(world, user.toBukkit()));
		}

		@Override
		public boolean has(String node) {
			return api.getProvider().playerHas(Bukkit.getWorlds().get(0).getName(), user.toBukkit(), node);
		}

		@Override
		public boolean has(String node, String world) {
			return api.getProvider().playerHas(world, user.toBukkit(), node);
		}

		@Override
		public boolean give(String node) {
			return api.getProvider().playerAdd(Bukkit.getWorlds().get(0).getName(), user.toBukkit(), node);
		}

		@Override
		public boolean give(String node, String world) {
			return api.getProvider().playerAdd(world, user.toBukkit(), node);
		}

		@Override
		public boolean take(String node) {
			return api.getProvider().playerRemove(Bukkit.getWorlds().get(0).getName(), user.toBukkit(), node);
		}

		@Override
		public boolean take(String node, String world) {
			return api.getProvider().playerRemove(world, user.toBukkit(), node);
		}

		@Override
		public String[] getPermissions() {
			return new String[0];
		}

		@Override
		public String[] getPermissions(String world) {
			return new String[0];
		}

		@Override
		public Inheritance getInheritance() {
			return groupInheritance;
		}
	}

	static class GroupImpl implements Group {

		private final String name;
		private final Inheritance groupInheritance;

		public GroupImpl(String name) {
			this.name = name;
			this.groupInheritance = new GroupInheritance(name);
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean has(String node) {
			return api.getProvider().groupHas(Bukkit.getWorlds().get(0).getName(), name, node);
		}

		@Override
		public boolean has(String node, String world) {
			return api.getProvider().groupHas(world, name, node);
		}

		@Override
		public boolean give(String node) {
			return api.getProvider().groupAdd(Bukkit.getWorlds().get(0).getName(), name, node);
		}

		@Override
		public boolean give(String node, String world) {
			return api.getProvider().groupAdd(world, name, node);
		}

		@Override
		public boolean take(String node) {
			return api.getProvider().groupRemove(Bukkit.getWorlds().get(0).getName(), name, node);
		}

		@Override
		public boolean take(String node, String world) {
			return api.getProvider().groupRemove(world, name, node);
		}

		@Override
		public String[] getPermissions() {
			return new String[0];
		}

		@Override
		public String[] getPermissions(String world) {
			return new String[0];
		}

		@Override
		public Inheritance getInheritance() {
			return groupInheritance;
		}
	}

	class UserInheritance implements Inheritance {

		private final LabyrinthUser user;

		UserInheritance(LabyrinthUser user) {
			this.user = user;
		}

		@Override
		public Group[] getSubGroups() {
			List<Group> groups = new ArrayList<>();
			for (String playerGroup : api.getProvider().getPlayerGroups(Bukkit.getWorlds().get(0).getName(), user.toBukkit())) {
				Group g = VaultImplementation.this.getGroup(playerGroup);
				if (g != null) {
					groups.add(g);
				}
			}
			return groups.toArray(new Group[0]);
		}

		@Override
		public Group[] getSubGroups(String world) {
			List<Group> groups = new ArrayList<>();
			for (String playerGroup : api.getProvider().getPlayerGroups(world, user.toBukkit())) {
				Group g = VaultImplementation.this.getGroup(playerGroup);
				if (g != null) {
					groups.add(g);
				}
			}
			return groups.toArray(new Group[0]);
		}

		@Override
		public boolean has(String group) {
			return api.getProvider().playerInGroup(Bukkit.getWorlds().get(0).getName(), user.toBukkit(), group);
		}

		@Override
		public boolean has(String group, String world) {
			return api.getProvider().playerInGroup(world, user.toBukkit(), group);
		}

		@Override
		public boolean has(Permission node) {
			return api.getProvider().has(user.toBukkit().getPlayer(), node.getName());
		}

		@Override
		public boolean has(Permission node, String world) {
			return has(node);
		}

		@Override
		public boolean give(String group) {
			return api.getProvider().playerAddGroup(Bukkit.getWorlds().get(0).getName(), user.toBukkit(), group);
		}

		@Override
		public boolean give(String group, String world) {
			return api.getProvider().playerAddGroup(world, user.toBukkit(), group);
		}

		@Override
		public boolean take(String group) {
			return api.getProvider().playerRemoveGroup(Bukkit.getWorlds().get(0).getName(), user.toBukkit(), group);
		}

		@Override
		public boolean take(String group, String world) {
			return api.getProvider().playerRemoveGroup(world, user.toBukkit(), group);
		}
	}

	static class GroupInheritance implements Inheritance {

		private final String name;

		GroupInheritance(String name) {
			this.name = name;
		}

		@Override
		public Group[] getSubGroups() {
			return new Group[0];
		}

		@Override
		public Group[] getSubGroups(String world) {
			return new Group[0];
		}

		@Override
		public boolean has(String group) {
			return false;
		}

		@Override
		public boolean has(String group, String world) {
			return false;
		}

		@Override
		public boolean has(Permission node) {
			return false;
		}

		@Override
		public boolean has(Permission node, String world) {
			return false;
		}

		@Override
		public boolean give(String group) {
			return false;
		}

		@Override
		public boolean give(String group, String world) {
			return false;
		}

		@Override
		public boolean take(String group) {
			return false;
		}

		@Override
		public boolean take(String group, String world) {
			return false;
		}
	}


}
