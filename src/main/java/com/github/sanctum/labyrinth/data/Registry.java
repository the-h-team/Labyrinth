package com.github.sanctum.labyrinth.data;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.plugin.Plugin;

/**
 * This class is used explicitly for searching for specific class types through an optionally given package
 * of a given plugin or a provided {@link java.io.File} and then running custom operations with them. Ex. Registering {@link org.bukkit.event.Listener} classes
 * automagically.
 *
 * @param <T> The type of class to search for and operate with.
 */
public class Registry<T> {

	private final Class<T> CLASS;
	private Plugin PLUGIN = null;
	private JarFile FILE;
	private String PACKAGE;

	public Registry(Class<T> cl) {
		this.CLASS = cl;
	}

	/**
	 * Select a class type to provide in a search of relevance.
	 *
	 * @param cl  The class to search for inheritance of.
	 * @param <T> The type of search this will inquire.
	 * @return The same registry instance.
	 */
	public static <T> Registry<T> use(Class<T> cl) {
		return new Registry<>(cl);
	}

	/**
	 * Source a plugin for the main jar file directory.
	 *
	 * @param plugin The plugin to source the classes from.
	 * @return The same registry instance.
	 * @throws UnsupportedEncodingException If character encoding needs to be consulted, but named character encoding is not supported
	 */
	public Registry<T> source(Plugin plugin) throws IOException {
		this.PLUGIN = plugin;
		this.FILE = new JarFile(URLDecoder.decode(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8"));
		return this;
	}

	/**
	 * Source a file for the main jar file directory.
	 *
	 * @param file The file to source the classes from.
	 * @return The same registry instance.
	 * @throws IOException If an IO error occurred.
	 */
	public Registry<T> source(java.io.File file) throws IOException {
		this.FILE = new JarFile(file);
		return this;
	}

	/**
	 * Optionally search in a targeted package name.
	 *
	 * @param packageName The name of the package to search.
	 * @return The same registry instance.
	 */
	public Registry<T> pick(String packageName) {
		this.PACKAGE = packageName;
		return this;
	}

	/**
	 * Specify actions on each element instantiated from query.
	 *
	 * @param operation The operation to take effect when each element is instantiated.
	 * @return The leftover data from the registry data operation.
	 */
	public RegistryData<T> operate(Consumer<T> operation) {
		Set<Class<T>> classes = Sets.newHashSet();
		JarFile jarFile = this.FILE;
		for (JarEntry jarEntry : Collections.list(jarFile.entries())) {
			String className = jarEntry.getName().replace("/", ".");

			final String substring = className.substring(0, className.length() - 6);
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
		return new RegistryData<>(additions, PLUGIN, PACKAGE);
	}

	/**
	 * This class is used explicitly for searching for inheritable class types of from a specified file location
	 * loading them into cache using your plugins class loader and then running operations with the found elements.
	 *
	 * @param <T> The type of class to use for instantiation.
	 */
	public static class File<T> {

		private final Class<T> CLASS;
		private Plugin PLUGIN;
		private ClassLoader LOADER;
		private String PACKAGE;
		private String DIRECTORY;

		public File(Class<T> cl) {
			this.CLASS = cl;
		}

		/**
		 * Select a plugin to use as our home location for file browsing.
		 *
		 * @param plugin The plugin sourcing the browsing location.
		 * @return The same registry file being loaded.
		 */
		public File<T> use(Plugin plugin) {
			this.PLUGIN = plugin;
			return this;
		}

		/**
		 * Provide a class loader instance to inject class files into.
		 *
		 * @param loader The class loader to use.
		 * @return The same registry file being loaded.
		 */
		public File<T> provide(ClassLoader loader) {
			this.LOADER = loader;
			return this;
		}

		/**
		 * Optionally target a specific package location.
		 *
		 * @param packageLoc The optional location to target search.
		 * @return The same registry file being loaded.
		 */
		public File<T> pick(String packageLoc) {
			this.PACKAGE = packageLoc;
			return this;
		}

		/**
		 * Select a directory for the file search.
		 *
		 * @param directory The/Directory/To/Do/The/Search/In
		 * @return The same registry file being loaded.
		 */
		public File<T> from(String directory) {
			this.DIRECTORY = directory;
			return this;
		}

		/**
		 * Operate on each instantiated element loaded into cache under the specified circumstances.
		 *
		 * @param operation The operation to run per element instantiated.
		 * @return A new registry data instance with the remaining elements from the search.
		 * @throws IOException               If an IO error occurred.
		 * @throws NoSuchMethodException     if a matching method is not found.
		 * @throws InvocationTargetException if the underlying method throws an exception
		 * @throws IllegalAccessException    if this Method object is enforcing Java language access control and the underlying method is inaccessible.
		 */
		public RegistryData<T> operate(Consumer<T> operation) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
			FileManager check = FileList.search(this.PLUGIN).find("Test", this.DIRECTORY);
			java.io.File parent = check.getFile().getParentFile();
			if (!parent.exists()) {
				//noinspection ResultOfMethodCallIgnored
				parent.mkdir();
			}
			List<T> list = new LinkedList<>();
			for (java.io.File f : parent.listFiles()) {
				URLClassLoader classLoader = (URLClassLoader) this.LOADER;
				Class<?> urlClassLoaderClass = URLClassLoader.class;
				Method method = urlClassLoaderClass.getDeclaredMethod("addURL", URL.class);
				method.setAccessible(true);
				method.invoke(classLoader, f.toURI().toURL());
				if (f.isFile()) {
					if (this.PACKAGE != null) {
						list.addAll(new Registry<T>(this.CLASS)
								.source(f)
								.pick(this.PACKAGE)
								.operate(operation)
								.getData());
					} else {
						list.addAll(new Registry<T>(this.CLASS)
								.source(f)
								.operate(operation)
								.getData());
					}
				}
			}
			return new RegistryData<>(list, this.PLUGIN, this.DIRECTORY);
		}

	}


}
