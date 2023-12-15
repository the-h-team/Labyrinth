package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.container.Cuboid;
import com.github.sanctum.panther.event.Vent;

public class DefaultFlag extends Cuboid.Flag {

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
			return new DefaultFlag(this.id);
		}

	}


	public DefaultFlag(Cuboid.Flag flag) {
		super(flag);
	}

	public DefaultFlag(String id) {
		super(id);
	}

}
