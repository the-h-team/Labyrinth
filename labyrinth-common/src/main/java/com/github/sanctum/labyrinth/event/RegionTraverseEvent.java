package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.Region;
import com.github.sanctum.labyrinth.event.custom.Vent;

public class RegionTraverseEvent extends Vent {

	private final Region.Resident resident;

	public RegionTraverseEvent(Region.Resident resident) {
		this.resident = resident;
	}

	public Region.Resident getResident() {
		return resident;
	}

	public Region getRegion() {
		return resident.getRegion().get();
	}

}
