package com.github.sanctum.labyrinth.library;

@FunctionalInterface
public interface ListOperation<T> {
	T append(T object);
}