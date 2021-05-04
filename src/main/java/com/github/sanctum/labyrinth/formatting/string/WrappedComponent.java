package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.library.Applicable;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.task.Schedule;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Encapsulate text component information and attach applicable data to it.
 */
public class WrappedComponent {

	private final TextComponent component;

	private final HUID commandSerial;

	private Applicable action;

	public WrappedComponent(TextComponent component) {
		this.component = component;
		this.commandSerial = HUID.randomID();
		this.component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + toString()));
		Labyrinth.COMPONENTS.add(this);
	}

	/**
	 * Inject code to be ran when this object is interacted with.
	 *
	 * @param action The information to read on interaction.
	 * @return The same wrapped component.
	 */
	public WrappedComponent accept(Applicable action) {
		this.action = action;
		return this;
	}

	/**
	 * Gets the command line serial as a string.
	 *
	 * @return The id of the wrapper.
	 */
	@Override
	public String toString() {
		return commandSerial.toString();
	}

	public Applicable action() {
		return action != null ? action : () -> {
		};
	}

	public TextComponent toReal() {
		return component;
	}

	public void remove() {
		Schedule.sync(() -> Labyrinth.COMPONENTS.remove(this)).wait(1);
	}

}
