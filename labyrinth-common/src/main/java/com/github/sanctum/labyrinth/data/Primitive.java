package com.github.sanctum.labyrinth.data;

import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * An object implied to be primitive.
 * <p>With no generics involved use natural parsing for definition.
 * <p>
 * The sole purpose of this object is to act as a delegate for a provided source object.
 * Standard usage of this interface is in tandem with {@link Configurable} abstraction.
 *
 * </p>
 */
public interface Primitive {

	/**
	 * Get the attached string if present.
	 *
	 * @return The string reference or null.
	 */
	String getString();

	/**
	 * Get the attached int if present.
	 *
	 * @return The int reference or 0.
	 */
	int getInt();

	/**
	 * Get the attached boolean if present.
	 *
	 * @return The boolean reference or false.
	 */
	boolean getBoolean();

	/**
	 * Get the attached double if present.
	 *
	 * @return The double reference or 0.0.
	 */
	double getDouble();

	/**
	 * Get the attached float if present.
	 *
	 * @return The float reference or 0.0f.
	 */
	float getFloat();

	/**
	 * Get the attached long if present.
	 *
	 * @return The long reference or 0L.
	 */
	long getLong();

	/**
	 * Get the attached list if present.
	 *
	 * @return The list reference or a new empty one.
	 */
	List<?> getList();

	/**
	 * Get the attached map if present.
	 *
	 * @return The map reference or a new empty one.
	 */
	Map<?, ?> getMap();

	/**
	 * Get the attached string list if present.
	 *
	 * @return The string list reference or a new empty one.
	 */
	List<String> getStringList();

	/**
	 * Get the attached int list if present.
	 *
	 * @return The int list reference or a new empty one.
	 */
	List<Integer> getIntegerList();

	/**
	 * Get the attached double list if present.
	 *
	 * @return The double list reference or a new empty one.
	 */
	List<Double> getDoubleList();

	/**
	 * Get the attached float list if present.
	 *
	 * @return The float list reference or a new empty one.
	 */
	List<Float> getFloatList();

	/**
	 * Get the attached long list if present.
	 *
	 * @return The long list reference or a new empty one.
	 */
	List<Long> getLongList();

	/**
	 * @return true if the object reference is a string.
	 */
	boolean isString();

	/**
	 * @return true if the object reference is a boolean
	 */
	boolean isBoolean();

	/**
	 * @return true if the object reference is an integer.
	 */
	boolean isInt();

	/**
	 * @return true if the object reference is a double.
	 */
	boolean isDouble();

	/**
	 * @return true if the object reference is a float.
	 */
	boolean isFloat();

	/**
	 * @return true if the object reference is a long.
	 */
	boolean isLong();

	/**
	 * @return true if the object reference is a list.
	 */
	boolean isList();

	/**
	 * @return true if the object reference is a string list.
	 */
	boolean isStringList();

	/**
	 * @return true if the object reference is a float list.
	 */
	boolean isFloatList();

	/**
	 * @return true if the object reference is a double list.
	 */
	boolean isDoubleList();

	/**
	 * @return true if the object reference is an integer list.
	 */
	boolean isIntegerList();

	/**
	 * @return true if the object reference is a long list.
	 */
	boolean isLongList();

	/**
	 * An object implied to be of bukkit origin.
	 *
	 * Custom bukkit objects consist of both {@link ItemStack} & {@link Location}
	 *
	 */
	interface Bukkit {

		/**
		 * @return true if the object reference is a location.
		 */
		boolean isLocation();

		/**
		 * @return true if the object reference is an itemstack.
		 */
		boolean isItemStack();

		/**
		 * Get the attached location if present.
		 *
		 * @return The location reference or null
		 */
		Location getLocation();

		/**
		 * Get the attached itemstack if present.
		 *
		 * @return The itemstack reference or null
		 */
		ItemStack getItemStack();

	}
}
