package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class VaultHook {

	public VaultHook(LabyrinthAPI instance) {
		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			final Economy provider = Bukkit.getServicesManager().load(Economy.class);
			if (provider != null) {
				instance.getLogger().info("- Vault economy provider found. Now using: " + provider.getName());
				final VaultEconomyProvision vault = new VaultEconomyProvision(provider);
				Bukkit.getServicesManager().register(EconomyProvision.class, vault, instance.getPluginInstance(), ServicePriority.Low);
				return;
			}
		}
		instance.getLogger().warning("- No vault economy provider found.");
		instance.getLogger().warning("- Vault economy implementations from Labyrinth will not work.");
	}
}
