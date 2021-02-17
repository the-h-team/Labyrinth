package com.github.sanctum.labyrinth.data;

import java.math.BigDecimal;
import java.util.Optional;
import org.bukkit.OfflinePlayer;

public class DefaultProvision extends EconomyProvision {

	@Override
	public String getImplementation() {
		return "Default";
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
}
