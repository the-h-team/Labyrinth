package com.github.sanctum.labyrinth.data;

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

public abstract class AbstractClassLoader<T> extends URLClassLoader {

	private static final Field PLUGIN_CLASS_MAP;

	private final List<Class<?>> classes;
	private final Plugin plugin;
	final T mainClass;

	static {
		try {
			PLUGIN_CLASS_MAP = Class.forName("org.bukkit.plugin.java.PluginClassLoader").getDeclaredField("classes");
			PLUGIN_CLASS_MAP.setAccessible(true);
		} catch (NoSuchFieldException | ClassNotFoundException e) {
			throw new IllegalStateException("Unable to reach class map", e);
		}
	}

	protected AbstractClassLoader(Plugin plugin, File file, ClassLoader parent, Class<T> main, Object... args) throws IOException {
		super(new URL[]{file.toURI().toURL()}, parent);
		final List<Class<?>> loadedClasses = new ArrayList<>();
		this.plugin = plugin;
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
					getClassMap().put(s, resolvedClass);
					plugin.getLogger().finest(() -> "Loaded '" + s + "' successfully.");
					loadedClasses.add(resolvedClass);
				});
		this.classes = loadedClasses;
		if (main != null) {
			try {
				Class<? extends T> addonClass = loadedClasses.stream().filter(main::isAssignableFrom).findFirst().map(aClass -> (Class<? extends T>) aClass).get();
				if (args != null) {
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
					this.mainClass = addonClass.getDeclaredConstructor(constructor.getParameterTypes()).newInstance(args);
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

	public AbstractClassLoader(Plugin plugin, File file, ClassLoader parent) throws IOException {
		this(plugin, file, parent, null);
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public T getMain() {
		return mainClass;
	}

	public List<Class<?>> getClasses() {
		return classes;
	}

	public Map<String, Class<?>> getClassMap() throws IllegalStateException {
		try {
			//noinspection unchecked
			return (Map<String, Class<?>>) PLUGIN_CLASS_MAP.get(plugin.getClass().getClassLoader());
		} catch (ClassCastException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String toString() {
		return "AbstractFileLoader{" +
				"Main=" + (mainClass == null ? "N/A" : mainClass) +
				'}';
	}

}
