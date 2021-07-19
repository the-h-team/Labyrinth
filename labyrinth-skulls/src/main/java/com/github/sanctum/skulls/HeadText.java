package com.github.sanctum.skulls;

final class HeadText {

	private final String name;

	private final String category;

	protected HeadText(String name, String category) {
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
