package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.container.Region;

public class RegionTraverseEvent extends DefaultEvent {

	private final Region.Resident resident;

	public RegionTraverseEvent(Region.Resident resident) {
		this.resident = resident;
	}

	public Region.Resident getResident() {
		return resident;
	}

	public Region getRegion() {
		return resident.getRegion().get(); // TODO: Try to throw explicitly or call #orElse(null) + update method nullity
	}

}
