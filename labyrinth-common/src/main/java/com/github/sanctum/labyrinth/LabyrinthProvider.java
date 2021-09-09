package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.ServiceType;

/**
 * @author ms5984
 */
public abstract class LabyrinthProvider {
	static LabyrinthAPI instance;

	private LabyrinthProvider() {
		throw new IllegalStateException("This class should never be instantiated!");
	}

	/**
	 * Get the registered instance of the Labyrinth API.
	 *
	 * @return instance of the Labyrinth API
	 */
	public static LabyrinthAPI getInstance() {
		return instance;
	}

	/**
	 * Get a service from cache.
	 *
	 * <p>
	 *     Example services are:
	 *
	 *     {@link Service#COMPONENTS}
	 *     {@link Service#COOLDOWNS}
	 *     {@link Service#LEGACY}
	 *     {@link Service#MESSENGER}
	 *     {@link Service#DATA}
	 *     {@link Service#RECORDING}
	 *     {@link Service#TASK}
	 *     {@link Service#VENT}
	 *
	 *     <p/>
	 *
	 * @param type The service to use.
	 * @param <T> The type of service.
	 * @return The service.
	 */
	public static  <T extends Service> T getService(ServiceType<T> type) {
		return getInstance().getServiceManager().get(type);
	}

}
