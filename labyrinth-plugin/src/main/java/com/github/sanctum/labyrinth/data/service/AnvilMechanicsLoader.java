package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.data.LabyrinthClassLoader;
import java.io.File;
import java.io.IOException;

public final class AnvilMechanicsLoader extends LabyrinthClassLoader<ExternalDataService> {

	AnvilMechanicsLoader(File file, ClassLoader parent, Object... args) throws IOException {
		super(file, parent, args);
	}

}
