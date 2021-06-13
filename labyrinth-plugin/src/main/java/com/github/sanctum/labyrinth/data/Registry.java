package com.github.sanctum.labyrinth.data;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
	private Predicate<? super String> FILTER;
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
		this.FILE = new JarFile(URLDecoder.decode(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), StandardCharsets.UTF_8));
		return this;
	}

	/**
	 * Filter your search for locations targeted.
	 * <p>
	 * Alternative to {@link Registry#pick(String)}
	 *
	 * @param predicate The information to rely on before targeting a search.
	 * @return The same registry instance.
	 */
	public Registry<T> filter(Predicate<? super String> predicate) {
		this.FILTER = predicate;
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

		if (this.PLUGIN != null) {
			try {
				jarFile = new JarFile(URLDecoder.decode(this.PLUGIN.getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), StandardCharsets.UTF_8));
			} catch (IOException e) {
				e.printStackTrace();
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

}
