package com.github.sanctum.labyrinth.common;

import org.bukkit.command.CommandSender;

class MailableTarget {

	private final boolean isPlayer;
	private final Object source;

	public MailableTarget(Object source) {
		isPlayer = CommandSender.class.isAssignableFrom(source.getClass());
		this.source = source;
	}

	public boolean isForPlayer() {
		return isPlayer;
	}

	public Object getSource() {
		return source;
	}
}
