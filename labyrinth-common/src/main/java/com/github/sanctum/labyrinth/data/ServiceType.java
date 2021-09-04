package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.api.Service;

import java.util.Arrays;
import java.util.function.Supplier;

public class ServiceType<T extends Service> {

	final Supplier<T> supplier;
	Class<T> c;

	@SuppressWarnings("unchecked")
	public ServiceType(Supplier<T> supplier) {
		this.supplier = supplier;
		// Previous code caused lots of problems. Had to remove - Hemp
		c = (Class<T>) supplier.get().getClass();
	}

	public Class<T> getType() {
		return c;
	}

	public Supplier<T> getLoader() {
		return supplier;
	}
}
