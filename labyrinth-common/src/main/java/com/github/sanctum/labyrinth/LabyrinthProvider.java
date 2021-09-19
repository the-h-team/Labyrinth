package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.ServiceType;

/**
 * ▄▄▌***▄▄▄·*▄▄▄▄·**▄·*▄▌▄▄▄**▪***▐*▄*▄▄▄▄▄*▄*.▄
 * ██•**▐█*▀█*▐█*▀█▪▐█▪██▌▀▄*█·██*•█▌▐█•██**██▪▐█
 * ██▪**▄█▀▀█*▐█▀▀█▄▐█▌▐█▪▐▀▀▄*▐█·▐█▐▐▌*▐█.▪██▀▐█
 * ▐█▌▐▌▐█*▪▐▌██▄▪▐█*▐█▀·.▐█•█▌▐█▌██▐█▌*▐█▌·██▌▐▀
 * .▀▀▀**▀**▀*·▀▀▀▀***▀*•*.▀**▀▀▀▀▀▀*█▪*▀▀▀*▀▀▀*·
 * Copyright (C) 2021 <strong>Sanctum</strong>
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * </p>
 * -
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * </p>
 * -
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 * </p>
 * Sanctum, hereby disclaims all copyright interest in the original features of this spigot library.
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
	 *     {@link Service#VENT}
	 *     </pre>
	 *
	 * @param type The service to use.
	 * @param <T> The type of service.
	 * @throws IllegalArgumentException If the provided service type isn't loaded.
	 * @return The service.
	 */
	@Note("This method only works on loaded services!")
	public static  <T extends Service> T getService(ServiceType<T> type) {
		return getInstance().getServiceManager().get(type);
	}

	@Note("This method only works on loaded services!")
	public static <T extends Service> T getService(Class<T> service) {
		return getInstance().getServiceManager().get(service);
	}

}
