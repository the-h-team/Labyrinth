package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.service.Check;
import java.util.HashMap;
import java.util.Map;

/**
 * A small and simple service loader.
 * @author rigobert0
 */
public class ServiceManager {
	final Map<ServiceType<?>, Service> services;

	public ServiceManager() {
		this.services = new HashMap<>();
	}

	public void load(ServiceType<? extends Service> type) {
		services.put(type, type.getLoader().get());
	}

	public <T extends Service> T get(Class<T> service) {
		return (T) services.entrySet().stream().filter(e -> service.isAssignableFrom(e.getValue().getClass())).map(Map.Entry::getValue).findFirst().orElse(null);
	}

	public <T extends Service> T get(ServiceType<T> serviceType) {
		Object service = services.entrySet().stream().filter(t -> serviceType.getType().isAssignableFrom(t.getKey().getType())).findFirst().map(Map.Entry::getValue).orElse(null);
		return Check.forNull((T)service, "No loaded instance of service type " + serviceType.getType().getSimpleName() + " was found.");
	}


}
