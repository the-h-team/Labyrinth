package com.github.sanctum.labyrinth.library;

public enum WorkbenchSlot {

	ONE(0),
	TWO(1),
	THREE(2),
	FOUR(3),
	FIVE(4),
	SIX(5),
	SEVEN(6),
	EIGHT(7),
	NINE(8),
	TEN(9);

	private final int slot;
	WorkbenchSlot(int slot) {
		this.slot = slot;
	}

	public int toInt() {
		return slot;
	}

}
