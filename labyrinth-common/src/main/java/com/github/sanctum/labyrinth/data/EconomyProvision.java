package com.github.sanctum.labyrinth.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.Optional;

public abstract class EconomyProvision {
	static AdvancedEconomyProvision enterprise;
	static VaultEconomyProvision vault;

	public static EconomyProvision getInstance() {
		EconomyProvision provision = Bukkit.getServicesManager().load(EconomyProvision.class);
		if (provision == null) {
			return EmptyProvision.INSTANCE;
		}
		return provision;
	}

	public abstract String getImplementation();

	public final boolean isValid() {
		return enterprise != null || vault != null || getInstance() != EmptyProvision.INSTANCE;
	}

	public Optional<Double> balance(OfflinePlayer p) {
		return Optional.empty();
	}

	public Optional<Double> balance(OfflinePlayer p, String world) {
		return Optional.empty();
	}

	public Optional<Boolean> has(BigDecimal amount, OfflinePlayer p) {
		return Optional.empty();
	}

	public Optional<Boolean> has(BigDecimal amount, OfflinePlayer p, String world) {
		return Optional.empty();
	}

	public Optional<Boolean> deposit(BigDecimal amount, OfflinePlayer p) {
		return Optional.empty();
	}

	public Optional<Boolean> deposit(BigDecimal amount, OfflinePlayer p, String world) {
		return Optional.empty();
	}

	public Optional<Boolean> withdraw(BigDecimal amount, OfflinePlayer p) {
		return Optional.empty();
	}

	public Optional<Boolean> withdraw(BigDecimal amount, OfflinePlayer p, String world) {
		return Optional.empty();
	}

	public Optional<Double> accountBalance(OfflinePlayer p) {
		return Optional.empty();
	}

	public Optional<Double> accountBalance(OfflinePlayer p, String world) {
		return Optional.empty();
	}

	public Optional<Boolean> accountHas(BigDecimal amount, OfflinePlayer p) {
		return Optional.empty();
	}

	public Optional<Boolean> accountHas(BigDecimal amount, OfflinePlayer p, String world) {
		return Optional.empty();
	}

	public Optional<Boolean> depositAccount(BigDecimal amount, OfflinePlayer p) {
		return Optional.empty();
	}

	public Optional<Boolean> depositAccount(BigDecimal amount, OfflinePlayer p, String world) {
		return Optional.empty();
	}

	public Optional<Boolean> withdrawAccount(BigDecimal amount, OfflinePlayer p) {
		return Optional.empty();
	}

	public Optional<Boolean> withdrawAccount(BigDecimal amount, OfflinePlayer p, String world) {
		return Optional.empty();
	}

	private static class EmptyProvision extends EconomyProvision {
		private static final EmptyProvision INSTANCE = new EmptyProvision();
		@Override
		public String getImplementation() {
			return "Default | No Economy Bridge";
		}
	}
}
