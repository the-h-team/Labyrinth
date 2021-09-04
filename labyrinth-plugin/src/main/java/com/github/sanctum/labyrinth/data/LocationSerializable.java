package com.github.sanctum.labyrinth.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@NodePointer("org.bukkit.Location")
public class LocationSerializable implements JsonAdapter<Location> {

	@Override
	public JsonElement write(Location l) {
		JsonObject o = new JsonObject();
		o.addProperty("x", l.getX());
		o.addProperty("y", l.getY());
		o.addProperty("z", l.getZ());
		o.addProperty("yaw", l.getYaw());
		o.addProperty("pitch", l.getPitch());
		o.addProperty("world", l.getWorld().getName());
		return o;
	}

	@Override
	public Location read(Map<String, Object> o) {
		String world;
		double x, y, z;
		float yaw, pitch;
		world = (String) o.get("world");
		x = (double) o.get("x");
		y = (double) o.get("y");
		z = (double) o.get("z");
		yaw = (float) (double) o.get("yaw");
		pitch = (float) (double) o.get("pitch");
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}

	@Override
	public Class<Location> getClassType() {
		return Location.class;
	}
}
