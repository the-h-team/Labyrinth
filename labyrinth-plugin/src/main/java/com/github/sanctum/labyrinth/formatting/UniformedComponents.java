package com.github.sanctum.labyrinth.formatting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class UniformedComponents<T> implements Serializable {

	private static final long serialVersionUID = 380726810757536184L;

	public abstract List<T> list();

	public abstract List<T> sort();

	public abstract List<T> sort(Comparator<? super T> comparable);

	public abstract Collection<T> collect();

	public abstract T[] array();

	public abstract <R> Stream<R> map(Function<? super T, ? extends R> mapper);

	public abstract Stream<T> filter(Predicate<? super T> predicate);

	public abstract T getFirst();

	public abstract T getLast();

	public abstract T get(int index);

	public static <T> UniformedComponents<T> accept(List<T> list) {
		return new UniformedComponents<T>() {
			private static final long serialVersionUID = -5158318648096932311L;

			@Override
			public List<T> list() {
				return new ArrayList<>(list);
			}

			@Override
			public List<T> sort() {
				return list();
			}

			@Override
			public List<T> sort(Comparator<? super T> comparable) {
				List<T> list = list();
				list.sort(comparable);
				return list;
			}

			@Override
			public Collection<T> collect() {
				return list();
			}

			@Override
			public T[] array() {
				return null;
			}

			@Override
			public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
				return list().stream().map(mapper);
			}

			@Override
			public Stream<T> filter(Predicate<? super T> predicate) {
				return list().stream().filter(predicate);
			}

			@Override
			public T getFirst() {
				return list().get(0);
			}

			@Override
			public T getLast() {
				return list().get(Math.max(list().size() - 1, 0));
			}

			@Override
			public T get(int index) {
				return list().get(index);
			}
		};
	}

}
