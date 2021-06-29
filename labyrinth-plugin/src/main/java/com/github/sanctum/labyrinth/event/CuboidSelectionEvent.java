package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.library.Cuboid;

public class CuboidSelectionEvent extends CuboidSelectEvent {

	public CuboidSelectionEvent(Cuboid.Selection selection) {
		super(selection);
	}


}
