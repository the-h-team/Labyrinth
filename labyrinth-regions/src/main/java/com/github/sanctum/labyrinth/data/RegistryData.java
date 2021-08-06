package com.github.sanctum.labyrinth.data;

import org.bukkit.plugin.Plugin;

import java.util.List;

public class RegistryData<T> {

	private final List<T> list;

	private final Plugin plugin;

	private final String location;

	protected RegistryData(List<T> list, Plugin plugin, String location) {
		this.list = list;
		this.plugin = plugin;
		this.location = location;
	}

	public Plugin getPlugin() {
		return this.plugin;
	}

	public String getLocation() {
		return location;
	}

	public List<T> getData() {
		return list;
	}
}
