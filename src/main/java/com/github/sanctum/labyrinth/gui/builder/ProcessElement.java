package com.github.sanctum.labyrinth.gui.builder;

import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ProcessElement implements Listener {

	private final PaginatedBuilder builder;

	private final SyncMenuItemPreProcessEvent event;

	protected ProcessElement(PaginatedBuilder builder, SyncMenuItemPreProcessEvent event) {
		this.builder = builder;
		this.event = event;
	}



	public void applyLogic(Consumer<SyncMenuItemPreProcessEvent> syncMenuItemPreProcessEventConsumer) {
		syncMenuItemPreProcessEventConsumer.accept(event);
	}

	public PaginatedBuilder complete() {
		return builder;
	}


}
