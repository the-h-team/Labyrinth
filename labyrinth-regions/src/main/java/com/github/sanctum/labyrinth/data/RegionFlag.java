package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.library.Cuboid;
import org.bukkit.plugin.Plugin;

public class RegionFlag extends Cuboid.Flag {

	public static class Builder {

		private final Plugin plugin;
		private Vent.Subscription<?> subscription;
		private String id;

		protected Builder(Plugin plugin) {
			this.plugin = plugin;
		}

		public static Builder initialize(Plugin plugin) {
			return new Builder(plugin);
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
			return new RegionFlag(this.plugin, this.id);
		}

	}


	public RegionFlag(Cuboid.Flag flag) {
		super(flag);
	}

	public RegionFlag(Plugin plugin, String id) {
		super(plugin, id);
	}

}
