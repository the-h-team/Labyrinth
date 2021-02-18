package com.github.sanctum.labyrinth.data;

import com.github.sanctum.economy.construct.account.permissive.AccountType;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public abstract class EconomyProvision {

	public static EconomyProvision getInstance() {
		Comparator<RegisteredServiceProvider<EconomyProvision>> myComparator = Comparator.comparing(RegisteredServiceProvider::getPriority,
				Comparator.nullsLast(Comparator.naturalOrder()));
		Queue<RegisteredServiceProvider<EconomyProvision>> queue = new PriorityQueue<>(myComparator);
		queue.addAll(Bukkit.getServicesManager().getRegistrations(EconomyProvision.class));
		if (queue.isEmpty()) {
			return null;
		}
		return queue.peek().getProvider();
	}

	public abstract String getImplementation();

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
