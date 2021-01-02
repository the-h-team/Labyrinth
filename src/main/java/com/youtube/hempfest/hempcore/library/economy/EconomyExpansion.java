package com.youtube.hempfest.hempcore.library.economy;

import java.util.List;
import java.util.UUID;
import org.bukkit.plugin.Plugin;

public interface EconomyExpansion {

	Plugin getPlugin();

	String getVersion();

	String format(double amount);

	String currencyPlural();

	String currencySingular();

    boolean isEnabled();

    boolean isInfiniteWorlds();

    boolean hasBankSupport();
    
    boolean infiniteBankAccounts();

    boolean hasPlayerAccount(String name);

    boolean hasPlayerAccount(String name, String world);

    boolean hasBankAccount(String name);

    boolean hasBankAccount(String name, String world);

	boolean hasBankAccount(UUID uuid);

	boolean hasBankAccount(UUID uuid, String world);

	boolean entityHasAmount(String name, double amount);

	boolean entityHasAmount(String name, String world, double amount);

	boolean entityHasAmount(UUID uuid, double amount);

	boolean entityHasAmount(UUID uuid, String world, double amount);

	boolean bankHasAmount(String accountID, double amount);

	boolean bankHasAmount(String accountID, String world, double amount);
	
	double getEntityBalance(String name);
	
	double getEntityBalance(String name, String world);

	double getEntityBalance(UUID uuid);

	double getEntityBalance(UUID uuid, String world);
	
	double getBankBalance(String accountID);
	
	double getBankBalance(String accountID, String world);
	
	BankAccount getBankAccount(String name);

	BankAccount getBankAccount(String name, String world);

	BankAccount getBankAccount(String name, String accountType, String world);

	BankAccount getBankAccount(UUID uuid);

	BankAccount getBankAccount(UUID uuid, String world);

	BankAccount getBankAccount(UUID uuid, String accountType, String world);

	EconomyAction entityWithdraw(String name, double amount);

	EconomyAction entityWithdraw(String name, String world, double amount);

	EconomyAction entityWithdraw(UUID uuid, double amount);

	EconomyAction entityWithdraw(UUID uuid, String world, double amount);

	EconomyAction bankWithdraw(String accountID, double amount);

	EconomyAction bankWithdraw(String accountID, String world, double amount);

	EconomyAction entityDeposit(String name, double amount);

	EconomyAction entityDeposit(String name, String world, double amount);

	EconomyAction entityDeposit(UUID uuid, double amount);

	EconomyAction entityDeposit(UUID uuid, String world, double amount);

	EconomyAction bankDeposit(String accountID, double amount);

	EconomyAction bankDeposit(String accountID, String world, double amount);

	EconomyAction createAccount(AccountType type);

	EconomyAction createAccount(AccountType type, double amount);

	EconomyAction createAccount(AccountType type, String world);

	EconomyAction createAccount(AccountType type, String world, double amount);

	EconomyAction createAccount(AccountType type, double amount, String accountType);

	EconomyAction createAccount(AccountType type, String world, String accountType);

	EconomyAction createAccount(AccountType type, String world, double amount, String accountType);

	EconomyAction deleteEntityAccount(String name);

	EconomyAction deleteEntityAccount(String name, String world);

	EconomyAction deleteEntityAccount(UUID uuid);

	EconomyAction deleteEntityAccount(UUID uuid, String world);

	EconomyAction deleteBankAccount(String accountID);

	EconomyAction deleteBankAccount(String accountID, String world);

	EconomyAction isBankOwner(String name, String accountID);

	EconomyAction isBankOwner(String name, String accountID, String world);

	EconomyAction isBankOwner(UUID uuid, String accountID);

	EconomyAction isBankOwner(UUID uuid, String accountID, String world);

	EconomyAction isBankMember(String name, String accountID);

	EconomyAction isBankMember(String name, String accountID, String world);

	EconomyAction isBankMember(UUID uuid, String accountID);

	EconomyAction isBankMember(UUID uuid, String accountID, String world);

	List<BankAccount> getBankAccounts();

	List<String> getBanks();

}
