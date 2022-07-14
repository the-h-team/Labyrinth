package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.event.DefaultEvent;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherList;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;

public class ActionBarEvent extends DefaultEvent {

	final static PantherCollection<ActionbarInstance> instances = new PantherList<>();
	final org.bukkit.entity.Player player;
	BaseComponent[] text;
	long repetition;

	public ActionBarEvent(@NotNull BaseComponent[] text, @NotNull org.bukkit.entity.Player holder, long repetition) {
		this.player = holder;
		this.repetition = repetition;
		this.text = text;
	}

	public boolean isRepeatable() {
		return repetition > -1;
	}

	public void setText(@NotNull BaseComponent... components) {
		this.text = components;
	}

	public void setRepetition(long repetition) {
		this.repetition = repetition;
	}

	public long getRepetition() {
		return repetition;
	}

	public BaseComponent[] getText() {
		return text;
	}

	public org.bukkit.entity.Player getPlayer() {
		return player;
	}

	public void refactor() {
		ActionbarInstance.of(player).refactor().deploy();
	}
}
