package com.github.sanctum.labyrinth.library;

import java.lang.reflect.Type;
import java.util.UUID;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface TypeFlag<T> {

	TypeFlag<Player> PLAYER = () -> Player.class;
	TypeFlag<UUID> UUID = () -> UUID.class;
	TypeFlag<String> STRING = () -> String.class;
	TypeFlag<Number> NUMBER = () -> Number.class;

	Class<T> getType();

	static TypeFlag<?> get(String name) throws RuntimeException {
		try {
			Class<?> clazz = Class.forName(name);
			return (TypeFlag<Object>) () -> (Class<Object>) clazz;
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("Class " + name + " not found!");
		}
	}

	static TypeFlag<?> get(Type type) {
		return get(type.getTypeName());
	}

}
