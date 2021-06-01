package com.github.sanctum.labyrinth.data;

import com.github.sanctum.economy.construct.account.permissive.AccountType;
import java.math.BigDecimal;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public abstract class EconomyProvision {

	public static EconomyProvision getInstance() {
		EconomyProvision provision = Bukkit.getServicesManager().load(EconomyProvision.class);
		if (provision == null) {
			return new DefaultProvision();
		} else
			return provision;
	}

	public abstract String getImplementation();

	public Optional<Double> balance(OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).getBalance().doubleValue());
		}
		return Optional.of(VaultHook.getEconomy().getBalance(p));
	}

	public Optional<Double> balance(OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).getBalance(world).doubleValue());
		}
		return Optional.of(VaultHook.getEconomy().getBalance(p, world));
	}

	public Optional<Boolean> has(BigDecimal amount, OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).has(amount));
		}
		return Optional.of(VaultHook.getEconomy().has(p, amount.doubleValue()));
	}

	public Optional<Boolean> has(BigDecimal amount, OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).has(amount, world));
		}
		return Optional.of(VaultHook.getEconomy().has(p, world, amount.doubleValue()));
	}

	public Optional<Boolean> deposit(BigDecimal amount, OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).deposit(amount).isSuccess());
		}
		return Optional.of(VaultHook.getEconomy().depositPlayer(p, amount.doubleValue()).transactionSuccess());
	}

	public Optional<Boolean> deposit(BigDecimal amount, OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).deposit(amount, world).isSuccess());
		}
		return Optional.of(VaultHook.getEconomy().depositPlayer(p, world, amount.doubleValue()).transactionSuccess());
	}

	public Optional<Boolean> withdraw(BigDecimal amount, OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).withdraw(amount).isSuccess());
		}
		return Optional.of(VaultHook.getEconomy().withdrawPlayer(p, amount.doubleValue()).transactionSuccess());
	}

	public Optional<Boolean> withdraw(BigDecimal amount, OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).withdraw(amount, world).isSuccess());
		}
		return Optional.of(VaultHook.getEconomy().withdrawPlayer(p, world, amount.doubleValue()).transactionSuccess());
	}

	public Optional<Double> accountBalance(OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p).getBalance().doubleValue());
		}
		return Optional.of(VaultHook.getEconomy().bankBalance(p.getUniqueId().toString()).amount);
	}

	public Optional<Double> accountBalance(OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p).getBalance(world).doubleValue());
		}
		return Optional.of(VaultHook.getEconomy().bankBalance(p.getUniqueId().toString()).amount);
	}

	public Optional<Boolean> accountHas(BigDecimal amount, OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p, AccountType.BANK_ACCOUNT).has(amount));
		}
		return Optional.of(VaultHook.getEconomy().bankBalance(p.getUniqueId().toString()).amount - amount.doubleValue() >= 0);
	}

	public Optional<Boolean> accountHas(BigDecimal amount, OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p, AccountType.BANK_ACCOUNT).has(amount, world));
		}
		return Optional.of(VaultHook.getEconomy().bankBalance(p.getUniqueId().toString()).amount - amount.doubleValue() >= 0);
	}

	public Optional<Boolean> depositAccount(BigDecimal amount, OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p, AccountType.BANK_ACCOUNT).deposit(amount).isSuccess());
		}
		return Optional.of(VaultHook.getEconomy().bankDeposit(p.getUniqueId().toString(), amount.doubleValue()).transactionSuccess());
	}

	public Optional<Boolean> depositAccount(BigDecimal amount, OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p, AccountType.BANK_ACCOUNT).deposit(amount, world).isSuccess());
		}
		return Optional.of(VaultHook.getEconomy().bankDeposit(p.getUniqueId().toString(), amount.doubleValue()).transactionSuccess());
	}

	public Optional<Boolean> withdrawAccount(BigDecimal amount, OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p, AccountType.BANK_ACCOUNT).withdraw(amount).isSuccess());
		}
		return Optional.of(VaultHook.getEconomy().bankWithdraw(p.getUniqueId().toString(), amount.doubleValue()).transactionSuccess());
	}

	public Optional<Boolean> withdrawAccount(BigDecimal amount, OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p, AccountType.BANK_ACCOUNT).withdraw(amount, world).isSuccess());
		}
		return Optional.of(VaultHook.getEconomy().bankWithdraw(p.getUniqueId().toString(), amount.doubleValue()).transactionSuccess());
	}


}
