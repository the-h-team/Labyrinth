package com.github.sanctum.labyrinth.formatting.component;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.library.Applicable;
import com.github.sanctum.panther.util.HUID;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Encapsulate text component information and attach applicable data to it.
 *
 * @author Hempfest
 */
public class WrappedComponent implements ActionComponent {

	private final TextComponent component;

	private final HUID commandSerial;

	private boolean marked;

	private Applicable action;

	public WrappedComponent(TextComponent component) {
		this.component = component;
		this.commandSerial = HUID.randomID();
		this.component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + getId()));
		LabyrinthProvider.getInstance().registerComponent(this).deploy();
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

	@Override
	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	@Override
	public boolean isMarked() {
		return marked;
	}

	/**
	 * Get the command line serial as a string.
	 *
	 * @return the id of the wrapper
	 */
	@Override
	public String getId() {
		return commandSerial.toString();
	}

	@Override
	public Applicable action() {
		return action != null ? action : () -> {
		};
	}

	public TextComponent toReal() {
		return component;
	}

	@Override
	public void remove() {
		LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> LabyrinthProvider.getInstance().removeComponent(this).deploy(), toString() + "-removal",5);
	}

}
