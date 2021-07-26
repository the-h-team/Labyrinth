package com.github.sanctum.labyrinth.data;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.Optional;

final class VaultEconomyProvision extends EconomyProvision {
    private final Economy economy;

    VaultEconomyProvision(Economy provider) {
        this.economy = provider;
        EconomyProvision.vault = this;
    }

    @Override
    public String getImplementation() {
        return "(Internal) | Interface: Vault";
    }

    @Override
    public Optional<Double> balance(OfflinePlayer p) {
        return Optional.of(economy.getBalance(p));
    }

    @Override
    public Optional<Double> balance(OfflinePlayer p, String world) {
        return Optional.of(economy.getBalance(p, world));
    }

    @Override
    public Optional<Boolean> has(BigDecimal amount, OfflinePlayer p) {
        return Optional.of(economy.has(p, amount.doubleValue()));
    }

    @Override
    public Optional<Boolean> has(BigDecimal amount, OfflinePlayer p, String world) {
        return Optional.of(economy.has(p, world, amount.doubleValue()));
    }

    @Override
    public Optional<Boolean> deposit(BigDecimal amount, OfflinePlayer p) {
        return Optional.of(economy.depositPlayer(p, amount.doubleValue()).transactionSuccess());
    }

    @Override
    public Optional<Boolean> deposit(BigDecimal amount, OfflinePlayer p, String world) {
        return Optional.of(economy.depositPlayer(p, world, amount.doubleValue()).transactionSuccess());
    }

    @Override
    public Optional<Boolean> withdraw(BigDecimal amount, OfflinePlayer p) {
        return Optional.of(economy.withdrawPlayer(p, amount.doubleValue()).transactionSuccess());
    }

    @Override
    public Optional<Boolean> withdraw(BigDecimal amount, OfflinePlayer p, String world) {
        return Optional.of(economy.withdrawPlayer(p, world, amount.doubleValue()).transactionSuccess());
    }

    @Override
    public Optional<Double> accountBalance(OfflinePlayer p) {
        return Optional.of(economy.bankBalance(p.getUniqueId().toString()).amount);
    }

    @Override
    public Optional<Double> accountBalance(OfflinePlayer p, String world) {
        return accountBalance(p);
    }

    @Override
    public Optional<Boolean> accountHas(BigDecimal amount, OfflinePlayer p) {
        return super.accountHas(amount, p);
    }

    @Override
    public Optional<Boolean> accountHas(BigDecimal amount, OfflinePlayer p, String world) {
        return accountHas(amount, p);
    }

    @Override
    public Optional<Boolean> depositAccount(BigDecimal amount, OfflinePlayer p) {
        return Optional.of(economy.bankDeposit(p.getUniqueId().toString(), amount.doubleValue()).transactionSuccess());
    }

    @Override
    public Optional<Boolean> depositAccount(BigDecimal amount, OfflinePlayer p, String world) {
        return depositAccount(amount, p);
    }

    @Override
    public Optional<Boolean> withdrawAccount(BigDecimal amount, OfflinePlayer p) {
        return Optional.of(economy.bankWithdraw(p.getUniqueId().toString(), amount.doubleValue()).transactionSuccess());
    }

    @Override
    public Optional<Boolean> withdrawAccount(BigDecimal amount, OfflinePlayer p, String world) {
        return withdrawAccount(amount, p);
    }
}
