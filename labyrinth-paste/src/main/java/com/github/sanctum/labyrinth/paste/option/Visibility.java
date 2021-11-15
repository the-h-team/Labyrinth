package com.github.sanctum.labyrinth.paste.option;

/**
 * An object describing result visibility
 */
public enum Visibility {

	PUBLIC(0),
	UNLISTED(1),
	PRIVATE(2);

	private final int id;

	Visibility(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return String.valueOf(id);
	}
}
