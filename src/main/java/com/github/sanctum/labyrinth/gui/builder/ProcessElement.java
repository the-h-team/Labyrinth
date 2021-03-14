package com.github.sanctum.labyrinth.gui.builder;

import java.util.function.Consumer;

/**
 * An object used to specify custom logic to elements used from the provided collection in the menu builder.
 */
public class ProcessElement {

	private SyncMenuItemPreProcessEvent event;

	private SyncMenuSwitchPageEvent e;

	protected ProcessElement(SyncMenuItemPreProcessEvent event) {
		this.event = event;
	}

	protected ProcessElement(SyncMenuSwitchPageEvent event) {
		this.e = event;
	}

	/**
	 * Create a lambda expression to formulate and customize primary menu elements.
	 *
	 * @param syncMenuItemPreProcessEventConsumer The item pre-process event.
	 */
	public void applyLogic(Consumer<SyncMenuItemPreProcessEvent> syncMenuItemPreProcessEventConsumer) {
		syncMenuItemPreProcessEventConsumer.accept(event);
	}

	/**
	 * Create a lambda expression to formulate and customize successful page switches.
	 *
	 * @param syncMenuSwitchPageEventConsumer The item pre-process event.
	 */
	public void applyRunningLogic(Consumer<SyncMenuSwitchPageEvent> syncMenuSwitchPageEventConsumer) {
		syncMenuSwitchPageEventConsumer.accept(e);
	}

}
