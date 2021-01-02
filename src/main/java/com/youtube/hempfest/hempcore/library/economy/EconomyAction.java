package com.youtube.hempfest.hempcore.library.economy;

public class EconomyAction {

	private double amount;

	private final boolean success;

	private final String info;

	public EconomyAction(double amount, boolean success, String info) {
		this.amount = amount;
		this.success = success;
		this.info = info;
	}

	public EconomyAction(boolean success, String info) {
		this.success = success;
		this.info = info;
	}

	public boolean isSuccess() {
		return success;
	}

	public double getAmount() {
		return amount;
	}

	public String getInfo() {
		return info;
	}
}
