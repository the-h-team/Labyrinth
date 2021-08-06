package com.github.sanctum.labyrinth.data;

import com.github.sanctum.economy.construct.implement.AdvancedEconomy;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class AdvancedEconomyImplementation {

	public AdvancedEconomyImplementation(LabyrinthAPI instance) {
		if (Bukkit.getPluginManager().getPlugin("Enterprise") != null) {
			final AdvancedEconomy provider = Bukkit.getServicesManager().load(AdvancedEconomy.class);
			if (provider != null) {
				instance.getLogger().info("- Enterprise economy provider found. Now using: " + provider.getPlugin().getName());
				final AdvancedEconomyProvision advancedEconomy = new AdvancedEconomyProvision(provider);
				Bukkit.getServicesManager().register(EconomyProvision.class, advancedEconomy, instance.getPluginInstance(), ServicePriority.Normal);
				return;
			}
		}
		instance.getLogger().warning("- No enterprise economy provider found.");
		instance.getLogger().warning("- Enterprise economy implementations from Labyrinth will not work.");
	}
}
