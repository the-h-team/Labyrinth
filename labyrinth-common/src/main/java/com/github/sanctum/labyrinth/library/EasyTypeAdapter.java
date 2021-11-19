package com.github.sanctum.labyrinth.library;

import com.google.gson.reflect.TypeToken;

public class EasyTypeAdapter<T> implements TypeFlag<T> {

	private final TypeToken<T> token;

	public EasyTypeAdapter() {
		this.token = new TypeToken<T>(){};
	}

	@Override
	public Class<T> getType() {
		return (Class<T>) token.getRawType();
	}
}
