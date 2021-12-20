package com.github.sanctum.labyrinth.library;

import java.io.Serializable;
import java.util.Map;

class LabyrinthJsonObjectWrapper<T extends Map<String, Object>> implements Serializable {
	private static final long serialVersionUID = -2360151064283982235L;

	final T t;
	final String pointer;

	LabyrinthJsonObjectWrapper(T t, String pointer) {
		this.t = t;
		this.pointer = pointer;
	}

}
