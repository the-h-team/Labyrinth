package com.github.sanctum.labyrinth.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.bukkit.plugin.Plugin;

public abstract class UpdateChecker {

	public static final int STANDARD = 3;
	public static final int SIMPLE = 2;
	public static final int BASIC = 1;

	private final int PROJECT_ID;
	private URL URL;
	private String LATEST;
	private final Plugin PLUGIN;

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

	public String getCurrent() {
		return LATEST;
	}

	public String getLatest() throws IOException {
		URLConnection con = URL.openConnection();
		return this.LATEST = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
	}

	public String getResource() {
		return "https://www.spigotmc.org/resources/" + PROJECT_ID;
	}

	public boolean hasUpdate(int precision) {
		try {
			String latest = getLatest();
			String version = PLUGIN.getDescription().getVersion();
			String[] version_split = version.split("\\.");
			String[] new_version_split = latest.split("\\.");

			if (precision == 1) {
				int current_1 = Integer.parseInt(version_split[0]);
				int latest_1 = Integer.parseInt(new_version_split[0]);
				return latest_1 > current_1;
			}

			if (precision == 2) {
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

			if (precision == 3) {
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
		try {
			String latest = getLatest();
			String version = PLUGIN.getDescription().getVersion();
			String[] version_split = version.split("\\.");
			String[] new_version_split = latest.split("\\.");
			int current_1 = Integer.parseInt(version_split[0]);
			int current_2 = Integer.parseInt(version_split[1]);
			int current_3 = Integer.parseInt(version_split[2]);
			int latest_1 = Integer.parseInt(new_version_split[0]);
			int latest_2 = Integer.parseInt(new_version_split[1]);
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
		} catch (Exception e) {
			return false;
		}
	}

}