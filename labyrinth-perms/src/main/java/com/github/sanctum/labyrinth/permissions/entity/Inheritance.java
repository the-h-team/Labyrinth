package com.github.sanctum.labyrinth.permissions.entity;

import org.bukkit.permissions.Permission;

public interface Inheritance {

	Group[] getSubGroups();

	Group[] getSubGroups(String world);

	boolean has(String group);

	boolean has(String group, String world);

	boolean has(Permission node);

	boolean has(Permission node, String world);

	boolean give(String group);

	boolean give(String group, String world);

	boolean take(String group);

	boolean take(String group, String world);

	default boolean test(String node) {
		return has(new Permission(node));
	}

	default boolean test(String node, String world) {
		return has(new Permission(node), world);
	}


}
