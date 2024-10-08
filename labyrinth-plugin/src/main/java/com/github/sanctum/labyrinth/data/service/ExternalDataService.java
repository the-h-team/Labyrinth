package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.api.LegacyCheckService;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.panther.annotation.Ordinal;
import com.github.sanctum.panther.util.Task;
import com.github.sanctum.panther.util.TaskChain;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.Bukkit;
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

	public static final class Handshake extends Task {

		private static final long serialVersionUID = -6080924258181953124L;
		static Handshake handshake;
		private final LabyrinthAPI instance;
		private final String version;
		private boolean shook;

		Handshake(LabyrinthAPI instance) {
			super("Labyrinth-Handshake", TaskChain.getSynchronous());
			this.instance = instance;
			this.version = LegacyCheckService.VERSION;
		}

		@Ordinal
		private void initialize() {
			if (LabyrinthProvider.getInstance().isJava20()) {
				instance.getLogger().info("- Using new mappings " + version + ", switching to WesJD AnvilGUI.");
				return;
			}

			if (!shook) {
				// create a stream containing the version jar.
				InputStream stream = instance.getPluginInstance().getResource(version + ".jar");

				if (stream == null) {
					instance.getLogger().severe("- Version " + version + " not supported correctly. Consult labyrinth developers.");
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
					this.shook = true;
					instance.getLogger().info("- Compiling anvil mechanics for version " + version + ".");
					// write the jar file to the service's directory.
					FileList.copy(stream, file);
					try {
						// Load the mechanics classes into the JVM
						AnvilMechanicsLoader loader = new AnvilMechanicsLoader(file, instance.getPluginInstance().getClass().getClassLoader());
						ExternalDataService service = loader.getMainClass();
						AnvilMechanics mechanics = service.getMechanics();
						if (mechanics != null) {
							if (!service.getServerVersion().contains(version)) {
								instance.getLogger().severe("- Version service " + service.getServerVersion() + " invalid for " + version);
								instance.getLogger().severe("- Anvil GUI interfacing will not work correctly.");
								return;
							}
							Bukkit.getServicesManager().register(AnvilMechanics.class, mechanics, instance.getPluginInstance(), ServicePriority.High);

							instance.getLogger().info("- Version service " + service.getClass().getSimpleName() + " selected as primary anvil mechanics.");

							service.setValid();
						} else {
							instance.getLogger().severe("- Version service " + service.getClass().getSimpleName() + " has invalid anvil mechanics...");
						}
					} catch (IOException e) {
						instance.getLogger().severe("- Unable to resolve version service " + version + ", contact labyrinth developers.");
					}

				}
			}
		}

		@Override
		public String toString() {
			return "Handshake{" +
					"version='" + version + '\'' +
					", shook=" + shook +
					'}';
		}

		public static Handshake getInstance(@NotNull LabyrinthAPI api) {
			return handshake != null ? handshake : (handshake = new Handshake(api));
		}

	}

}
