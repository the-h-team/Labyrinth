package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.library.Cuboid;
import org.bukkit.plugin.Plugin;

public class RegionFlag extends Cuboid.Flag {

	public static class Builder {

		private final Plugin plugin;
		private Vent.Subscription<?> subscription;
		private String id;
		private String message;

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

		public Builder receive(String message) {
			this.message = message;
			return this;
		}

		public Cuboid.Flag finish() {
			//Schedule.sync(() -> RegionServicesManager.getInstance().load(service)).wait(1);
			Vent.subscribe(subscription);
			return new RegionFlag(this.plugin, this.id, this.message);
		}

	}


	public RegionFlag(Cuboid.Flag flag) {
		super(flag);
	}

	public RegionFlag(Plugin plugin, String id, String message) {
		super(plugin, id, message);
	}

	@SuppressWarnings("UnusedReturnValue")
	public RegionFlag setMessage(String message) {
		this.message = message;
		return this;
	}

}
