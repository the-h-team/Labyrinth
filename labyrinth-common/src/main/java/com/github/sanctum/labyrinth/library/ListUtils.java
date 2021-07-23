package com.github.sanctum.labyrinth.library;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Hempfest
 */
public class ListUtils<T> {

	private final List<T> list;

	protected ListUtils(List<T> list) {
		this.list = new ArrayList<>(list);
	}

	/**
	 * Append/replace elements within a given list.
	 *
	 * @param list the provided list
	 * @param <T> the object in use for this element separation
	 * @return a list utility for element appendage/replacement
	 */
	public static <T> ListUtils<T> use(List<T> list) {
		return new ListUtils<>(list);
	}

	/**
	 * Append information to each element in the list using the provided
	 * objects properties instead of (T + T) style.
	 *
	 * @param action The accepted element to append info to. In reference demands no return statement, allows custom object
	 *               modification.
	 * @return the newly modified list
	 */
	public List<T> append(Consumer<T> action) {
		List<T> array = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			T t = list.get(i);
			if (i != list.size() - 1) {
				action.accept(t);
			}
			array.add(t);
		}
		return array;
	}

	/**
	 * Append information to each element in the list using object appendage or replacement.
	 *
	 * @param object The object to either append or entirely replace. Demands a return statement of the same object type.
	 * @return the newly modified list
	 */
	public List<T> append(ListOperation<T> object) {
		List<T> array = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			T t = list.get(i);
			if (i != list.size() - 1) {
				array.add(object.append(t));
			} else {
				array.add(t);
			}
		}
		return array;
	}

	@FunctionalInterface
	public interface ListOperation<T> {
		T append(T object);
	}
}
