package com.github.sanctum.skulls;

final class HeadContext {

	private final String name;

	private final String category;

	HeadContext(String name, String category) {
		this.name = name;
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}
}
