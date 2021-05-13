package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;

public class RegionFlag extends Cuboid.Flag {

	public static class Builder {

		private final Plugin plugin;
		private RegionService service;
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

		public Builder envelope(Consumer<RegionService> serviceConsumer) {
			serviceConsumer.accept(new RegionService() {

			});
			return this;
		}

		public Builder envelope(RegionService service) {
			this.service = service;
			return this;
		}

		public Builder receive(String message) {
			this.message = message;
			return this;
		}

		public Cuboid.Flag finish() {
			Schedule.sync(() -> RegionServicesManager.getInstance().load(service)).wait(1);
			return new RegionFlag(this.plugin, this.id, this.message);
		}

	}


	public RegionFlag(Cuboid.Flag flag) {
		super(flag);
	}

	public RegionFlag(Plugin plugin, String id, String message) {
		super(plugin, id, message);
	}

}
