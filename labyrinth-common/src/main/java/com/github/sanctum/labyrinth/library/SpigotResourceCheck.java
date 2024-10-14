package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.interfacing.WebResponse;
import com.github.sanctum.panther.annotation.Experimental;
import org.bukkit.plugin.Plugin;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;

/**
 * @author Hempfest
 */
public abstract class SpigotResourceCheck implements ResourceVersionCheck {
    private static final long serialVersionUID = -5429608140512156144L;
    /**
     * Format: X.X.X
     */
    public static final int STANDARD = 3;
    /**
     * Format: X.X
     */
    public static final int SIMPLE = 2;
    /**
     * Format: X
     */
    public static final int BASIC = 1;

    private final int projectId;
    private URL url;
    private String latest;
    private final Plugin plugin;

    public SpigotResourceCheck(Plugin plugin, int id) {
        this.projectId = id;
        this.plugin = plugin;
        this.latest = plugin.getDescription().getVersion();
        try {
            this.url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectId);
        } catch (MalformedURLException ignored) {
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull String getCurrent() {
        return latest;
    }

    @Override
    public @NotNull String getLatest() {
        return this.latest;
    }

    public @NotNull String getResource() {
        return "https://www.spigotmc.org/resources/" + projectId;
    }

    public boolean hasUpdate(@MagicConstant(intValues = {STANDARD, SIMPLE, BASIC}) int precision) {
        try {
            String latest = getLatest();
            String version = plugin.getDescription().getVersion();
            String[] version_split = version.split("\\.");
            String[] new_version_split = latest.split("\\.");

            if (precision == BASIC) { // Format: vX
                int current_1 = Integer.parseInt(version_split[0]);
                int latest_1 = Integer.parseInt(new_version_split[0]);
                return latest_1 > current_1;
            }

            if (precision == SIMPLE) { // Format: vX.x
                int current_1 = Integer.parseInt(version_split[0]);
                int latest_1 = Integer.parseInt(new_version_split[0]);
                int current_2 = Integer.parseInt(version_split[1]);
                int latest_2 = Integer.parseInt(new_version_split[1]);

                if (latest_1 > current_1) {
                    return true;
                } else {
                    if (latest_1 == current_1) {
                        return latest_2 > current_2;
                    }
                    return false;
                }

            }
            if (precision == STANDARD) { // Format vX.X.X
                int current_1 = Integer.parseInt(version_split[0]);
                int latest_1 = Integer.parseInt(new_version_split[0]);
                int current_2 = Integer.parseInt(version_split[1]);
                int latest_2 = Integer.parseInt(new_version_split[1]);
                int current_3 = Integer.parseInt(version_split[2]);
                int latest_3 = Integer.parseInt(new_version_split[2]);
                if (latest_1 > current_1) {
                    return true;
                }
                if (latest_1 == current_1 && latest_2 == current_2) {
                    if (latest_3 == current_3) {
                        return false;
                    }
                    return latest_3 > current_3;
                }
                if (latest_1 == current_1 && latest_2 > current_2) {
                    if (latest_3 > current_3) {
                        return true;
                    }
                    return true;
                }
                return false;
            }
            throw new IllegalStateException("Wrong precision selected.");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasUpdate() {
        return hasUpdate(SpigotResourceCheck.STANDARD);
    }

    @Override
    public void run() {
        try {
            this.latest = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream())).readLine();
            LabyrinthProvider.getInstance().getLogger().info(" - [SpigotResourceCheck] Successfully ran check operation.");
        } catch (IOException e) {
            throw new RuntimeException("[SpigotResourceCheck] Failed to run check operation:", e);
        }
    }

    @Override
    public @NotNull WebResponse getFromGitHub(String output, String file, String type) {
        try {
            URL url = new URL("https://github.com/" + getAuthor() + "/" + getResource() + "/releases/download/" + getLatest() + "/" + getResource() + ".jar");
            File f = new File(LabyrinthProvider.getInstance().getPluginInstance().getDataFolder(), "downloads/" + output + "/" + file + "." + type);
            if (f.exists()) return () -> "Latest version of " + getResource() + " already downloaded.";
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            File parent = f.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) return () -> "Unable to access output location.";
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            return () -> "SUCCESS";
        } catch (IOException ex) {
            try {
                File f = new File(LabyrinthProvider.getInstance().getPluginInstance().getDataFolder(), "downloads/" + output + "/log.txt");
                PrintWriter myWriter = new PrintWriter(f);
                for (StackTraceElement trace : ex.getStackTrace()) {
                    Date now = new Date();
                    myWriter.println("[" + now.toLocaleString() + "] " + trace.toString());
                }
                myWriter.close();
            } catch (FileNotFoundException e) {
                return () -> "Unable to locate target file from github & print log failure.";
            }
            return () -> "Unable to get " + getResource() + " latest release, resource not found.";
        }
    }
}