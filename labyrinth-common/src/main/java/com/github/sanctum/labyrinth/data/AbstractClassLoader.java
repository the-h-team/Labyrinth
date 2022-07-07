package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Experimental;
import com.github.sanctum.labyrinth.data.service.DummyAdapter;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.bukkit.plugin.Plugin;

/**
 * A class dedicated to allowing developers to load external jars into memory.
 *
 * @param <T> The optional main class this loader needs to locate and instantiate.
 */
public abstract class AbstractClassLoader<T> extends URLClassLoader {

	protected final Field PLUGIN_CLASS_MAP;
	protected final List<Class<?>> classes;
	protected final ClassLoader pluginClassLoader;
	protected final T mainClass;

	protected AbstractClassLoader(File file, ClassLoader parent, Object... args) throws IOException {
		super(new URL[]{file.toURI().toURL()}, parent);
		Class<T> main = (Class<T>) new TypeToken<T>(getClass()){}.getRawType();
		try {
			PLUGIN_CLASS_MAP = Class.forName("org.bukkit.plugin.java.PluginClassLoader").getDeclaredField("classes");
			PLUGIN_CLASS_MAP.setAccessible(true);
		} catch (NoSuchFieldException | ClassNotFoundException e) {
			throw new IllegalStateException("Unable to reach plugin class map", e);
		}
		final List<Class<?>> loadedClasses = new ArrayList<>();
		final Plugin plugin = LabyrinthProvider.getInstance().getPluginInstance();
		this.pluginClassLoader = plugin.getClass().getClassLoader();
		if (!file.isFile()) throw new IllegalArgumentException("The provided file is not a jar file!");
		new JarFile(file).stream()
				.map(ZipEntry::getName)
				.filter(entryName -> entryName.contains(".class") && !entryName.contains("$"))
				.map(classPath -> classPath.replace('/', '.'))
				.map(className -> className.substring(0, className.length() - 6))
				.forEach(s -> {
					final Class<?> resolvedClass;
					try {
						resolvedClass = loadClass(s, true);
					} catch (ClassNotFoundException e) {
						plugin.getLogger().warning(() -> "Unable to inject '" + s + "'");
						plugin.getLogger().warning(e::getMessage);
						return;
					}
					getPluginClassMap().put(s, resolvedClass);
					plugin.getLogger().finest(() -> "Loaded '" + s + "' successfully.");
					loadedClasses.add(resolvedClass);
				});
		this.classes = loadedClasses;
		if (main != null) {
			try {
				Class<? extends T> addonClass = loadedClasses.stream().filter(main::isAssignableFrom).findFirst().map(aClass -> (Class<? extends T>) aClass).get();
				if (args != null && args.length > 0) {
					Constructor<T> constructor = null;
					for (Constructor<?> con : main.getConstructors()) {
						if (args.length == con.getParameters().length) {
							int success = 0;
							for (int i = 0; i < args.length; i++) {
								Class<?> objectClass = args[i].getClass();
								Class<?> typeClass = con.getParameters()[i].getType();
								if (objectClass.isAssignableFrom(typeClass)) {
									success++;
								}
								if (success == args.length) {
									constructor = (Constructor<T>) con;
									break;
								}
							}
						}
					}
					this.mainClass = constructor != null ? addonClass.getDeclaredConstructor(constructor.getParameterTypes()).newInstance(args) : addonClass.getDeclaredConstructor().newInstance();
				} else {
					this.mainClass = addonClass.getDeclaredConstructor().newInstance();
				}
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
				throw new IllegalStateException("No public constructor", ex);
			} catch (InstantiationException ex) {
				throw new IllegalStateException("Unusual constructor args detected.", ex);
			}
		} else this.mainClass = null;
	}

	/**
	 * Get the main class for this class loader.
	 *
	 * @return the main class for this class loader if one exists.
	 */
	public T getMainClass() {
		return mainClass;
	}

	/**
	 * Get a list of all classes loaded by this class loader.
	 *
	 * @return all classes loaded by this class loader.
	 */
	public List<Class<?>> getClasses() {
		return ImmutableList.copyOf(classes);
	}

	/**
	 * Unload a class from memory. If the provided class is not found an exception will occur, if the provided string results in a path
	 * this method will switch in an attempt at locating and removing the relative class files it belongs to.
	 *
	 * @param name The name of the class file or path.
	 * @return true if the class(es) got removed from memory.
	 * @throws ClassNotFoundException if the attempted class resolve fails and the included text doesn't result in a valid directory.
	 */
	@Experimental
	public boolean unload(String name) throws ClassNotFoundException {
		Map<String, Class<?>> classes = getPluginClassMap();
		if (classes.containsKey(name)) {
			classes.remove(name);
			return true;
		} else throw new ClassNotFoundException("Class " + name + " not found, cannot unload.");
	}

	/**
	 * Simply unload a loaded class from this addon loader.
	 *
	 * @param clazz The class to unload.
	 * @throws WrongLoaderUsedException when the class attempting removal belongs to a different loader instance.
	 * @return true if the class was able to unload.
	 */
	@Experimental
	public boolean unload(Class<?> clazz) throws WrongLoaderUsedException {
		Map<String, Class<?>> classes = getPluginClassMap();
		String name = clazz.getName().replace("/", ".").substring(0, clazz.getName().length() - 6);
		classes.remove(name);
		if (!this.classes.contains(clazz)) throw new WrongLoaderUsedException("Class " + clazz.getName() + " does not belong to this loader!");
		return this.classes.remove(clazz);
	}

	public final Map<String, Class<?>> getPluginClassMap() throws IllegalStateException {
		try {
			//noinspection unchecked
			return (Map<String, Class<?>>) PLUGIN_CLASS_MAP.get(this.pluginClassLoader);
		} catch (ClassCastException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String toString() {
		return "AbstractFileLoader{" +
				"Main=" + mainClass +
				'}';
	}

}
