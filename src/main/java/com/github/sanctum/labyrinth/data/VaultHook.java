package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.Labyrinth;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

	private static Economy econ = null;

	private Labyrinth instance;

	public VaultHook(Labyrinth instance){
		this.instance = instance;
		if (!economyFound()) {
			instance.getLogger().warning("- No vault economy provider found.");
			instance.getLogger().warning("- Economy implementations from Labyrinth will not work.");
		}
	}

	private boolean economyFound() {
		if (instance.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = instance.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		instance.getLogger().info("- Economy provider found. Now using: " + rsp.getProvider().getName());
		return true;
	}

	public static Economy getEconomy() {
		return econ;
	}

}
