package com.github.sanctum.labyrinth.gui.printer;

import java.util.function.Consumer;

/**
 * Encapsulate custom data and feed it to an {@link AnvilMenu} to be provided to a player.
 */
public class AnvilBuilder {

	protected final String title;
	protected AnvilMenu gui;
	protected ItemBuilder left;
	protected ItemBuilder right;

	protected AnvilBuilder(String title) {
		this.title = title;

	}

	/**
	 * Build a fresh anvil gui with the provided title to boot.
	 *
	 * @param title The title of the menu.
	 * @return A fresh {@link AnvilMenu} builder.
	 */
	public static AnvilBuilder from(String title) {
		return new AnvilBuilder(title);
	}

	/**
	 * Build the item to be displayed in the {@link AnvilMenu.Slot#INPUT_LEFT}
	 *
	 * @param builderConsumer An item builder with provided inventory click modification.
	 * @return The same anvil gui builder.
	 */
	public AnvilBuilder setLeftItem(Consumer<ItemBuilder> builderConsumer) {
		this.left = ItemBuilder.next();
		builderConsumer.accept(this.left);
		return this;
	}

	/**
	 * Build the item to be displayed in the {@link AnvilMenu.Slot#INPUT_RIGHT}
	 *
	 * @param builderConsumer An item builder with provided inventory click modification.
	 * @return The same anvil gui builder.
	 */
	public AnvilBuilder setRightItem(Consumer<ItemBuilder> builderConsumer) {
		this.right = ItemBuilder.next();
		builderConsumer.accept(this.right);
		return this;
	}

	/**
	 * Finish your builder and convert it into an Anvil GUI object ready for use.
	 *
	 * @return An anvil gui object ready for use.
	 */
	public AnvilMenu get() {
		return new AnvilMenu(title, left, right);
	}

}