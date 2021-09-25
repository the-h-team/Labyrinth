package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.library.Deployable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h2>Constantly consistent...</h2>
 * <p>The underlying data behind this object is that of a constant field belonging to a specified class.</p>
 *
 * @param <T> The type of object this constant represents.
 */
public interface Constant<T> {

	String getName();

	Class<T> getType();

	Class<?> getParent();

	T getValue();

	default List<T> getCousins() {
		return values(getParent(), getType());
	}

	static <T> List<T> values(Type source, Class<T> c) {
		Deployable<List<T>> deployable = AccessibleConstants.collect(source).resolve(c);
		deployable.queue();
		return deployable.submit().join();
	}

	static List<Object> values(Type source) {
		Deployable<Map<Class<?>, List<Object>>> deployable = AccessibleConstants.collect(source).resolve();
		deployable.queue();
		Deployable<Map<Class<?>, List<Object>>> deployable2 = ProtectedConstants.collect(source).resolve();
		deployable2.queue();
		List<Object> list = new ArrayList<>();
		for (Collection<Object> l : deployable.submit().join().values()) {
			list.addAll(l);
		}
		for (Collection<Object> l : deployable2.submit().join().values()) {
			list.addAll(l);
		}
		return list;
	}

	static List<Object> values(Object source) {
		Deployable<Map<Class<?>, List<Object>>> deployable = AccessibleConstants.collect(source).resolve();
		deployable.queue();
		Deployable<Map<Class<?>, List<Object>>> deployable2 = ProtectedConstants.collect(source).resolve();
		deployable2.queue();
		List<Object> list = new ArrayList<>();
		for (Collection<Object> l : deployable.submit().join().values()) {
			list.addAll(l);
		}
		for (Collection<Object> l : deployable2.submit().join().values()) {
			list.addAll(l);
		}
		return list;
	}

}
