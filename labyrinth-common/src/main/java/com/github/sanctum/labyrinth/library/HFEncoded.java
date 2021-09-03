package com.github.sanctum.labyrinth.library;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.util.Base64;

@SuppressWarnings("RedundantThrows")
public class HFEncoded {

	private Object obj;

	private String objSerial;

	/**
	 * Convert the entire object into a string while retaining all of its values.
	 *
	 * <p>WARNING: Making changes to objects then attempting to attach/reuse older un-modified objects
	 * could have negative effects, ensure you have proper object handling when dealing with serialization.</p>
	 *
	 * @param obj a Serializable object (Java Serializable and/or Bukkit's ConfigurationSerializable)
	 */
	public HFEncoded(Object obj) {
		this.obj = obj;
	}

	/**
	 * Convert a serialized object from its string form back into an object of desired type
	 * while retaining all originally saved values.
	 *
	 * <p>WARNING: Making changes to objects then attempting to attach/reuse older un-modified objects
	 * could have negative effects, ensure you have proper object handling when dealing with serialization.</p>
	 *
	 * @param objSerial The serialized object string to convert
	 */
	public HFEncoded(String objSerial) {
		this.objSerial = objSerial;
	}

	/**
	 * Convert the object into a byte array using base 64 encryption.
	 *
	 * @return The inputted object as a byte array
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
	 * @return a serialized, encoded form of this object with retained values
	 * @throws NotSerializableException if unable to write the object
	 */
	public String serialize() throws NotSerializableException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(output);
			outputStream.writeObject(obj);
			outputStream.flush();
		} catch (IOException e) {
			throw new IllegalStateException("This should never happen", e);
		}

		byte[] serial = output.toByteArray();
		return Base64.getEncoder().encodeToString(serial);
	}

	/**
	 * The original stored object retaining all values converted back to an object.
	 * <p>
	 * WARN: You will need to pass a type to the object upon use.
	 *
	 * @return the deserialized form of an object with its original state
	 * @throws IOException typically, if a class has been modified in comparison
	 * to its original structure
	 * @throws ClassNotFoundException if the class could not be located properly
	 */
	public Object deserialized() throws IOException, ClassNotFoundException {
		byte[] serial = Base64.getDecoder().decode(objSerial);
		ByteArrayInputStream input = new ByteArrayInputStream(serial);
		BukkitObjectInputStream inputStream = new BukkitObjectInputStream(input);
		return inputStream.readObject();
	}

	// TODO: Rethrow IO/ClassNotFound as checked exception(s) to remove nullity
	/**
	 * Deserialize an object of specified type from a string.
	 * <p>
	 * Primarily for misc use, deserialization is handled internally for normal object use from containers.
	 *
	 * @param type the type this object represents
	 * @param <R> the type this object represents
	 * @return a deserialized object or null
	 */
	public <R> @Nullable R deserialize(Class<R> type) {
		try {
			Object o = deserialized();
			if (o == null) return null;
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
