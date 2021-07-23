package com.github.sanctum.labyrinth.formatting.component;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.library.Applicable;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.task.Schedule;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Encapsulate text component information and attach applicable data to it.
 *
 * @author Hempfest
 */
public class WrappedComponent {

	private final TextComponent component;

	private final HUID commandSerial;

	private boolean marked;

	private Applicable action;

	public WrappedComponent(TextComponent component) {
		this.component = component;
		this.commandSerial = HUID.randomID();
		this.component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + toString()));
		LabyrinthProvider.getInstance().getComponents().add(this);
	}

	/**
	 * Inject code to be run when this object is interacted with.
	 *
	 * @param action the information to read on interaction
	 * @return this wrapped component
	 */
	public WrappedComponent accept(Applicable action) {
		this.action = action;
		return this;
	}

	public WrappedComponent setMarked(boolean marked) {
		this.marked = marked;
		return this;
	}

	public boolean isMarked() {
		return marked;
	}

	/**
	 * Get the command line serial as a string.
	 *
	 * @return the id of the wrapper
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
		Schedule.sync(() -> LabyrinthProvider.getInstance().getComponents().remove(this)).wait(1);
	}

}
