package com.github.sanctum.labyrinth.data;

import com.google.common.collect.ImmutableMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Load addon classes in Java 16+.
 *
 * @author ms5984
 */
public class AddonLoader {
    private static final Field PLUGIN_CLASS_MAP;
    static {
        try {
            PLUGIN_CLASS_MAP = Class.forName("org.bukkit.plugin.java.PluginClassLoader").getDeclaredField("classes");
            PLUGIN_CLASS_MAP.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to reach class map", e);
        }
    }

    private AddonLoader(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    final class AddonClassLoader extends URLClassLoader {
        private AddonClassLoader(URL[] urls) {
            super(urls, javaPlugin.getClass().getClassLoader());
        }

        final Class<?> resolveClass(String name) throws ClassNotFoundException {
            return loadClass(name, true);
        }

        void injectClass(String className) {
            final Class<?> resolvedClass;
            try {
                resolvedClass = resolveClass(className);
            } catch (ClassNotFoundException e) {
                javaPlugin.getLogger().warning(() -> "Unable to inject '" + className + "'");
                javaPlugin.getLogger().warning(e::getMessage);
                return;
            }
            getClassMap(javaPlugin).put(className, resolvedClass);
            javaPlugin.getLogger().finest(() -> "Loaded '" + className + "' successfully.");
        }
    }
    private final JavaPlugin javaPlugin;

    /**
     * Load a single jar file for your plugin.
     *
     * @param addonJar addon jar as a File object
     * @throws IllegalArgumentException if addonJar is not a .jar file
     * @throws IllegalStateException if unable to read from the JarFile
     */
    public void loadFile(File addonJar) throws IllegalArgumentException, IllegalStateException {
        if (addonJar.isDirectory()) throw new IllegalArgumentException("File must not be a directory");
        if (!addonJar.getName().endsWith(".jar")) throw new IllegalArgumentException("File must be a .jar");
        final JarFile jarFile;
        try {
            jarFile = new JarFile(addonJar);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        injectJars(ImmutableMap.of(getValidJarURL(addonJar).orElseThrow(IllegalStateException::new), jarFile));
        javaPlugin.getLogger().info(() -> "Loaded addon file " + addonJar.getName() + "successfully.");
    }

    /**
     * Load all jars from a directory for your plugin.
     *
     * @param folder addon folder as a File object
     * @throws IllegalArgumentException if folder is not a directory
     */
    public void loadFolder(File folder) throws IllegalArgumentException {
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
        injectJars(builder.build());
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
    private void injectJars(Map<URL, JarFile> jars) {
        if (jars.isEmpty()) return;
        final AddonClassLoader addonClassLoader = new AddonClassLoader(jars.keySet().toArray(URL[]::new));
        jars.values().forEach(jarFile -> jarFile.stream()
                .map(ZipEntry::getName)
                .filter(entryName -> entryName.contains(".class") && !entryName.contains("$"))
                .map(classPath -> classPath.replace('/', '.'))
                .map(className -> className.substring(0, className.length() - 6))
                .forEach(addonClassLoader::injectClass));
    }

    /**
     * Get an AddonLoader instance for your plugin.
     *
     * @param yourPlugin an instance of your main plugin class
     * @return an addon loader for your plugin
     */
    public static AddonLoader forPlugin(JavaPlugin yourPlugin) {
        return new AddonLoader(yourPlugin);
    }

    private static Map<String, Class<?>> getClassMap(JavaPlugin javaPlugin) throws IllegalStateException {
        try {
            //noinspection unchecked
            return (Map<String, Class<?>>) PLUGIN_CLASS_MAP.get(javaPlugin.getClass().getClassLoader());
        } catch (ClassCastException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
