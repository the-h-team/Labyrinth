package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.annotation.NodePointer;
import java.util.List;
import java.util.Map;

/**
 * An object that encapsulates data storage services.
 * Save, delete, read or modify existing data in multi-dimensional spaces.
 *
 * @author Hempfest
 * @version 1.0
 */
public interface Node extends MemorySpace, Removable {

	/**
	 * Get the object attached to this node if present.
	 *
	 * @return The object under this node or null.
	 */
	Object get();

	/**
	 * Get the string attached to this node if present.
	 *
	 * @return The string under this node or null.
	 */
	String getString();

	/**
	 * Get the int attached to this node if present.
	 *
	 * @return The int under this node or 0.
	 */
	int getInt();

	/**
	 * Get the boolean attached to this node if present.
	 *
	 * @return The boolean under this node or false.
	 */
	boolean getBoolean();

	/**
	 * Get the double attached to this node if present.
	 *
	 * @return The double under this node or 0.0.
	 */
	double getDouble();

	/**
	 * Get the float attached to this node if present.
	 *
	 * @return The float under this node or 0.0f.
	 */
	float getFloat();

	/**
	 * Get the long attached to this node if present.
	 *
	 * @return The long under this node or 0L.
	 */
	long getLong();

	/**
	 * Get the list attached to this node if present.
	 *
	 * @return The list under this node or a new empty one.
	 */
	List<?> getList();

	/**
	 * Get the map attached to this node if present.
	 *
	 * @return The map under this node or a new empty one.
	 */
	Map<?, ?> getMap();

	/**
	 * Get the string list attached to this node if present.
	 *
	 * @return The string list under this node or a new empty one.
	 */
	List<String> getStringList();

	/**
	 * Get the int list attached to this node if present.
	 *
	 * @return The int list under this node or a new empty one.
	 */
	List<Integer> getIntegerList();

	/**
	 * Get the double list attached to this node if present.
	 *
	 * @return The double list under this node or a new empty one.
	 */
	List<Double> getDoubleList();

	/**
	 * Get the float list attached to this node if present.
	 *
	 * @return The float list under this node or a new empty one.
	 */
	List<Float> getFloatList();

	/**
	 * Get the long list attached to this node if present.
	 *
	 * @return The long list under this node or a new empty one.
	 */
	List<Long> getLongList();

	/**
	 * @return true if the object under this node is a string.
	 */
	boolean isString();

	/**
	 * @return true if the object under this node is a boolean
	 */
	boolean isBoolean();

	/**
	 * @return true if the object under this node is an integer.
	 */
	boolean isInt();

	/**
	 * @return true if the object under this node is a double.
	 */
	boolean isDouble();

	/**
	 * @return true if the object under this node is a float.
	 */
	boolean isFloat();

	/**
	 * @return true if the object under this node is a long.
	 */
	boolean isLong();

	/**
	 * @return true if the object under this node is a list.
	 */
	boolean isList();

	/**
	 * @return true if the object under this node is a string list.
	 */
	boolean isStringList();

	/**
	 * @return true if the object under this node is a float list.
	 */
	boolean isFloatList();

	/**
	 * @return true if the object under this node is a double list.
	 */
	boolean isDoubleList();

	/**
	 * @return true if the object under this node is an integer list.
	 */
	boolean isIntegerList();

	/**
	 * @return true if the object under this node is a long list.
	 */
	boolean isLongList();

	/**
	 * @return The full key for this node.
	 */
	String getName();

	/**
	 * @return The configuration this node belongs to.
	 */
	Configurable getParent();

	/**
	 * If the result of this node ends in an object instead of another node parse its type here.
	 *
	 * @param type The object type.
	 * @param <T>  The type.
	 * @return The object at the end of this node parsed to a desirable type.
	 */
	<T> T get(Class<T> type);

	/**
	 * Set the object that this node represents.
	 *
	 * <p>If the existing node is a section and not an object modifying this results in a great chance of unwanted data removal</p>
	 *
	 * <p>All objects being saved must be either a known primitive or {@link NodePointer} type</p>
	 *
	 * @param o The object to add.
	 */
	void set(Object o);

	/**
	 * Convert all known data from this node to json text.
	 *
	 * @return The data from this node converted to JSON text.
	 */
	String toJson();

}
