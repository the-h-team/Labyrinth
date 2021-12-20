package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.data.Configurable;
import com.github.sanctum.labyrinth.data.JsonAdapter;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthList;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.jetbrains.annotations.NotNull;

final class LabyrinthObjectInputStream extends BukkitObjectInputStream {

	private transient final LabyrinthCollection<ClassLookup> collection = new LabyrinthList<>();
	private transient ClassLoader classLoader;

	LabyrinthObjectInputStream(InputStream in) throws IOException {
		super(in);
	}

	public LabyrinthObjectInputStream add(ClassLookup lookup) {
		collection.add(lookup);
		return this;
	}

	public LabyrinthObjectInputStream setLoader(@NotNull ClassLoader loader) {
		this.classLoader = loader;
		return this;
	}

	@Override
	protected Object resolveObject(Object obj) throws IOException {
		if (obj instanceof LabyrinthJsonObjectWrapper) {
			LabyrinthJsonObjectWrapper<?> wrapper = ((LabyrinthJsonObjectWrapper<?>) obj);
			JsonAdapter<?> adapter = Configurable.getAdapter(wrapper.pointer);
			obj = adapter.read(wrapper.t);
		}

		return super.resolveObject(obj);
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		if (this.classLoader != null) {
			try {
				return Class.forName(desc.getName(), true, classLoader);
			} catch (ClassNotFoundException ignored) {
			}
		}
		if (!collection.isEmpty()) {
			Class<?> test = collection.stream().filter(lookup -> lookup.accept(desc.getName()) != null).findFirst().map(lookup -> lookup.accept(desc.getName())).orElse(null);
			if (test != null) return test;
		}
		return super.resolveClass(desc);
	}
}
