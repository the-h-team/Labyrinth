package com.github.sanctum.labyrinth.gui.printer;

import java.util.function.Consumer;
import org.bukkit.entity.Player;

public class AnvilBuilder {

	protected final Player player;
	protected final String title;
	protected AnvilMenu gui;
	protected ItemBuilder left;
	protected ItemBuilder right;

	protected AnvilBuilder(Player player, String title) {
		this.player = player;
		this.title = title;

	}

	public static AnvilBuilder from(Player player, String title) {
		return new AnvilBuilder(player, title);
	}

	public AnvilBuilder setLeftItem(Consumer<ItemBuilder> builderConsumer) {
		this.left = ItemBuilder.next();
		builderConsumer.accept(this.left);
		return this;
	}

	public AnvilBuilder setRightItem(Consumer<ItemBuilder> builderConsumer) {
		this.right = ItemBuilder.next();
		builderConsumer.accept(this.right);
		return this;
	}

	public AnvilMenu get() {
		return new AnvilMenu(player, title, left, right);
	}

}
