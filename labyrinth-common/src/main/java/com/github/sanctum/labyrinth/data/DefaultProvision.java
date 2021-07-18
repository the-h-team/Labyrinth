package com.github.sanctum.labyrinth.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.Optional;

public class DefaultProvision extends EconomyProvision {

	@Override
	public String getImplementation() {
		boolean isVault = Bukkit.getPluginManager().isPluginEnabled("Vault");
		if (Bukkit.getPluginManager().isPluginEnabled("Enterprise")) {
			isVault = false;
		} else {
			if (!isVault) {
				return "Default | No Economy Bridge";
			}
		}
		return "Default (Normal) | Interface: " + (isVault ? "Vault" : "Enterprise");
	}

	@Override
	public Optional<Boolean> deposit(BigDecimal amount, OfflinePlayer p) {
		return super.deposit(amount, p);
	}

	@Override
	public Optional<Boolean> deposit(BigDecimal amount, OfflinePlayer p, String world) {
		return super.deposit(amount, p, world);
	}

	@Override
	public Optional<Boolean> withdraw(BigDecimal amount, OfflinePlayer p) {
		return super.withdraw(amount, p);
	}

	@Override
	public Optional<Boolean> withdraw(BigDecimal amount, OfflinePlayer p, String world) {
		return super.withdraw(amount, p, world);
	}

	@Override
	public Optional<Boolean> has(BigDecimal amount, OfflinePlayer p) {
		return super.has(amount, p);
	}

	@Override
	public Optional<Boolean> has(BigDecimal amount, OfflinePlayer p, String world) {
		return super.has(amount, p, world);
	}

	@Override
	public Optional<Double> balance(OfflinePlayer p) {
		return super.balance(p);
	}

	@Override
	public Optional<Double> balance(OfflinePlayer p, String world) {
		return super.balance(p, world);
	}

	@Override
	public Optional<Double> accountBalance(OfflinePlayer p) {
		return super.accountBalance(p);
	}

	@Override
	public Optional<Double> accountBalance(OfflinePlayer p, String world) {
		return super.accountBalance(p, world);
	}
}
