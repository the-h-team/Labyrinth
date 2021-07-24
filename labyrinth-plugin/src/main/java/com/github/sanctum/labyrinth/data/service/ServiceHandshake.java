package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.Registry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import java.io.File;
import java.io.InputStream;

public class ServiceHandshake {

	public static void locate() {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
		final LabyrinthAPI labyrinthAPI = LabyrinthProvider.getInstance();
		InputStream stream = labyrinthAPI.getPluginInstance().getResource(version + ".jar");

		File file = new File("plugins/Labyrinth/Service/" + version + ".jar");

		if (!file.getParentFile().exists()) {
			//noinspection ResultOfMethodCallIgnored
			file.getParentFile().mkdirs();
		}

		for (File f : file.getParentFile().listFiles()) {
			if (f.isFile()) {
				if (f.delete()) {
					labyrinthAPI.getLogger().info("- Deleting old version traces.");
				}
			}
		}

		if (stream == null) {
			labyrinthAPI.getLogger().severe("===================================================================");
			labyrinthAPI.getLogger().severe("- Version service " + version + " not found. Consult labyrinth developers.");
			labyrinthAPI.getLogger().severe("===================================================================");
			return;
		}

		if (!file.exists()) {
			FileManager.copy(stream, file);
			labyrinthAPI.getLogger().info("===================================================================");
			labyrinthAPI.getLogger().info("- Compiling version " + version + ".");
			labyrinthAPI.getLogger().info("===================================================================");
		}
	}

	public static void register() {

		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);

		final LabyrinthAPI labyrinthAPI = LabyrinthProvider.getInstance();
		new Registry.Loader<>(ExternalDataService.class).from("Service").source(labyrinthAPI.getPluginInstance()).operate(key -> {

			AnvilMechanics mechanics = key.getMechanics();

			if (mechanics != null) {

				if (!key.getServerVersion().contains(version)) {
					labyrinthAPI.getLogger().severe("===================================================================");
					labyrinthAPI.getLogger().severe("- Version service " + key.getServerVersion() + " invalid for " + version);
					labyrinthAPI.getLogger().severe("===================================================================");
					return;
				}

				Bukkit.getServicesManager().register(AnvilMechanics.class, mechanics, labyrinthAPI.getPluginInstance(), ServicePriority.High);

				labyrinthAPI.getLogger().info("===================================================================");
				labyrinthAPI.getLogger().info("- Version service " + key.getClass().getSimpleName() + " selected as primary anvil gui instructor.");
				labyrinthAPI.getLogger().info("===================================================================");

			} else {

				labyrinthAPI.getLogger().warning("===================================================================");
				labyrinthAPI.getLogger().warning("- Version service " + key.getClass().getSimpleName() + " has an invalid mechanical override skipping...");
				labyrinthAPI.getLogger().warning("===================================================================");

			}

		});

	}

}
