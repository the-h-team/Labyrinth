package com.github.sanctum.labyrinth.formatting;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class UniformedComponents<T> {

	public abstract List<T> list();

	public abstract List<T> sort();

	public abstract List<T> sort(Comparable<? super T> comparable);

	public abstract Collection<T> collect();

	public abstract T[] array();

	public abstract <R> Stream<R> map(Function<? super T, ? extends R> mapper);

	public abstract Stream<T> filter(Predicate<? super T> predicate);

	public abstract T getFirst();

	public abstract T getLast();

	public abstract T get(int index);

}
