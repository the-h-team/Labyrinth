package com.github.sanctum.labyrinth.library;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class HFEncoded {

	private Object obj;

	private String objSerial;

	/**
	 * Convert the entire object into a string while retaining all of its values.
	 *
	 * <p>WARNING: Making changes to objects then attempting to attach/reuse older un-modified obejcts
	 * could have negative effects, ensure you have proper object handling when dealing with serialization.</p>
	 *
	 * @param obj The Java Serializable implemented object to convert.
	 */
	public HFEncoded(Object obj) {
		this.obj = obj;
	}

	/**
	 * Convert a serialized object from its string form back into an object of desired type
	 * while retaining all originally saved values.
	 *
	 * <p>WARNING: Making changes to objects then attempting to attach/reuse older un-modified obejcts
	 * could have negative effects, ensure you have proper object handling when dealing with serialization.</p>
	 *
	 * @param objSerial The serialized object string to convert.
	 */
	public HFEncoded(String objSerial) {
		this.objSerial = objSerial;
	}

	/**
	 * Convert the object into a byte array using base 64 encryption.
	 *
	 * @return The inputted object as a byte array.
	 */
	public byte[] toByteArray() {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(output);
			outputStream.writeObject(obj);
			outputStream.flush();
			return output.toByteArray();
		} catch (IOException e) {
			return new byte[0];
		}
	}

	/**
	 * The original stored object retaining all values converted to a string.
	 *
	 * @return Get's a serialized hash for this object with retained values.
	 * @throws IOException Throw's an exception if Bukkit cannot serialize the object due to no
	 *                     inheritance of the java.io Serializable interface.
	 */
	public String serialize() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(output);
		outputStream.writeObject(obj);
		outputStream.flush();

		byte[] serial = output.toByteArray();
		return Base64.getEncoder().encodeToString(serial);
	}

	/**
	 * The original stored object retaining all values converted back to an object.
	 *
	 * <p>WARN: You will need to pass a type to the object upon use.</p>
	 *
	 * @return Get's an object back from a serialized string with all original values.
	 * @throws IOException            Throw's IO if something had been modified in comparison to
	 *                                it's original methods/class structure.
	 * @throws ClassNotFoundException Throw's exception if the class was unable to be
	 *                                located properly.
	 */
	public Object deserialized() throws IOException, ClassNotFoundException {
		byte[] serial = Base64.getDecoder().decode(objSerial);
		ByteArrayInputStream input = new ByteArrayInputStream(serial);
		BukkitObjectInputStream inputStream = new BukkitObjectInputStream(input);
		return inputStream.readObject();
	}

	/**
	 * Deserialize an object of specified type from a string.
	 * <p>
	 * Primarily for misc use, deserialization is handled internally for normal object use from containers.
	 *
	 * @param type The type this object represents.
	 * @param <R>  The type this object represents
	 * @return The deserialized object otherwise null.
	 */
	public <R> R deserialize(Class<R> type) {
		try {
			Object o = deserialized();
			if (type.isAssignableFrom(o.getClass())) {
				return (R) o;
			} else {
				throw new IllegalArgumentException(o.getClass().getSimpleName() + " is not assignable from " + type.getSimpleName());
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
