package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.Labyrinth;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @deprecated No longer supported. Replaced by {@link} and marked for later removal.
 */
public class Config {
    private final String n;
    private final String d;
    private FileConfiguration fc;
    private File file;
    private final JavaPlugin plugin;
    private static final List<Config> configs = new ArrayList<>();

    public Config(final String n, final String d) {
        this.plugin = JavaPlugin.getProvidingPlugin(Config.class);
        this.n = n;
        this.d = d;
        Config.configs.add(this);
    }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return (n == null) ? "" : n;
    }

    public String getDirectory() {
        return (d == null) ? "" : d;
    }

    public static Config get(final String n, final String d) {
        for (final Config c: configs) {
            if(c.getName().equals(n) && (c.getDirectory() != null && c.getDirectory().equals(d))) {
                return c;
            }
        }

        return new Config(n, d);
    }

    public void flush() {
        configs.removeIf(c -> c.getName().equals(getName()));
    }

    public boolean delete() {
        configs.removeIf(c -> c.getName().equals(getName()));
        return this.getFile().delete();
    }

    public boolean exists() {
        if(this.fc == null || this.file == null) {
            final File temp = new File(this.getDataFolder(), this.getName() + ".yml");
            if(!temp.exists()) {
                return false;
            }
            this.file = temp;
        }
        return true;
    }

    public File getFile() {
        if(this.file == null) {
            this.file = new File(this.getDataFolder(), this.getName() + ".yml"); //create method get data folder
            if(!this.file.exists()) {
                try {
                    this.file.createNewFile();
                }catch(final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.file;
    }

    public FileConfiguration getConfig() {
        if(this.fc == null) {
            this.fc = YamlConfiguration.loadConfiguration(this.getFile());
        }
        return this.fc;
    }

    public File getDataFolder() {
        final File dir = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
        File d;
        if (this.d != null) {
            d = new File(dir.getParentFile().getPath(), Labyrinth.getInstance().getName() + "/" + this.d + "/");
        } else {
            d = new File(dir.getParentFile().getPath(), Labyrinth.getInstance().getName());
        }
        if(!d.exists()) {
            d.mkdirs();
        }
        return d;
    }

    public void reload() {
        if (this.file == null) {
            this.file = new File(getDataFolder(), getName() + ".yml");
            if (!this.file.exists())
                try {
                    this.file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            this.fc = YamlConfiguration.loadConfiguration(this.file);
            File defConfigStream = new File(this.plugin.getDataFolder(), getName() + ".yml");
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.fc.setDefaults(defConfig);
            Config.configs.removeIf(c -> c.getName().equals(this.n));
        }
    }

    public void saveConfig() {
        try {
            this.getConfig().save(this.getFile());
        }catch(final IOException e) {
            e.printStackTrace();
        }
    }

}

