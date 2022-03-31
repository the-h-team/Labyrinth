package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.interfacing.ResourceCheck;
import com.github.sanctum.labyrinth.interfacing.WebResponse;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.intellij.lang.annotations.MagicConstant;

/**
 * @author Hempfest
 */
public abstract class UpdateChecker implements ResourceCheck {

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

	private final int PROJECT_ID;
	private URL URL;
	private String LATEST;
	private final Plugin PLUGIN;

	@Note("Used to update labyrinth specifically")
	public UpdateChecker() {
		this(LabyrinthProvider.getInstance().getPluginInstance(), 97679);
	}

	public UpdateChecker(Plugin plugin, int id) {
		this.PROJECT_ID = id;
		this.PLUGIN = plugin;
		this.LATEST = plugin.getDescription().getVersion();
		try {
			this.URL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + PROJECT_ID);
		} catch (MalformedURLException ignored) {
		}
	}

	public Plugin getPlugin() {
		return PLUGIN;
	}

	@Override
	public String getCurrent() {
		return LATEST;
	}

	@Override
	public String getLatest() {
		try {
			return this.LATEST = new BufferedReader(new InputStreamReader(URL.openConnection().getInputStream())).readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getResource() {
		return "https://www.spigotmc.org/resources/" + PROJECT_ID;
	}

	public boolean hasUpdate(@MagicConstant(intValues = {STANDARD, SIMPLE, BASIC}) int precision) {
		try {
			String latest = getLatest();
			String version = PLUGIN.getDescription().getVersion();
			String[] version_split = version.split("\\.");
			String[] new_version_split = latest.split("\\.");

			if (precision == BASIC) {
				int current_1 = Integer.parseInt(version_split[0]);
				int latest_1 = Integer.parseInt(new_version_split[0]);
				return latest_1 > current_1;
			}

			if (precision == SIMPLE) {
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

			if (precision == STANDARD) {
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
			throw new IllegalStateException("Unknown version precision.");
		} catch (Exception e) {
			return false;
		}
	}

	public boolean hasUpdate() {
		return hasUpdate(UpdateChecker.STANDARD);
	}

	@Override
	public void run() {
		if (hasUpdate()) {
			WebResponse.download(this, "labyrinth", getResource() + "-LU" + getLatest(), ".jar");
		}
	}
}