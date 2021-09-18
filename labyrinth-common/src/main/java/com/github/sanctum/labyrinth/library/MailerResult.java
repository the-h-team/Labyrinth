package com.github.sanctum.labyrinth.library;

import org.bukkit.command.CommandSender;

class MailerResult {

	private final boolean isPlayer;
	private final Object source;

	public MailerResult(Object source) {
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
