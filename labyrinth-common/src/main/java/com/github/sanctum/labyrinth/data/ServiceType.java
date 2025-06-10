package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.api.Service;

import com.github.sanctum.panther.util.EasyTypeAdapter;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@Deprecated
@ApiStatus.ScheduledForRemoval
public class ServiceType<T extends Service> {

	final Supplier<T> supplier;
	Class<T> c;

	public ServiceType(Supplier<T> supplier) {
		this.supplier = supplier;
		c = (Class<T>) supplier.get().getClass();
	}

	public Class<T> getType() {
		return c;
	}

	public Supplier<T> getLoader() {
		return supplier;
	}

}
