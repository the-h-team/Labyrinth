package com.youtube.hempfest.hempcore.library.economy;

import java.util.Arrays;
import java.util.List;

public class BankAccount {

	private final String accountID;

	private final double balance;

	private final String world;

	private final List<String> members;

	public BankAccount(String accountID, double balance, String world, String... members) {
		this.accountID = accountID;
		this.balance = balance;
		this.world = world;
		this.members = Arrays.asList(members);
	}

	public double getBalance() {
		return balance;
	}

	public String getWorld() {
		return world;
	}

	public String getAccountID() {
		return accountID;
	}

	public List<String> getMembers() {
		return members;
	}
}
