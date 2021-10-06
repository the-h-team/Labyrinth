package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.annotation.Json;
import java.util.Map;

public class DummyReducer implements Json.Reducer {
	@Override
	public Map<String, Object> reduce(Object o) {
		return null;
	}
}
