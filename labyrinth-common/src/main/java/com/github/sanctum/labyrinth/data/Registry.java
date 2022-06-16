package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Note;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import org.bukkit.plugin.Plugin;

/**
 * This class is used explicitly for searching for specific class types through an optionally given package
 * of a given plugin or a provided {@link File} and then running custom operations with them. Ex. Registering {@link org.bukkit.event.Listener} classes
 * automagically.
 *
 * @param <T> the type of class to search for and operate with
 */
public class Registry<T> {

	private final Class<T> CLASS;
	private Predicate<? super String> FILTER;
	private Object handle = null;
	private String PACKAGE;

	public Registry(Class<T> cl) {
		this.CLASS = cl;
	}

	/**
	 * Select a class type to provide in a search of relevance.
	 *
	 * @param cl  the class to search for inheritance of
	 * @param <T> the type of search this will inquire
	 * @return this Registry instance
	 */
	public static <T> Registry<T> use(Class<T> cl) {
		return new Registry<>(cl);
	}

	/**
	 * Source an object for the main jar file directory.
	 *
	 * @param loader the object to source-inject the classes from
	 * @return this Registry instance
	 */
	public Registry<T> source(Object loader) {
		this.handle = loader;
		return this;
	}

	/**
	 * Optionally search in a targeted package name.
	 *
	 * @param packageName the name of the package to search
	 * @return this Registry instance
	 * @deprecated Use {@link Registry#filter(String)} instead!!
	 */
	@Deprecated
	public Registry<T> pick(String packageName) {
		this.PACKAGE = packageName;
		return this;
	}

	public Registry<T> filter(String packageName) {
		this.PACKAGE = packageName;
		return this;
	}

	public Registry<T> filter(Predicate<? super String> predicate) {
		this.FILTER = predicate;
		return this;
	}

	/**
	 * Specify actions on each element instantiated from query.
	 *
	 * @return the leftover data from the registry data operation
	 */
	public RegistryData<T> operate() {
		Set<Class<T>> classes = Sets.newHashSet();
		JarFile jarFile = null;
		if (this.handle != null) {
			try {
				jarFile = new JarFile(URLDecoder.decode(this.handle.getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), String.valueOf(StandardCharsets.UTF_8)));
			} catch (IOException e) {
				e.printStackTrace(); // TODO: Decide whether to return/rethrow at this point so as to avoid NPE on line 89
			}
		}

		if (jarFile == null) throw new IllegalStateException("Invalid jar file");

		Stream<JarEntry> entries = jarFile.stream();

		if (this.FILTER != null) {
			entries.forEach(entry -> {
				String className = entry.getName().replace("/", ".");
				final String substring = className.substring(0, Math.max(className.length() - 6, 0));
				if (this.FILTER.test(className)) {
					Class<?> clazz = null;
					try {
						clazz = Class.forName(substring);
					} catch (ClassNotFoundException ignored) {
					}
					if (clazz != null) {
						if (CLASS.isAssignableFrom(clazz)) {
							classes.add((Class<T>) clazz);
						}
					}
				}
			});
		} else {
			entries.forEach(entry -> {

				String className = entry.getName().replace("/", ".");
				final String substring = className.substring(0, Math.max(className.length() - 6, 0));
				if (this.PACKAGE != null) {
					if (className.startsWith(PACKAGE) && className.endsWith(".class")) {
						Class<?> clazz = null;
						try {
							clazz = Class.forName(substring);
						} catch (ClassNotFoundException ignored) {
						}
						if (clazz != null) {
							if (CLASS.isAssignableFrom(clazz)) {
								classes.add((Class<T>) clazz);
							}
						}
					}
				} else {

					if (className.endsWith(".class")) {
						Class<?> clazz = null;
						try {
							clazz = Class.forName(substring);
						} catch (ClassNotFoundException ignored) {
						}
						if (clazz != null) {
							if (CLASS.isAssignableFrom(clazz)) {
								classes.add((Class<T>) clazz);
							}
						}
					}
				}
			});
		}
		List<T> additions = new LinkedList<>();
		for (Class<T> aClass : classes) {
			try {
				T a = aClass.getDeclaredConstructor().newInstance();
				additions.add(a);
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
				break;
			}
		}
		return new RegistryData<>(additions, handle, PACKAGE);
	}

	/**
	 * Specify actions on each element instantiated from query.
	 *
	 * @param operation an operation to perform when each element is instantiated
	 * @return the leftover data from the registry data operation
	 */
	public RegistryData<T> operate(Consumer<T> operation) {
		Set<Class<T>> classes = Sets.newHashSet();
		JarFile jarFile = null;
		if (this.handle != null) {
			try {
				jarFile = new JarFile(URLDecoder.decode(this.handle.getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), String.valueOf(StandardCharsets.UTF_8)));
			} catch (IOException e) {
				throw new IllegalStateException("Directory not valid", e);
			}
		}
		if (jarFile == null) throw new IllegalStateException("Invalid jar file");

		Stream<JarEntry> entries = jarFile.stream();

		if (this.FILTER != null) {
			entries.forEach(entry -> {
				String className = entry.getName().replace("/", ".");
				final String substring = className.substring(0, Math.max(className.length() - 6, 0));
				if (this.FILTER.test(className)) {
					Class<?> clazz = null;
					try {
						clazz = Class.forName(substring);
					} catch (ClassNotFoundException ignored) {
					}
					if (clazz != null) {
						if (CLASS.isAssignableFrom(clazz)) {
							classes.add((Class<T>) clazz);
						}
					}
				}
			});
		} else {
			entries.forEach(entry -> {

				String className = entry.getName().replace("/", ".");
				final String substring = className.substring(0, Math.max(className.length() - 6, 0));
				if (this.PACKAGE != null) {
					if (className.startsWith(PACKAGE) && className.endsWith(".class")) {
						Class<?> clazz = null;
						try {
							clazz = Class.forName(substring);
						} catch (ClassNotFoundException ignored) {
						}
						if (clazz != null) {
							if (CLASS.isAssignableFrom(clazz)) {
								classes.add((Class<T>) clazz);
							}
						}
					}
				} else {

					if (className.endsWith(".class")) {
						Class<?> clazz = null;
						try {
							clazz = Class.forName(substring);
						} catch (ClassNotFoundException ignored) {
						}
						if (clazz != null) {
							if (CLASS.isAssignableFrom(clazz)) {
								classes.add((Class<T>) clazz);
							}
						}
					}
				}
			});
		}
		List<T> additions = new LinkedList<>();
		for (Class<T> aClass : classes) {
			try {
				T a = aClass.getDeclaredConstructor().newInstance();
				operation.accept(a);
				additions.add(a);
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
				break;
			}
		}
		return new RegistryData<>(additions, handle, PACKAGE);
	}

	/**
	 * Using {@link com.github.sanctum.labyrinth.data.AddonLoader} internally delegate information to instantiate loaded jar classes.
	 * This class will attempt to locate and resolve all target class types from the specified file directory.
	 *
	 * @param <T> The type of class to load.
	 */
	public static class Loader<T> {

		private final Class<T> type;
		private AddonLoader loader;
		private Plugin plugin;
		private String directory;

		public Loader(Class<T> type) {
			this.type = type;
		}

		public Loader<T> source(Plugin plugin) {
			this.plugin = plugin;
			return this;
		}

		public Loader<T> from(String directory) {
			this.directory = directory;
			return this;
		}

		@Note("New! Unload all classes relative to this loader.")
		public boolean unravel() {
			if (this.loader != null) {
				try {
					return loader.unload(FileList.search(this.plugin).get("dummy", this.directory).getRoot().getParent().getParentFile().getPath());
				} catch (ClassNotFoundException e) {
					LabyrinthProvider.getInstance().getLogger().severe("Unable to unload classes for path '" + directory + "'");
				}
			}
			return false;
		}

		public RegistryData<T> confine() {

			File file = FileList.search(this.plugin).get("dummy", this.directory).getRoot().getParent().getParentFile();

			AddonLoader l = loader != null ? loader : (loader = AddonLoader.newInstance(this.plugin));

			List<Class<?>> classes = l.loadFolder(file);

			List<T> data = new LinkedList<>();

			for (Class<?> cl : classes) {
				if (this.type.isAssignableFrom(cl)) {
					try {
						T e = (T) cl.getDeclaredConstructor().newInstance();
						data.add(e);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			return new RegistryData<>(data, this.plugin, this.directory);
		}

		public RegistryData<T> confine(Consumer<T> action) {

			File file = FileList.search(this.plugin).get("dummy", this.directory).getRoot().getParent().getParentFile();

			AddonLoader l = loader != null ? loader : (loader = AddonLoader.newInstance(this.plugin));

			List<Class<?>> classes = l.loadFolder(file);

			List<T> data = new LinkedList<>();

			for (Class<?> cl : classes) {
				if (this.type.isAssignableFrom(cl)) {
					try {
						T e = (T) cl.getDeclaredConstructor().newInstance();
						action.accept(e);
						data.add(e);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			return new RegistryData<>(data, this.plugin, this.directory);
		}

		public RegistryData<T> construct(Object... o) {
			File file = FileList.search(this.plugin).get("dummy", this.directory).getRoot().getParent().getParentFile();
			AddonLoader l = loader != null ? loader : (loader = AddonLoader.newInstance(this.plugin));

			List<Class<?>> classes = l.loadFolder(file);
			List<T> data = new LinkedList<>();

			Constructor<T> constructor = null;
			for (Constructor<?> con : this.type.getConstructors()) {
				if (o.length == con.getParameters().length) {
					int success = 0;
					for (int i = 0; i < o.length; i++) {
						Class<?> objectClass = o[i].getClass();
						Class<?> typeClass = con.getParameters()[i].getType();
						if (objectClass.isAssignableFrom(typeClass)) {
							success++;
						}
						if (success == o.length) {
							constructor = (Constructor<T>) con;
							break;
						}
					}
				}
			}
			for (Class<?> cl : classes) {
				if (this.type.isAssignableFrom(cl)) {
					try {
						T e;
						if (constructor != null) {
							e = (T) cl.getDeclaredConstructor(constructor.getParameterTypes()).newInstance(o);
						} else {
							e = (T) cl.getDeclaredConstructor().newInstance();
						}
						data.add(e);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			return new RegistryData<>(data, this.plugin, this.directory);
		}

		public RegistryData<T> construct(Consumer<T> action, Object... o) {
			File file = FileList.search(this.plugin).get("dummy", this.directory).getRoot().getParent().getParentFile();
			AddonLoader l = loader != null ? loader : (loader = AddonLoader.newInstance(this.plugin));

			List<Class<?>> classes = l.loadFolder(file);
			List<T> data = new LinkedList<>();

			Constructor<T> constructor = null;
			for (Constructor<?> con : this.type.getConstructors()) {
				if (o.length == con.getParameters().length) {
					int success = 0;
					for (int i = 0; i < o.length; i++) {
						Class<?> objectClass = o[i].getClass();
						Class<?> typeClass = con.getParameters()[i].getType();
						if (objectClass.isAssignableFrom(typeClass)) {
							success++;
						}
						if (success == o.length) {
							constructor = (Constructor<T>) con;
							break;
						}
					}
				}
			}
			for (Class<?> cl : classes) {
				if (this.type.isAssignableFrom(cl)) {
					try {
						T e;
						if (constructor != null) {
							e = (T) cl.getDeclaredConstructor(constructor.getParameterTypes()).newInstance(o);
						} else {
							e = (T) cl.getDeclaredConstructor().newInstance();
						}
						action.accept(e);
						data.add(e);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			return new RegistryData<>(data, this.plugin, this.directory);
		}


	}


}
