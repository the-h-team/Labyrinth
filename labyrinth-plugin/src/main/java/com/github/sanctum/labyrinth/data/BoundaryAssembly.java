package com.github.sanctum.labyrinth.data;

@FunctionalInterface
public interface BoundaryAssembly {

	void accept(BoundaryAction action);

}
