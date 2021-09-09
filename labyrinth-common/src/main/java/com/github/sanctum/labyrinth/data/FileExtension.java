package com.github.sanctum.labyrinth.data;

public interface FileExtension {

	String getExtension();

	Class<? extends Configurable> getImplementation();

}
