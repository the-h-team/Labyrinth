package com.github.sanctum.labyrinth.data;

import com.google.common.collect.Sets;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
	private Plugin PLUGIN = null;
	private String PACKAGE;

	public Registry(Class<T> cl) {
		this.CLASS = cl;
	}

	/**
	 * Select a class type to provide in a search of relevance.
	 *
	 * @param cl the class to search for inheritance of
	 * @param <T> the type of search this will inquire
	 * @return this Registry instance
	 */
	public static <T> Registry<T> use(Class<T> cl) {
		return new Registry<>(cl);
	}

	/**
	 * Source a plugin for the main jar file directory.
	 *
	 * @param plugin the plugin to source the classes from
	 * @return this Registry instance
	 */
	public Registry<T> source(Plugin plugin) {
		this.PLUGIN = plugin;
		return this;
	}

	/**
	 * Optionally search in a targeted package name.
	 *
	 * @param packageName the name of the package to search
	 * @return this Registry instance
	 */
	public Registry<T> pick(String packageName) {
		this.PACKAGE = packageName;
		return this;
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
		if (this.PLUGIN != null) {
			try {
				jarFile = new JarFile(URLDecoder.decode(this.PLUGIN.getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), String.valueOf(StandardCharsets.UTF_8)));
			} catch (IOException e) {
				e.printStackTrace(); // TODO: Decide whether to return/rethrow at this point so as to avoid NPE on line 89
			}
		}

		List<JarEntry> entries = Collections.list(jarFile.entries());

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
		return new RegistryData<>(additions, PLUGIN, PACKAGE);
	}

	public static class Loader<T> {

		private final Class<T> type;

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

		public RegistryData<T> operate(Consumer<T> action) {

			File file = FileList.search(this.plugin).find("Test", this.directory).getFile().getParentFile();

			List<Class<?>> classes = AddonLoader.forPlugin(JavaPlugin.getProvidingPlugin(this.plugin.getClass()))
					.loadFolder(file);

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

	}


}
