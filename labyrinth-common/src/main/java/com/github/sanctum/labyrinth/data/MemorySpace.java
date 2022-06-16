package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.annotation.See;
import com.github.sanctum.labyrinth.data.container.LabyrinthAtlas;
import com.github.sanctum.labyrinth.data.container.LabyrinthEntryMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;
import com.github.sanctum.labyrinth.interfacing.JsonIntermediate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An object holding multiple possible nodes or objects full of their own respective data whether it be more nodes or objects.
 *
 * @author Hempfest
 * @version 1.0
 */
@See({Node.class, Configurable.class, Atlas.class, LabyrinthAtlas.class})
public interface MemorySpace {

	/**
	 * @return The full path for this memory space.
	 */
	String getPath();

	/**
	 * Checks if a node exists under the specified name space.
	 *
	 * @param key the name space.
	 * @return false if the specified name space is non existent or equal to an object not a node otherwise true
	 */
	boolean isNode(String key);

	/**
	 * Get's a child node to this current node under the specified name space.
	 *
	 * <p>Nodes of nodes will automatically append each others key paths</p>
	 *
	 * @param key the name space.
	 * @return The existing node or a new one depending on implementation.
	 * Default {@link Configurable} usage provides fresh nodes if non existent.
	 */
	Node getNode(String key);

	/**
	 * Get the name spaces behind every object within this memory space.
	 *
	 * @param deep Whether to look through multiple layers or not.
	 * @return All data keys within this memory space.
	 */
	Set<String> getKeys(boolean deep);

	/**
	 * Get the values within this memory space.
	 *
	 * @param deep Whether to look through multiple layers or not.
	 * @return All data within this memory space.
	 */
	Map<String, Object> getValues(boolean deep);

	/**
	 * Convert a map into a usable read-only memory space.
	 *
	 * @param map The map to convert.
	 * @return The copied memory space.
	 */
	static @NotNull MemorySpace wrapNonLinked(@NotNull Map<String, Object> map) {
		AtlasMap copied = new AtlasMap();
		copied.putAll(map);
		return copied;
	}

	/**
	 * Convert a known json object from string form into a usable read-only memory space.
	 *
	 * @param json The string to convert.
	 * @return The copied memory space.
	 */
	static @NotNull MemorySpace wrapNonLinked(@NotNull @Json String json) {
		return wrapNonLinked(JsonIntermediate.convertToMap(JsonIntermediate.toJsonObject(json)));
	}

}
