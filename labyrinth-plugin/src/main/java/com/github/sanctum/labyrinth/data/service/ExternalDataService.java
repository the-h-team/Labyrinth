package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.Registry;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

/**
 * Used solely to load {@link AnvilMechanics} for use with {@link com.github.sanctum.labyrinth.gui.unity.construct.Menu} on RUNTIME.
 */
public abstract class ExternalDataService {

	private boolean valid;

	public abstract AnvilMechanics getMechanics();

	public abstract String getServerVersion();

	public boolean isValid() {
		return valid;
	}

	protected void setValid() {
		this.valid = true;
	}

	public static class Handshake {

		private final LabyrinthAPI instance;

		private final String version;

		private boolean located;

		public Handshake(LabyrinthAPI instance) {
			this.instance = instance;
			this.version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
		}

		public String getVersion() {
			return version;
		}

		public boolean isLocated() {
			return located;
		}

		public boolean locate() {
			if (!located) {
				InputStream stream = instance.getPluginInstance().getResource(version + ".jar");

				File file = new File("plugins/Labyrinth/Service/" + version + ".jar");

				if (!file.getParentFile().exists()) {
					//noinspection ResultOfMethodCallIgnored
					file.getParentFile().mkdirs();
				}

				for (File f : file.getParentFile().listFiles()) {
					if (f.isFile()) {
						if (f.delete()) {
							instance.getLogger().info("- Deleting old version traces.");
						}
					}
				}

				if (stream == null) {
					instance.getLogger().severe("===================================================================");
					instance.getLogger().severe("- Version service " + version + " not found. Consult labyrinth developers.");
					instance.getLogger().severe("===================================================================");
					return false;
				}

				if (!file.exists()) {
					this.located = true;
					FileList.copy(stream, file);
					instance.getLogger().info("===================================================================");
					instance.getLogger().info("- Compiling version " + version + ".");
					instance.getLogger().info("===================================================================");
				}
				return true;
			} else {
				return false;
			}
		}

		public boolean register() {

			List<ExternalDataService> services = new Registry.Loader<>(ExternalDataService.class).from("Service").source(instance.getPluginInstance()).confine(key -> {

				AnvilMechanics mechanics = key.getMechanics();

				if (mechanics != null) {

					if (!key.getServerVersion().contains(version)) {
						instance.getLogger().severe("===================================================================");
						instance.getLogger().severe("- Version service " + key.getServerVersion() + " invalid for " + version);
						instance.getLogger().severe("===================================================================");
						return;
					}

					Bukkit.getServicesManager().register(AnvilMechanics.class, mechanics, instance.getPluginInstance(), ServicePriority.High);

					instance.getLogger().info("===================================================================");
					instance.getLogger().info("- Version service " + key.getClass().getSimpleName() + " selected as primary anvil gui instructor.");
					instance.getLogger().info("===================================================================");

					key.setValid();

				} else {
					instance.getLogger().warning("===================================================================");
					instance.getLogger().warning("- Version service " + key.getClass().getSimpleName() + " has an invalid mechanical override skipping...");
					instance.getLogger().warning("===================================================================");
				}

			}).getData();

			if (services.size() == 1 && services.get(0).isValid()) {
				this.located = true;
				return true;
			}

			return false;
		}

	}

}
