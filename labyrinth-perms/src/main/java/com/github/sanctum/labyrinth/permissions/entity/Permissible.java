package com.github.sanctum.labyrinth.permissions.entity;

public interface Permissible {

	boolean has(String node);

	boolean has(String node, String world);

	boolean give(String node);

	boolean give(String node, String world);

	boolean take(String node);

	boolean take(String node, String world);

	String[] getPermissions();

	String[] getPermissions(String world);

	Inheritance getInheritance();

}
