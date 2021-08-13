package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import org.bukkit.plugin.Plugin;

public class RegisteredListener {

	private final Object o;

	private final Plugin host;

	public RegisteredListener(Plugin host, Object o) {
		this.o = o;
		this.host = host;
	}

	public Plugin getHost() {
		return host;
	}

	public Object getListener() {
		return o;
	}

	public void remove() {
		LabyrinthProvider.getInstance().getEventMap().unregister(this);
	}

}
