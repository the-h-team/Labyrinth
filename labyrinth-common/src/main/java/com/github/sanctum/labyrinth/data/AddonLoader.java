package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Removal;
import com.github.sanctum.labyrinth.data.container.LabyrinthEntryMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Load addon classes in Java 8-16+.
 *
 * @author ms5984
 */
public class AddonLoader {
    private static final Field PLUGIN_CLASS_MAP;

    private final LabyrinthMap<String, List<String>> classMap = new LabyrinthEntryMap<>();
    private final Plugin javaPlugin;
    private AddonClassLoader loader;

    static {
        try {
            PLUGIN_CLASS_MAP = Class.forName("org.bukkit.plugin.java.PluginClassLoader").getDeclaredField("classes");
            PLUGIN_CLASS_MAP.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to reach plugin class map", e);
        }
    }

    private AddonLoader(Plugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    /**
     * Get an AddonLoader instance for your plugin.
     *
     * @param yourPlugin an instance of your main plugin class
     * @deprecated Use {@link AddonLoader#newInstance(Plugin)} instead.
     * @return an addon loader for your plugin
     */
    @Deprecated
    @Removal(inVersion = "1.7.9")
    public static AddonLoader forPlugin(Plugin yourPlugin) {
        return new AddonLoader(yourPlugin);
    }

    /**
     * Get an AddonLoader instance for your plugin.
     *
     * @param yourPlugin an instance of your main plugin class
     * @return an addon loader for your plugin
     */
    public static AddonLoader newInstance(Plugin yourPlugin) {
        return new AddonLoader(yourPlugin);
    }

    /**
     * Load a single jar file for your plugin.
     *
     * @param addonJar addon jar as a File object
     * @throws IllegalArgumentException if addonJar is not a .jar file
     * @throws IllegalStateException if unable to read from the JarFile
     * @return list of loaded classes ready for processing
     */
    public List<Class<?>> loadFile(File addonJar) throws IllegalArgumentException, IllegalStateException {
        if (addonJar.isDirectory()) throw new IllegalArgumentException("File must not be a directory");
        if (!addonJar.getName().endsWith(".jar")) throw new IllegalArgumentException("File must be a .jar");
        final JarFile jarFile;
        try {
            jarFile = new JarFile(addonJar);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        final List<Class<?>> classList = injectJars(addonJar.getPath(), ImmutableMap.of(getValidJarURL(addonJar).orElseThrow(IllegalStateException::new), jarFile));
        javaPlugin.getLogger().info(() -> "Loaded addon file " + addonJar.getName() + " successfully.");
        return classList;
    }

    /**
     * Load all jars from a directory for your plugin.
     *
     * @param folder addon folder as a File object
     * @throws IllegalArgumentException if folder is not a directory
     * @return list of all loaded classes ready for processing
     */
    public List<Class<?>> loadFolder(File folder) throws IllegalArgumentException {
        if (!folder.isDirectory()) throw new IllegalArgumentException("File is not a folder!");
        javaPlugin.getLogger().info(() -> "Processing folder '" + folder.getName() + "'");
        final ImmutableMap.Builder<URL, JarFile> builder = ImmutableMap.builder();
        for (File file : folder.listFiles()) {
            getValidJarURL(file).ifPresent(url -> {
                final JarFile jarFile;
                try {
                    jarFile = new JarFile(file);
                } catch (IOException e) {
                    javaPlugin.getLogger().warning(e::getMessage);
                    return;
                }
                builder.put(url, jarFile);
            });
        }
        final List<Class<?>> classes = injectJars(folder.getPath(), builder.build());
        javaPlugin.getLogger().info(() -> "Loaded addon files from " + folder.getPath() + " successfully.");
        return classes;
    }

    /**
     * Unload a class from memory. If the provided class is not found an exception will occur, if the provided string results in a path
     * this method will switch in an attempt at locating and removing the relative class files it belongs to.
     *
     * @param name The name of the class file or path.
     * @return true if the class(es) got removed from memory.
     * @throws ClassNotFoundException if the attempted class resolve fails and the included text doesn't result in a valid directory.
     */
    public boolean unload(String name) throws ClassNotFoundException {
        if (loader != null) {
            Map<String, Class<?>> classes = getClassMap(javaPlugin);
            if (classMap.containsKey(name)) {
                for (String st : classMap.get(name)) {
                    classes.remove(st);
                }
                classMap.remove(name);
                return true;
            }
            List<String> list = locateList(name);
            if (classes.containsKey(name)) {
                classes.remove(name);
                if (list != null) list.remove(name);
                return true;
            } else throw new ClassNotFoundException("Class " + name + " not found, cannot unload.");
        }
        return false;
    }

    /**
     * Simply unload a loaded class from this addon loader.
     *
     * @param clazz The class to unload.
     * @throws WrongLoaderUsedException when the class attempting removal belongs to a different loader instance.
     * @return true if the class was able to unload.
     */
    public boolean unload(Class<?> clazz) throws WrongLoaderUsedException {
        if (loader != null) {
            Map<String, Class<?>> classes = getClassMap(javaPlugin);
            String name = clazz.getName().replace("/", ".").substring(0, clazz.getName().length() - 6);
            List<String> list = locateList(name);
            if (list != null) list.remove(name);
            classes.remove(name);
            return loader.removeClass(clazz);
        }
        return false;
    }

    private List<String> locateList(String name) {
        for (List<String> strings : classMap.values()) {
            for (String s : strings) {
                if (s.equalsIgnoreCase(name)) return strings;
            }
        }
        return null;
    }

    private Optional<URL> getValidJarURL(File file) {
        if (file.getName().endsWith(".jar")) {
            final URL url;
            try {
                url = file.toURI().toURL();
                return Optional.of(url);
            } catch (MalformedURLException e) {
                javaPlugin.getLogger().warning(e::getMessage);
                javaPlugin.getLogger().warning("This is very unusual, contact Labyrinth developers.");
            }
        }
        return Optional.empty();
    }

    private List<Class<?>> injectJars(String path, Map<URL, JarFile> jars) {
        if (jars.isEmpty()) return ImmutableList.of();
        final AddonClassLoader addonClassLoader = new AddonClassLoader(jars.keySet().toArray(new URL[0]));
        loader = addonClassLoader;
        jars.values().forEach(jarFile -> jarFile.stream()
                .map(ZipEntry::getName)
                .filter(entryName -> entryName.contains(".class") && !entryName.contains("$"))
                .map(classPath -> classPath.replace('/', '.'))
                .map(className -> className.substring(0, className.length() - 6))
                .forEach(s -> addonClassLoader.injectClass(path, s)));
        return addonClassLoader.loadedClasses;
    }

    Map<String, Class<?>> getClassMap(Plugin javaPlugin) throws IllegalStateException {
        try {
            //noinspection unchecked
            return (Map<String, Class<?>>) PLUGIN_CLASS_MAP.get(javaPlugin.getClass().getClassLoader());
        } catch (ClassCastException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    final class AddonClassLoader extends URLClassLoader {
        private final List<Class<?>> loadedClasses = new ArrayList<>();

        private AddonClassLoader(URL[] urls) {
            super(urls, javaPlugin.getClass().getClassLoader());
        }

        Class<?> resolveClass(String name) throws ClassNotFoundException {
            return loadClass(name, true);
        }

        boolean removeClass(Class<?> clazz) throws WrongLoaderUsedException {
            if (loadedClasses.contains(clazz)) {
                loadedClasses.remove(clazz);
                return true;
            } else throw new WrongLoaderUsedException("Class " + clazz.getName() + " does not belong to this loader!");
        }

        void injectClass(String path, String className) {
            final Class<?> resolvedClass;
            try {
                resolvedClass = resolveClass(className);
            } catch (ClassNotFoundException e) {
                javaPlugin.getLogger().warning(() -> "Unable to inject '" + className + "'");
                javaPlugin.getLogger().warning(e::getMessage);
                return;
            }
            getClassMap(javaPlugin).put(className, resolvedClass);
            List<String> classes = classMap.get(path);
            if (classes != null) {
                classes.add(className);
            } else {
                classes = new ArrayList<>();
                classes.add(className);
                classMap.put(path, classes);
            }
            javaPlugin.getLogger().finest(() -> "Loaded '" + className + "' successfully.");
            loadedClasses.add(resolvedClass);
        }
    }

}
