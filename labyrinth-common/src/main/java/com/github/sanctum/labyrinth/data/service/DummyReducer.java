package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.annotation.Json;
import java.util.HashMap;
import java.util.Map;

public final class DummyReducer implements Json.Reducer {
	@Override
	public Map<String, Object> reduce(Object o) {
		Map<String, Object> map = new HashMap<>();
		map.put("element", o.toString());
		return map;
	}
}
