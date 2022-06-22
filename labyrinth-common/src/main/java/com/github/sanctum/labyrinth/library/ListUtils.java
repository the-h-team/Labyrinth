package com.github.sanctum.labyrinth.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Hempfest
 */
public class ListUtils<T> {

	private LinkedList<T> list;

	protected ListUtils(List<T> list) {
		this.list = new LinkedList<>(list);
	}

	/**
	 * Append/replace elements within a given list.
	 *
	 * @param list the provided list
	 * @param <T>  the object in use for this element separation
	 * @return a list utility for element appendage/replacement
	 */
	public static <T> ListUtils<T> use(List<T> list) {
		return new ListUtils<>(list);
	}

	/**
	 * Append/replace elements within a given list.
	 *
	 * @param list the provided list
	 * @param <T>  the object in use for this element separation
	 * @return a list utility for element appendage/replacement
	 */
	public static <T> ListUtils<T> use(T[] list) {
		return new ListUtils<>(Arrays.asList(list));
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

	/**
	 * Append information to each element in the list using the provided
	 * objects properties instead of (T + T) style.
	 *
	 * @param action The accepted element to append info to. In reference demands no return statement, allows custom object
	 *               modification.
	 * @return the newly modified list
	 */
	public ListUtils<T> consume(Consumer<T> action) {
		List<T> array = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			T t = list.get(i);
			if (i != list.size() - 1) {
				action.accept(t);
			}
			array.add(t);
		}
		this.list = new LinkedList<>(array);
		return this;
	}

	/**
	 * Append information to each element in the list using object appendage or replacement.
	 *
	 * @param object The object to either append or entirely replace. Demands a return statement of the same object type.
	 * @return the newly modified list
	 */
	public ListUtils<T> operate(ListOperation<T> object) {
		List<T> array = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			T t = list.get(i);
			if (i != list.size() - 1) {
				array.add(object.append(t));
			} else {
				array.add(t);
			}
		}
		this.list = new LinkedList<>(array);
		return this;
	}

	public ListUtils<T> insertFirst(T value) {
		this.list.addFirst(value);
		return this;
	}

	public ListUtils<T> insertLast(T value) {
		this.list.addLast(value);
		return this;
	}

	public List<T> addFirst(T value) {
		this.list.addFirst(value);
		return this.list;
	}

	public List<T> addLast(T value) {
		this.list.addLast(value);
		return this.list;
	}

	public <R> R join(Function<List<T>, R> fun) {
		return fun.apply(list);
	}

	public boolean stringContainsIgnoreCase(CharSequence sequence) {
		if (list.isEmpty()) return false;
		if (String.class.isAssignableFrom(list.get(0).getClass())) {
			for (T s : list) {
				String context = s.toString();
				if (StringUtils.use(context).containsIgnoreCase(sequence)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean stringContainsIgnoreCase(CharSequence... sequence) {
		if (list.isEmpty()) return false;
		if (String.class.isAssignableFrom(list.get(0).getClass())) {
			for (T s : list) {
				String context = s.toString();
				if (StringUtils.use(context).containsIgnoreCase(sequence)) {
					return true;
				}
			}
		}
		return false;
	}

	@FunctionalInterface
	public interface ListOperation<T> {
		T append(T object);
	}
}
