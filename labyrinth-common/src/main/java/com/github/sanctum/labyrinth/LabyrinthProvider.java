package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.ServiceType;
import com.github.sanctum.panther.annotation.FieldsFrom;
import com.github.sanctum.panther.annotation.Note;
import org.jetbrains.annotations.Nullable;

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
	 * Gets a service from cache.
	 * <h3>Example services:</h3>
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
	 * @param type the service type
	 * @return the service instance
	 * @throws IllegalArgumentException If the provided service type isn't loaded.
	 */
	@Note("This method only works on loaded services!")
	public static <T extends Service> T getService(@FieldsFrom(Service.class) ServiceType<T> type) {
		final T load = getInstance().getServiceManager().newLoader(type.getType()).load();
		//noinspection ConstantValue
		if (load == null) {
			throw new IllegalArgumentException("No loaded instance of service type " + type.getType().getSimpleName() + " was found.");
		}
		return load;
	}

	@SuppressWarnings("DataFlowIssue")
	@Note("This method only works on loaded services!")
	public static <T extends Service> @Nullable T getService(Class<T> service) {
		return getInstance().getServiceManager().newLoader(service).load();
	}

}
