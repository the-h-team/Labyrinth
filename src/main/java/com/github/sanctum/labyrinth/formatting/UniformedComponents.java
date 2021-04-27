package com.github.sanctum.labyrinth.formatting;

import java.io.Serializable;
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

}
