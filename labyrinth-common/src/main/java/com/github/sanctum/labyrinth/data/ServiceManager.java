package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.service.Check;
import java.util.HashMap;
import java.util.Map;

/**
 * A small and simple service loader.
 * @author rigobert0
 */
public final class ServiceManager {
	final Map<ServiceType<?>, Service> services;

	public ServiceManager() {
		this.services = new HashMap<>();
	}

	public void load(ServiceType<? extends Service> type) {
		services.put(type, type.getLoader().get());
	}

	public <T extends Service> T get(Class<T> service) {
		return service.cast(services.values().stream().filter(v -> service.isAssignableFrom(v.getClass())).findFirst().orElse(null));
	}

	public <T extends Service> T get(ServiceType<T> serviceType) {
		T service = services.entrySet().stream().filter(t -> serviceType.equals(t.getKey())).findFirst().map(e -> serviceType.getType().cast(e.getValue())).orElse(null);
		return Check.forNull(service, "No loaded instance of service type " + serviceType.getType().getSimpleName() + " was found.");
	}


}
