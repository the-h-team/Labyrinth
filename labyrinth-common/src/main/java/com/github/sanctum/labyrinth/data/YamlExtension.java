package com.github.sanctum.labyrinth.data;

import com.github.sanctum.panther.file.Configurable;

public final class YamlExtension implements Configurable.Extension {

	public static final Configurable.Extension INSTANCE = new YamlExtension();

	@Override
	public String get() {
		return ".yml";
	}

	@Override
	public Class<? extends Configurable> getImplementation() {
		return YamlConfiguration.class;
	}
}
