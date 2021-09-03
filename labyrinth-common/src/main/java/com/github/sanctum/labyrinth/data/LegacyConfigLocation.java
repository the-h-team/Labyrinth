package com.github.sanctum.labyrinth.data;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

/**
 * Serialize and read a {@link Location} in older versions.
 *
 * @author ms5984
 */
@SerializableAs("org.bukkit.Location")
public final class LegacyConfigLocation implements ConfigurationSerializable {
	private final Location location;

	/**
	 * Wrap an existing location.
	 * <p>
	 * <strong>Does not clone!</strong>
	 *
	 * @param location a live Bukkit location
	 */
	public LegacyConfigLocation(@NotNull Location location) {
		this.location = location;
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
		final World world = location.getWorld();
		if (world != null) {
			builder.put("world", world.getName());
		}
		builder.put("x", location.getX());
		builder.put("y", location.getY());
		builder.put("z", location.getZ());
		builder.put("pitch", location.getPitch());
		builder.put("yaw", location.getYaw());
		return builder.build();
	}

	/**
	 * Get a clone of the internal Location object.
	 *
	 * @return a clone of the internal Location object
	 */
	public final Location getLocation() {
		return location.clone();
	}

	/**
	 * Parse a LegacyConfigLocation from config.
	 * <p>
	 * Pitch and yaw may be omitted; these values will initialize to 0f.
	 *
	 * @param map map of values
	 * @return a new LegacyConfigLocation with the deserialized state
	 * @throws IllegalArgumentException if any of x, y, or z cannot be resolved
	 * @implNote This implementation will attempt to resolve the config-stored
	 * world name into a live Bukkit world; if this process fails a valid
	 * World will be resolved in the following manner:
	 * <pre>
	 *     Bukkit.getWorld().get(0); // Get the first world
	 * </pre>
	 */
	public static LegacyConfigLocation deserialize(Map<String, Object> map) throws IllegalArgumentException {
		double x, y, z;
		float pitch, yaw;
		final World world = Optional.ofNullable(map.get("world"))
				.filter(String.class::isInstance)
				.map(String.class::cast)
				.map(Bukkit::getWorld)
				.orElse(Bukkit.getWorlds().get(0));
		x = Optional.ofNullable(map.get("x"))
				.map(LegacyConfigLocation::tryDouble)
				.orElseThrow(() -> new IllegalArgumentException("X cannot be omitted!"));
		y = Optional.ofNullable(map.get("y"))
				.map(LegacyConfigLocation::tryDouble)
				.orElseThrow(() -> new IllegalArgumentException("Y cannot be omitted!"));
		z = Optional.ofNullable(map.get("z"))
				.map(LegacyConfigLocation::tryDouble)
				.orElseThrow(() -> new IllegalArgumentException("Z cannot be omitted!"));
		pitch = Optional.ofNullable(map.get("pitch"))
				.map(LegacyConfigLocation::tryFloat)
				.orElse(0f);
		yaw = Optional.ofNullable(map.get("yaw"))
				.map(LegacyConfigLocation::tryFloat)
				.orElse(0f);
		return new LegacyConfigLocation(new Location(world, x, y, z, yaw, pitch));
	}

	private static Double tryDouble(Object object) {
		try {
			return (double) object;
		} catch (ClassCastException ignored) {
			return null;
		}
	}

	private static Float tryFloat(Object object) {
		try {
			return (float) object;
		} catch (ClassCastException ignored) {
			return null;
		}
	}
}
