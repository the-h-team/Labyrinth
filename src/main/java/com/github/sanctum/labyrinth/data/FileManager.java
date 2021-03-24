package com.github.sanctum.labyrinth.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Encapsulates config file operations.
 */
public class FileManager {
    protected final String n;
    protected final String d;
    protected File file;
    protected FileConfiguration fc;
    protected final File parent;
    protected final Plugin plugin;

    protected FileManager(@NotNull Plugin plugin, @NotNull final String n, @Nullable final String d) {
        this.n = n;
        this.d = d;
        this.plugin = plugin;
        // Get the data directory of the plugin that is providing this Config implementation
        final File pluginDataDir = plugin.getDataFolder();
        if (!pluginDataDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            pluginDataDir.mkdir();
        }
        // If d is null or empty, use plugin's data folder. If not get the file describing the subdirectory.
        final File parent = (d == null || d.isEmpty()) ? pluginDataDir : new File(pluginDataDir, d);
        if (!parent.exists()) {
            //noinspection ResultOfMethodCallIgnored
            parent.mkdir();
        }
        this.parent = parent;
        this.file = new File(parent, n.concat(".yml"));
        if (FileList.CACHE.get(plugin) == null) {
            LinkedList<FileManager> managers = new LinkedList<>();
            managers.add(this);
            FileList.CACHE.putIfAbsent(plugin, managers);
        } else {
            LinkedList<FileManager> managers = new LinkedList<>(FileList.CACHE.get(plugin));
            if (!managers.contains(this)) {
                managers.add(this);
            }
            FileList.CACHE.put(plugin, managers);
        }
    }

    /**
     * Copy an InputStream directly to a given File.
     * <p>
     * Useful for placing resources retrieved from a JavaPlugin
     * implementation at custom locations.
     *
     * @param in an InputStream, likely a plugin resource
     * @param file the desire file
     * @throws IllegalArgumentException if the file describes a directory
     * @throws IllegalStateException if write is unsuccessful
     */
    public static void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File is a directory!", e);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write to file! See log:", e);
        }
    }

    /**
     * Get the name of this Config.
     *
     * @return name of Config
     */
    public String getName() {
        return n;
    }

    /**
     * Get the description of the config if it has one.
     * <p>
     * Used to resolve subdirectory if present.
     *
     * @return an Optional describing this config's description field
     */
    public Optional<String> getDescription() {
        return Optional.ofNullable(d);
    }

    /**
     * Delete the backing file of this configuration.
     * <p>
     * Does not destroy backing YamlConfiguration; perform
     * {@link FileManager#reload()} if that is desired as well.
     *
     * @return true if file was successfully deleted
     */
    public boolean delete() {
        return file.delete();
    }

    /**
     * Check if the backing file currently exists.
     * <p>
     * Does interact whatsoever with the YamlConfiguration.
     *
     * @return true if file exists
     */
    public boolean exists() {
        return file.exists();
    }

    /**
     * Get the backing file for this Config; additionally creates
     * if not found.
     *
     * @return backing file File object
     */
    public File getFile() {
        if(!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch(final IOException e) {
                throw new IllegalStateException("Unable to create file! See log: " + this.file, e);
            }
        }
        return file;
    }

    /**
     * Get the FileConfiguration managed by this Config object.
     *
     * @return a File (Yaml) FileManager object
     */
    synchronized public FileConfiguration getConfig() {
        if(this.fc == null) {
            // fast exit with new blank configuration in the case of nonexistent file
            if (!file.exists()) {
                YamlConfiguration result = new YamlConfiguration();
                this.fc = result;
                return result;
            }
            // load configuration from file
            this.fc = YamlConfiguration.loadConfiguration(file);
        }
        return this.fc;
    }

    /**
     * Reload the file from disk.
     * <p>
     * If the backing file has been deleted, this method assigns a fresh,
     * blank configuration internally to this object. Otherwise, the file
     * is read from, directly replacing the existing configuration with
     * its values. No attempt is made to save the existing configuration
     * state, so keep that in mind when running this call.
     */
    synchronized public void reload() {
        if (!this.file.exists()) {
            this.fc = new YamlConfiguration();
        }

        this.fc = YamlConfiguration.loadConfiguration(this.file);
        File defConfigStream = new File(this.plugin.getDataFolder(), getName() + ".yml");
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        this.fc.setDefaults(defConfig);

        if (FileList.CACHE.get(this.plugin) == null) {
            LinkedList<FileManager> managers = new LinkedList<>();
            managers.add(this);
            FileList.CACHE.putIfAbsent(this.plugin, managers);
        } else {
            LinkedList<FileManager> managers = new LinkedList<>(FileList.CACHE.get(this.plugin));
            managers.removeIf(m -> m.getName().equals(getName()) && m.getDescription().equals(getDescription()));
            managers.add(this);
            FileList.CACHE.put(this.plugin, managers);
        }
    }

    /**
     * Save the configuration to its backing file.
     *
     * @throws IllegalStateException if an error is encountered while saving
     */
    synchronized public void saveConfig() {
        try {
            getConfig().save(file);
        } catch (final IOException e) {
            throw new IllegalStateException("Unable to save configuration file!", e);
        }
    }

    /**
     * Save the configuration to its backing file then immediately reload changes.
     *
     * @throws IllegalStateException if an error is encountered while saving
     */
    synchronized public void refreshConfig() {
        saveConfig();
        reload();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileManager)) return false;
        FileManager config = (FileManager) o;
        return n.equals(config.n) &&
                Objects.equals(d, config.d) &&
                Objects.equals(fc, config.fc) &&
                Objects.equals(file, config.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, d);
    }
}

