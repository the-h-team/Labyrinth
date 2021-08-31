package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.api.Service;
import java.util.HashMap;
import java.util.Map;

public class ServiceManager {
	final Map<ServiceType<?>, Service> services;

	public ServiceManager() {
		this.services = new HashMap<>();
	}

	public void load(ServiceType<? extends Service> type) {
		services.put(type, type.getLoader().get());
	}

	public <T extends Service> T get(ServiceType<T> serviceType) {
		T service = (T) services.entrySet().stream().filter(t -> t.getKey().getType().isAssignableFrom(serviceType.getType())).findFirst().map(Map.Entry::getValue).orElse(null);
		if (service != null) {
			return service;
		}
		return serviceType.getLoader().get();
	}


}
