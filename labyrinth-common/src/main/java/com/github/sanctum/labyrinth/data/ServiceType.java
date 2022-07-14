package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.api.Service;

import com.github.sanctum.panther.util.EasyTypeAdapter;
import java.util.function.Supplier;

public class ServiceType<T extends Service> {

	final Supplier<T> supplier;
	Class<T> c;

	public ServiceType(Supplier<T> supplier) {
		this.supplier = supplier;
		c = new EasyTypeAdapter<T>().getType();
	}

	public Class<T> getType() {
		return c;
	}

	public Supplier<T> getLoader() {
		return supplier;
	}

}
