package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.library.Cuboid;

public class CuboidCreationEvent extends CuboidSelectEvent {

	public CuboidCreationEvent(Cuboid.Selection selection) {
		super(selection);
	}


}
