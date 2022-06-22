package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Ordinal;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.Registry;
import com.github.sanctum.labyrinth.task.Task;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

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

	public static class Handshake extends Task {

		private static final long serialVersionUID = -6080924258181953124L;
		static Handshake handshake;
		private final LabyrinthAPI instance;
		private final String version;
		private boolean located;

		Handshake(LabyrinthAPI instance) {
			super("Labyrinth-Handshake", TaskService.SYNCHRONOUS);
			this.instance = instance;
			this.version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
		}

		@Ordinal
		private void initialize() {
			if (!located) {
				// create a stream containing the version jar.
				InputStream stream = instance.getPluginInstance().getResource(version + ".jar");

				if (stream == null) {
					instance.getLogger().severe("===================================================================");
					instance.getLogger().severe("- Version service " + version + " not supported. Consult labyrinth developers.");
					instance.getLogger().severe("===================================================================");
					return;
				}

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

				if (!file.exists()) {
					this.located = true;
					// write the jar file to the service's directory.
					FileList.copy(stream, file);
					instance.getLogger().info("===================================================================");
					instance.getLogger().info("- Compiling version " + version + ".");
					instance.getLogger().info("===================================================================");

					try {
						AnvilMechhanicsLoader loader = new AnvilMechhanicsLoader(file);
						ExternalDataService service = loader.getMainClass();
						AnvilMechanics mechanics = service.getMechanics();
						if (mechanics != null) {
							if (!service.getServerVersion().contains(version)) {
								instance.getLogger().severe("===================================================================");
								instance.getLogger().severe("- Version service " + service.getServerVersion() + " invalid for " + version);
								instance.getLogger().severe("===================================================================");
								return;
							}

							Bukkit.getServicesManager().register(AnvilMechanics.class, mechanics, instance.getPluginInstance(), ServicePriority.High);

							instance.getLogger().info("===================================================================");
							instance.getLogger().info("- Version service " + service.getClass().getSimpleName() + " selected as primary anvil mechanics.");
							instance.getLogger().info("===================================================================");

							service.setValid();
						} else {
							instance.getLogger().warning("===================================================================");
							instance.getLogger().warning("- Version service " + service.getClass().getSimpleName() + " has invalid anvil mechanics...");
							instance.getLogger().warning("===================================================================");
						}
					} catch (IOException e) {
						instance.getLogger().severe("===================================================================");
						instance.getLogger().severe("- Unable to resolve version service " + version + ", contact labyrinth developers.");
						instance.getLogger().severe("===================================================================");
					}

				}
			}
		}

		@Override
		public String toString() {
			return "Handshake{" +
					"version='" + version + '\'' +
					", located=" + located +
					'}';
		}

		public static Handshake getInstance(@NotNull LabyrinthAPI api) {
			return handshake != null ? handshake : (handshake = new Handshake(api));
		}

	}

}
