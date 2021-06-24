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

	public boolean isValid() {

		if (!Bukkit.getPluginManager().isPluginEnabled("Vault") && !Bukkit.getPluginManager().isPluginEnabled("Enterprise")) {
			return false;
		}

		return VaultHook.getEconomy() != null || AdvancedHook.getEconomy() != null;
	}

	public Optional<Double> balance(OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).getBalance().doubleValue());
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().getBalance(p));
		}
		return Optional.empty();
	}

	public Optional<Double> balance(OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).getBalance(world).doubleValue());
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().getBalance(p, world));
		}
		return Optional.empty();
	}

	public Optional<Boolean> has(BigDecimal amount, OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).has(amount));
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().has(p, amount.doubleValue()));
		}
		return Optional.empty();
	}

	public Optional<Boolean> has(BigDecimal amount, OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).has(amount, world));
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().has(p, world, amount.doubleValue()));
		}
		return Optional.empty();
	}

	public Optional<Boolean> deposit(BigDecimal amount, OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).deposit(amount).isSuccess());
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().depositPlayer(p, amount.doubleValue()).transactionSuccess());
		}
		return Optional.empty();
	}

	public Optional<Boolean> deposit(BigDecimal amount, OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).deposit(amount, world).isSuccess());
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().depositPlayer(p, world, amount.doubleValue()).transactionSuccess());
		}
		return Optional.empty();
	}

	public Optional<Boolean> withdraw(BigDecimal amount, OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).withdraw(amount).isSuccess());
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().withdrawPlayer(p, amount.doubleValue()).transactionSuccess());
		}
		return Optional.empty();
	}

	public Optional<Boolean> withdraw(BigDecimal amount, OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getWallet(p).withdraw(amount, world).isSuccess());
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().withdrawPlayer(p, world, amount.doubleValue()).transactionSuccess());
		}
		return Optional.empty();
	}

	public Optional<Double> accountBalance(OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p).getBalance().doubleValue());
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().bankBalance(p.getUniqueId().toString()).amount);
		}
		return Optional.empty();
	}

	public Optional<Double> accountBalance(OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p).getBalance(world).doubleValue());
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().bankBalance(p.getUniqueId().toString()).amount);
		}
		return Optional.empty();
	}

	public Optional<Boolean> accountHas(BigDecimal amount, OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p, AccountType.BANK_ACCOUNT).has(amount));
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().bankBalance(p.getUniqueId().toString()).amount - amount.doubleValue() >= 0);
		}
		return Optional.empty();
	}

	public Optional<Boolean> accountHas(BigDecimal amount, OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p, AccountType.BANK_ACCOUNT).has(amount, world));
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().bankBalance(p.getUniqueId().toString()).amount - amount.doubleValue() >= 0);
		}
		return Optional.empty();
	}

	public Optional<Boolean> depositAccount(BigDecimal amount, OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p, AccountType.BANK_ACCOUNT).deposit(amount).isSuccess());
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().bankDeposit(p.getUniqueId().toString(), amount.doubleValue()).transactionSuccess());
		}
		return Optional.empty();
	}

	public Optional<Boolean> depositAccount(BigDecimal amount, OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p, AccountType.BANK_ACCOUNT).deposit(amount, world).isSuccess());
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().bankDeposit(p.getUniqueId().toString(), amount.doubleValue()).transactionSuccess());
		}
		return Optional.empty();
	}

	public Optional<Boolean> withdrawAccount(BigDecimal amount, OfflinePlayer p) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p, AccountType.BANK_ACCOUNT).withdraw(amount).isSuccess());
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().bankWithdraw(p.getUniqueId().toString(), amount.doubleValue()).transactionSuccess());
		}
		return Optional.empty();
	}

	public Optional<Boolean> withdrawAccount(BigDecimal amount, OfflinePlayer p, String world) {
		if (AdvancedHook.getEconomy() != null) {
			return Optional.of(AdvancedHook.getEconomy().getAccount(p, AccountType.BANK_ACCOUNT).withdraw(amount, world).isSuccess());
		}
		if (VaultHook.getEconomy() != null) {
			return Optional.of(VaultHook.getEconomy().bankWithdraw(p.getUniqueId().toString(), amount.doubleValue()).transactionSuccess());
		}
		return Optional.empty();
	}


}
