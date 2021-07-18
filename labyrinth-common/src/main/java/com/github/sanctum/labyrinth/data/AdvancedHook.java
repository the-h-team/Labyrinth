package com.github.sanctum.labyrinth.data;

import com.github.sanctum.economy.construct.implement.AdvancedEconomy;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class AdvancedHook {

	private static AdvancedEconomy econ = null;

	private final LabyrinthAPI instance;

	public AdvancedHook(LabyrinthAPI instance) {
		this.instance = instance;
		if (!economyFound()) {
			instance.getLogger().warning("- No enterprise economy provider found.");
			instance.getLogger().warning("- Enterprise economy implementations from Labyrinth will not work.");
		}
	}

	private boolean economyFound() {
		if (Bukkit.getPluginManager().getPlugin("Enterprise") == null) {
			return false;
		}
		RegisteredServiceProvider<AdvancedEconomy> rsp = Bukkit.getServicesManager().getRegistration(AdvancedEconomy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		instance.getLogger().info("- Enterprise economy provider found. Now using: " + rsp.getProvider().getPlugin().getName());
		return true;
	}

	/**
	 * Get the Enterprise economy interface, provided if possible onEnable natively.
	 *
	 * @return A labyrinth native compilation of Enterprise's economy interface.
	 */
	public static AdvancedEconomy getEconomy() {
		return econ;
	}

}
