package com.github.sanctum.labyrinth.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RandomObject<T> {

	private List<T> collection;
	private T[] array;

	public RandomObject(Collection<T> collection) {
		this.collection = new ArrayList<>(collection);
	}

	public RandomObject(T[] array) {
		this.array = array;
	}

	public T get(Object o) {
		return get(o.hashCode());
	}

	public T get(int hashcode) {
		if (collection != null) {
			int i = (hashcode % collection.size() + collection.size()) % collection.size();
			return collection.get(i);
		} else if (array != null){
			int i = (hashcode % array.length + array.length) % array.length;
			return array[i];
		}
		return null;
	}


}
