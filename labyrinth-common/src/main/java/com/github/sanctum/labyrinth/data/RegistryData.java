package com.github.sanctum.labyrinth.data;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class RegistryData<T> implements Iterable<T> {

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
		return Collections.unmodifiableList(list);
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}
}
