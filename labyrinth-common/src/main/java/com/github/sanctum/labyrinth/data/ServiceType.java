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
		c = (Class<T>) Arrays.stream(supplier.getClass().getMethods())
				.filter(m -> m.getName().equals("get") && m.getParameterTypes().length == 0)
				.findFirst().orElseThrow((IllegalArgumentException::new)).getReturnType();
	}

	public Class<T> getType() {
		return c;
	}

	public Supplier<T> getLoader() {
		return supplier;
	}
}
