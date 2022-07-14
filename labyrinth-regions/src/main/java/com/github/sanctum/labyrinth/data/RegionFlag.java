package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.panther.event.Vent;

public class RegionFlag extends Cuboid.Flag {

	public static class Builder {
		private Vent.Subscription<?> subscription;
		private String id;

		protected Builder() {

		}

		public static Builder initialize() {
			return new Builder();
		}

		public Builder label(String id) {
			this.id = id;
			return this;
		}

		public Builder envelope(Vent.Subscription<?> subscription) {
			this.subscription = subscription;
			return this;
		}

		public Cuboid.Flag finish() {
			if (this.subscription != null) {
				LabyrinthProvider.getInstance().getEventMap().subscribe(subscription);
			}
			return new RegionFlag(this.id);
		}

	}


	public RegionFlag(Cuboid.Flag flag) {
		super(flag);
	}

	public RegionFlag(String id) {
		super(id);
	}

}
