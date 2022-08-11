package com.github.sanctum.labyrinth.formatting.component;

import com.github.sanctum.labyrinth.formatting.ToolTip;
import com.github.sanctum.panther.util.Applicable;

public interface ActionComponent {

	Applicable action();

	boolean isMarked();

	void setMarked(boolean marked);

	void remove();

	String getId();

	default boolean isTooltip() {
		return this instanceof ToolTip;
	}

}
