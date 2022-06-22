package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.annotation.Note;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TypeFlag<T> extends Type {

	TypeFlag<Player> PLAYER = () -> Player.class;
	TypeFlag<UUID> UUID = () -> UUID.class;
	TypeFlag<HUID> HUID = () -> HUID.class;
	TypeFlag<String> STRING = () -> String.class;
	TypeFlag<Boolean> BOOLEAN = () -> Boolean.class;
	TypeFlag<Number> NUMBER = () -> Number.class;

	Class<T> getType();

	@Override
	default String getTypeName() {
		return getType().getTypeName();
	}

	default T cast(Object o) {
		return (T) o;
	}

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

	@Note("Use this to parse type flags magically using tokens!")
	static <T> TypeFlag<T> get() {
		return new EasyTypeAdapter<>();
	}


}
