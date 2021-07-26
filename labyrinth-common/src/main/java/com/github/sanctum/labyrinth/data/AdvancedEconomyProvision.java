package com.github.sanctum.labyrinth.data;

import com.github.sanctum.economy.construct.account.permissive.AccountType;
import com.github.sanctum.economy.construct.implement.AdvancedEconomy;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Optional;

final class AdvancedEconomyProvision extends EconomyProvision {
    private final AdvancedEconomy provider;

    AdvancedEconomyProvision(@NotNull AdvancedEconomy provider) {
        this.provider = provider;
        EconomyProvision.enterprise = this;
    }

    @Override
    public String getImplementation() {
        return "(Internal) | Interface: Enterprise";
    }

    @Override
    public Optional<Double> balance(OfflinePlayer p) {
        return Optional.ofNullable(provider.getWallet(p).getBalance()).map(BigDecimal::doubleValue);
    }

    @Override
    public Optional<Double> balance(OfflinePlayer p, String world) {
        return Optional.ofNullable(provider.getWallet(p).getBalance(world)).map(BigDecimal::doubleValue);
    }

    @Override
    public Optional<Boolean> has(BigDecimal amount, OfflinePlayer p) {
        return Optional.of(provider.getWallet(p).has(amount));
    }

    @Override
    public Optional<Boolean> has(BigDecimal amount, OfflinePlayer p, String world) {
        return Optional.of(provider.getWallet(p).has(amount, world));
    }

    @Override
    public Optional<Boolean> deposit(BigDecimal amount, OfflinePlayer p) {
        return Optional.of(provider.getWallet(p).deposit(amount).isSuccess());
    }

    @Override
    public Optional<Boolean> deposit(BigDecimal amount, OfflinePlayer p, String world) {
        return Optional.of(provider.getWallet(p).deposit(amount, world).isSuccess());
    }

    @Override
    public Optional<Boolean> withdraw(BigDecimal amount, OfflinePlayer p) {
        return Optional.of(provider.getWallet(p).withdraw(amount).isSuccess());
    }

    @Override
    public Optional<Boolean> withdraw(BigDecimal amount, OfflinePlayer p, String world) {
        return Optional.of(provider.getWallet(p).withdraw(amount, world).isSuccess());
    }

    @Override
    public Optional<Double> accountBalance(OfflinePlayer p) {
        return Optional.ofNullable(provider.getAccount(p).getBalance()).map(BigDecimal::doubleValue);
    }

    @Override
    public Optional<Double> accountBalance(OfflinePlayer p, String world) {
        return Optional.ofNullable(provider.getAccount(p).getBalance(world)).map(BigDecimal::doubleValue);
    }

    @Override
    public Optional<Boolean> accountHas(BigDecimal amount, OfflinePlayer p) {
        return Optional.of(provider.getAccount(p, AccountType.BANK_ACCOUNT).has(amount));
    }

    @Override
    public Optional<Boolean> accountHas(BigDecimal amount, OfflinePlayer p, String world) {
        return Optional.of(provider.getAccount(p, AccountType.BANK_ACCOUNT).has(amount, world));
    }

    @Override
    public Optional<Boolean> depositAccount(BigDecimal amount, OfflinePlayer p) {
        return Optional.of(provider.getAccount(p, AccountType.BANK_ACCOUNT).deposit(amount).isSuccess());
    }

    @Override
    public Optional<Boolean> depositAccount(BigDecimal amount, OfflinePlayer p, String world) {
        return Optional.of(provider.getAccount(p, AccountType.BANK_ACCOUNT).deposit(amount, world).isSuccess());
    }

    @Override
    public Optional<Boolean> withdrawAccount(BigDecimal amount, OfflinePlayer p) {
        return Optional.of(provider.getAccount(p, AccountType.BANK_ACCOUNT).withdraw(amount).isSuccess());
    }

    @Override
    public Optional<Boolean> withdrawAccount(BigDecimal amount, OfflinePlayer p, String world) {
        return Optional.of(provider.getAccount(p, AccountType.BANK_ACCOUNT).withdraw(amount, world).isSuccess());
    }
}
