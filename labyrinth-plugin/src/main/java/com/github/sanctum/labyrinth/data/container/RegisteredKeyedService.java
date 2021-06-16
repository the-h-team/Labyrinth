package com.github.sanctum.labyrinth.data.container;

import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

public class RegisteredKeyedService<T, K> {

	private final T service;

	private final Class<T> path;

	private final ServicePriority priority;

	private final K key;

	protected RegisteredKeyedService(T service, K key, ServicePriority priority) {
		this.service = service;
		this.priority = priority;
		this.key = key;
		this.path = (Class<T>) service.getClass();
	}

	public Class<T> getSuperClass() {
		return this.path;
	}

	public K getKey() {
		return this.key;
	}

	@NotNull
	public ServicePriority getPriority() {
		return this.priority;
	}

	public T getService() {
		return this.service;
	}
}
