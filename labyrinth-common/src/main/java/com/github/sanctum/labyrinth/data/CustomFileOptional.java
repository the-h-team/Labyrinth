package com.github.sanctum.labyrinth.data;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CustomFileOptional {

	private final FileList list;
	private final String name;
	private final String directory;
	private final FileExtension extension;

	protected CustomFileOptional(FileList list, String name, String description, FileExtension extension) {
		this.list = list;
		this.name = name;
		this.directory = description;
		this.extension = extension;
	}

	public boolean isPresent() {
		return list.exists(name, directory, extension);
	}

	public @Nullable FileManager get() {
		if (isPresent()) {
			return list.get(name, directory, extension);
		}
		return null;
	}

	public @NotNull FileManager orElse(Configurable configurable) {
		if (isPresent()) {
			return list.get(name, directory, extension);
		}
		list.inject(configurable);
		return list.get(name, directory, extension);
	}

	public @NotNull FileManager orElseGet(Supplier<Configurable> supplier) {
		return orElse(supplier.get());
	}

	public void ifPresent(Consumer<FileManager> consumer) {
		if (isPresent()) {
			consumer.accept(list.get(name, directory, extension));
		}
	}


}
