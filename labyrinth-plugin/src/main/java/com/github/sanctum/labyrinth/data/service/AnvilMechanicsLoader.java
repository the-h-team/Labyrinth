package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.AbstractClassLoader;
import java.io.File;
import java.io.IOException;

class AnvilMechanicsLoader extends AbstractClassLoader<ExternalDataService> {

	static final ClassLoader parent = LabyrinthProvider.getInstance().getPluginInstance().getClass().getClassLoader();

	AnvilMechanicsLoader(File file, Object... args) throws IOException {
		super(file, parent, args);
	}
}
