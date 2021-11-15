package com.github.sanctum.labyrinth.paste.option;

/**
 * An object used for delegating expiration periods.
 */
public enum Expiration {

	NEVER("N"),
	TEN_MINUTE("10M"),
	ONE_HOUR("1H"),
	ONE_DAY("1D"),
	ONE_WEEK("1W"),
	TWO_WEEK("2W"),
	ONE_MONTH("1M"),
	SIX_MONTH("6M"),
	ONE_YEAR("1Y");

	private final String id;

	Expiration(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return id;
	}
}
