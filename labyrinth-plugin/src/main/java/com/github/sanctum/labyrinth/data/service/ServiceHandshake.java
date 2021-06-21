package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.Registry;
import java.io.File;
import java.io.InputStream;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class ServiceHandshake {

	public static void locate() {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
		InputStream stream = Labyrinth.getInstance().getResource(version + ".jar");

		if (stream == null) {
			Labyrinth.getInstance().getLogger().severe("===================================================================");
			Labyrinth.getInstance().getLogger().severe("- Version service " + version + " not found. Consult labyrinth developers.");
			Labyrinth.getInstance().getLogger().severe("===================================================================");
			return;
		}

		File file = new File("plugins/Labyrinth/Service/" + version + ".jar");

		for (File f : file.getParentFile().listFiles()) {
			if (f.isFile()) {
				if (f.delete()) {
					Labyrinth.getInstance().getLogger().info("- Deleting old version traces.");
				}
			}
		}

		if (!file.exists()) {
			FileManager.copy(stream, file);
			Labyrinth.getInstance().getLogger().info("===================================================================");
			Labyrinth.getInstance().getLogger().info("- Compiling version " + version + ".");
			Labyrinth.getInstance().getLogger().info("===================================================================");
		}
	}

	public static void register() {

		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);

		new Registry.Loader<>(ExternalDataService.class).from("Service").source(Labyrinth.getInstance()).operate(key -> {

			AnvilMechanics mechanics = key.getMechanics();

			if (mechanics != null) {

				if (!key.getServerVersion().contains(version)) {
					Labyrinth.getInstance().getLogger().severe("===================================================================");
					Labyrinth.getInstance().getLogger().severe("- Version service " + key.getServerVersion() + " invalid for " + version);
					Labyrinth.getInstance().getLogger().severe("===================================================================");
					return;
				}

				Bukkit.getServicesManager().register(AnvilMechanics.class, mechanics, Labyrinth.getInstance(), ServicePriority.High);

				Labyrinth.getInstance().getLogger().info("===================================================================");
				Labyrinth.getInstance().getLogger().info("- Version service " + key.getClass().getSimpleName() + " selected as primary anvil gui instructor.");
				Labyrinth.getInstance().getLogger().info("===================================================================");

			} else {

				Labyrinth.getInstance().getLogger().severe("===================================================================");
				Labyrinth.getInstance().getLogger().severe("- Version service " + key.getClass().getSimpleName() + " has an invalid mechanical override.");
				Labyrinth.getInstance().getLogger().severe("===================================================================");

			}

		});

	}

}
