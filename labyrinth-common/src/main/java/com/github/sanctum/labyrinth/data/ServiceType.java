package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.api.Service;

import com.github.sanctum.panther.util.EasyTypeAdapter;

public class ServiceType<T extends Service> {

	Class<T> c;

	public ServiceType() {
		c = new EasyTypeAdapter<T>().getType();
	}

	public Class<T> getType() {
		return c;
	}

}
