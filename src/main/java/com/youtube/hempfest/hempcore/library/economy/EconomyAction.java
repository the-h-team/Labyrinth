package com.youtube.hempfest.hempcore.library.economy;

import com.youtube.hempfest.hempcore.HempCore;
import com.youtube.hempfest.hempcore.data.Config;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class EconomyAction {

	private double amount;

	private final boolean success;

	private final String info;

	private final String name;

	public EconomyAction(double amount, String name, boolean success, String info) {
		this.amount = amount;
		this.success = success;
		this.info = info;
		this.name = name;
	}

	public EconomyAction(String name, boolean success, String info) {
		this.success = success;
		this.info = info;
		this.name = name;
	}

	public boolean isSuccess() {
		if (HempCore.getInstance().logRunning()) {
			HempCore.getInstance().log.info("- Transaction of " + amount + "has " + (success ? "SUCCEEDED" : "FAILED"));
			Config config = Config.get("log-" + new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(), "Logs");
			List<String> array = config.getConfig().getStringList("info");
			array.add("[Economy] - Transaction of " + amount + "has " + (success ? "SUCCEEDED" : "FAILED"));
			config.getConfig().set("info", array);
			config.saveConfig();
		}
		return success;
	}

	/**
	 * Gets the exact amount involved with the transaction
	 * @return amount or null
	 */
	public double getAmount() {
		return amount;
	}

	public String getInfo() {
		if (HempCore.getInstance().logRunning()) {
			HempCore.getInstance().log.info("- [INFO]: " + info);
			Config config = Config.get("log-" + new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(), "Logs");
			List<String> array = config.getConfig().getStringList("info");
			array.add("[Economy] - [INFO]: " + info);
			config.getConfig().set("info", array);
			config.saveConfig();
		}
		return info;
	}
}
