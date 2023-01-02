package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.ServiceType;
import com.github.sanctum.panther.annotation.FieldsFrom;
import com.github.sanctum.panther.annotation.Note;

/**
 * <pre>
 * ▄▄▌***▄▄▄·*▄▄▄▄·**▄·*▄▌▄▄▄**▪***▐*▄*▄▄▄▄▄*▄*.▄
 * ██•**▐█*▀█*▐█*▀█▪▐█▪██▌▀▄*█·██*•█▌▐█•██**██▪▐█
 * ██▪**▄█▀▀█*▐█▀▀█▄▐█▌▐█▪▐▀▀▄*▐█·▐█▐▐▌*▐█.▪██▀▐█
 * ▐█▌▐▌▐█*▪▐▌██▄▪▐█*▐█▀·.▐█•█▌▐█▌██▐█▌*▐█▌·██▌▐▀
 * .▀▀▀**▀**▀*·▀▀▀▀***▀*•*.▀**▀▀▀▀▀▀*█▪*▀▀▀*▀▀▀*·
 * </pre>
 * Copyright (c) 2020-2023 <strong>Sanctum</strong>
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
	 * <h3>Example services are: </h3>
	 * <pre>
	 *     {@link Service#COMPONENTS}
	 *     {@link Service#COOLDOWNS}
	 *     {@link Service#LEGACY}
	 *     {@link Service#MESSENGER}
	 *     {@link Service#DATA}
	 *     {@link Service#RECORDING}
	 *     {@link Service#TASK}
	 *     </pre>
	 *
	 * @param type The service to use.
	 * @param <T>  The type of service.
	 * @return The service.
	 * @throws IllegalArgumentException If the provided service type isn't loaded.
	 */
	@Note("This method only works on loaded services!")
	public static <T extends Service> T getService(@FieldsFrom(Service.class) ServiceType<T> type) {
		return getInstance().getServiceManager().get(type);
	}

	@Note("This method only works on loaded services!")
	public static <T extends Service> T getService(Class<T> service) {
		return getInstance().getServiceManager().get(service);
	}

}
