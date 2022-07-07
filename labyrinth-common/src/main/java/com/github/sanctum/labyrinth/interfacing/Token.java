package com.github.sanctum.labyrinth.interfacing;

public interface Token<T> {

	boolean isValid();

	T get();

}
