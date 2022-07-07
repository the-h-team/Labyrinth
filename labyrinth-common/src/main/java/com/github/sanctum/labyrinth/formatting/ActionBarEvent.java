package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthList;
import com.github.sanctum.labyrinth.event.custom.Vent;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionBarEvent extends Vent {

	final static LabyrinthCollection<ActionbarInstance> instances = new LabyrinthList<>();
	final Player player;
	BaseComponent[] text;
	long repetition;

	public ActionBarEvent(@NotNull BaseComponent[] text, @NotNull Player holder, long repetition) {
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

	public Player getPlayer() {
		return player;
	}

	public void refactor() {
		ActionbarInstance.of(player).refactor().deploy();
	}
}
