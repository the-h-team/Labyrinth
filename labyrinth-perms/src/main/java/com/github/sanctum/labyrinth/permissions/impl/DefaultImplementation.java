package com.github.sanctum.labyrinth.permissions.impl;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.LabyrinthUser;
import com.github.sanctum.labyrinth.permissions.Permissions;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

public class DefaultImplementation implements Permissions {

	final Map<String, com.github.sanctum.labyrinth.permissions.entity.User> users = new HashMap<>();
	final Map<String, com.github.sanctum.labyrinth.permissions.entity.Group> groups = new HashMap<>();

	@Override
	public Plugin getProvider() {
		return LabyrinthProvider.getInstance().getPluginInstance();
	}

	@Override
	public boolean isMultiWorld() {
		return false;
	}

	@Override
	public boolean isSuperPerms() {
		return false;
	}

	@Override
	public boolean isGroupsAllowed() {
		return false;
	}

	@Override
	public com.github.sanctum.labyrinth.permissions.entity.User getUser(OfflinePlayer player) {
		return users.computeIfAbsent(player.getName(), User::new);
	}

	@Override
	public com.github.sanctum.labyrinth.permissions.entity.User getUser(LabyrinthUser user) {
		return users.computeIfAbsent(user.getName(), User::new);
	}

	@Override
	public com.github.sanctum.labyrinth.permissions.entity.Group getGroup(String name) {
		return groups.computeIfAbsent(name, Group::new);
	}

	@Override
	public com.github.sanctum.labyrinth.permissions.entity.User[] getUsers() {
		return users.values().toArray(new com.github.sanctum.labyrinth.permissions.entity.User[0]);
	}

	@Override
	public com.github.sanctum.labyrinth.permissions.entity.Group[] getGroups() {
		return groups.values().toArray(new com.github.sanctum.labyrinth.permissions.entity.Group[0]);
	}

	static class User implements com.github.sanctum.labyrinth.permissions.entity.User {

		private final LabyrinthUser user;

		User(String name) {
			this.user = LabyrinthUser.get(name);
		}

		@Override
		public LabyrinthUser getLabyrinth() {
			return this.user;
		}

		@Override
		public com.github.sanctum.labyrinth.permissions.entity.Group getGroup() {
			return null;
		}

		@Override
		public com.github.sanctum.labyrinth.permissions.entity.Group getGroup(String world) {
			return null;
		}

		@Override
		public boolean has(String node) {
			return false;
		}

		@Override
		public boolean has(String node, String world) {
			return false;
		}

		@Override
		public boolean give(String node) {
			return false;
		}

		@Override
		public boolean give(String node, String world) {
			return false;
		}

		@Override
		public boolean take(String node) {
			return false;
		}

		@Override
		public boolean take(String node, String world) {
			return false;
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
		public com.github.sanctum.labyrinth.permissions.entity.Inheritance getInheritance() {
			return new Inheritance();
		}
	}

	static class Group implements com.github.sanctum.labyrinth.permissions.entity.Group {

		private final String name;

		public Group(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean has(String node) {
			return false;
		}

		@Override
		public boolean has(String node, String world) {
			return false;
		}

		@Override
		public boolean give(String node) {
			return false;
		}

		@Override
		public boolean give(String node, String world) {
			return false;
		}

		@Override
		public boolean take(String node) {
			return false;
		}

		@Override
		public boolean take(String node, String world) {
			return false;
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
		public com.github.sanctum.labyrinth.permissions.entity.Inheritance getInheritance() {
			return new Inheritance();
		}
	}

	static class Inheritance implements com.github.sanctum.labyrinth.permissions.entity.Inheritance {

		@Override
		public com.github.sanctum.labyrinth.permissions.entity.Group[] getSubGroups() {
			return new com.github.sanctum.labyrinth.permissions.entity.Group[0];
		}

		@Override
		public com.github.sanctum.labyrinth.permissions.entity.Group[] getSubGroups(String world) {
			return new com.github.sanctum.labyrinth.permissions.entity.Group[0];
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
