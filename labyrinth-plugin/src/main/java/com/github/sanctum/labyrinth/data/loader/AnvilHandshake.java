package com.github.sanctum.labyrinth.data.loader;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.Registry;
import java.io.InputStream;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class AnvilHandshake {

	public static void locate() {
		FileManager fm = FileList.search(Labyrinth.getInstance()).find("Test", "Service");
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
		InputStream stream = Labyrinth.getInstance().getResource(version);
		if (stream == null) {
			Labyrinth.getInstance().getLogger().severe("===================================================================");
			Labyrinth.getInstance().getLogger().severe("- AnvilKey-" + version + " not found. Consult labyrinth developers.");
			Labyrinth.getInstance().getLogger().severe("===================================================================");
			return;
		}
		FileManager.copy(stream, fm.getFile());
		Labyrinth.getInstance().getLogger().info("===================================================================");
		Labyrinth.getInstance().getLogger().info("- AnvilKey-" + version + " injected into directory.");
		Labyrinth.getInstance().getLogger().info("===================================================================");
	}

	public static void register() {

		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);

		new Registry.Loader<>(AnvilKey.class).from("Service").source(Labyrinth.getInstance()).operate(key -> {

			AnvilMechanics mechanics = key.get();

			if (mechanics != null) {

				if (!key.version().contains(version)) {
					Labyrinth.getInstance().getLogger().severe("===================================================================");
					Labyrinth.getInstance().getLogger().severe("- AnvilKey-" + key.version() + " invalid for " + version);
					Labyrinth.getInstance().getLogger().severe("===================================================================");
					return;
				}

				Bukkit.getServicesManager().register(AnvilMechanics.class, mechanics, Labyrinth.getInstance(), ServicePriority.High);

				Labyrinth.getInstance().getLogger().info("===================================================================");
				Labyrinth.getInstance().getLogger().info("- AnvilKey-" + key.getClass().getSimpleName() + " selected as primary anvil gui instructor.");
				Labyrinth.getInstance().getLogger().info("===================================================================");

			} else {

				Labyrinth.getInstance().getLogger().severe("===================================================================");
				Labyrinth.getInstance().getLogger().severe("- AnvilKey-" + key.getClass().getSimpleName() + " has an invalid mechanical override.");
				Labyrinth.getInstance().getLogger().severe("===================================================================");

			}

		});

	}

}
