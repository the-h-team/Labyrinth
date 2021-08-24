package com.github.sanctum.labyrinth.data;

import java.util.List;

public class RegistryData<T> {

	private final List<T> list;

	private final Object loader;

	private final String location;

	protected RegistryData(List<T> list, Object loader, String location) {
		this.list = list;
		this.loader = loader;
		this.location = location;
	}

	public Object getLoader() {
		return this.loader;
	}

	public String getLocation() {
		return location;
	}

	public List<T> getData() {
		return list;
	}
}
