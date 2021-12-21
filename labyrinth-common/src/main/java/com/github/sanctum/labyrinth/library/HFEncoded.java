package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.annotation.Removal;
import com.github.sanctum.labyrinth.api.Service;
import java.io.ObjectOutputStream;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.util.Base64;

/**
 * A class for serializing/deserializing object's with support for {@link java.io.Serializable}, {@link org.bukkit.configuration.serialization.ConfigurationSerializable} & {@link com.github.sanctum.labyrinth.data.JsonAdapter}
 */
public class HFEncoded {

	private final Object obj;

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
	@Deprecated
	@Removal(because = "of new delegation", inVersion = "1.7.5")
	public HFEncoded(String objSerial) {
		this.obj = objSerial;
	}

	@Note("Delegate for both serialization transactions")
	public static HFEncoded of(@NotNull Object obj) {
		return new HFEncoded(obj);
	}

	/**
	 * Convert the object into a byte array using base 64 encryption.
	 *
	 * @return The inputted object as a byte array
	 */
	public byte[] toByteArray() {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			LabyrinthObjectOutputStream outputStream = new LabyrinthObjectOutputStream(output);
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
			LabyrinthObjectOutputStream outputStream = new LabyrinthObjectOutputStream(output);
			outputStream.writeObject(obj);
			outputStream.flush();
		} catch (IOException e) {
			throw new IllegalStateException("This should never happen", e);
		}

		byte[] serial = output.toByteArray();
		return Base64.getEncoder().encodeToString(serial);
	}

	/**
	 * If the provided object is an encoded byte array deserialize it back into its object state.
	 *
	 * @return The object the byte array once was or null if not an encoded byte array.
	 * @throws IOException if an I/O error occurs while reading stream heade
	 * @throws ClassNotFoundException if the class of the serialized object cannot be found
	 */
	public <T> T fromByteArray() throws IOException, ClassNotFoundException {
		if (obj == null || !byte[].class.isAssignableFrom(obj.getClass())) return null;
		byte[] ar = (byte[]) obj;
		ByteArrayInputStream input = new ByteArrayInputStream(ar);
		LabyrinthObjectInputStream inputStream = new LabyrinthObjectInputStream(input);
		return (T) inputStream.readObject();

	}

	/**
	 * The original stored object retaining all values converted back to an object.
	 * <p>
	 * WARN: You will need to pass a type to the object upon use.
	 *
	 * @param lookups The individual class initializers to use.
	 * @return the deserialized form of an object with its original state
	 * @throws IOException typically, if a class has been modified in comparison
	 * to its original structure
	 * @throws ClassNotFoundException if the class could not be located properly
	 */
	public Object deserialized(ClassLookup... lookups) throws IOException, ClassNotFoundException {
		byte[] serial = Base64.getDecoder().decode(obj.toString());
		ByteArrayInputStream input = new ByteArrayInputStream(serial);
		LabyrinthObjectInputStream inputStream = new LabyrinthObjectInputStream(input);
		for (ClassLookup l : lookups) {
			inputStream.add(l);
		}
		return inputStream.readObject();
	}

	/**
	 * If the provided object is an encoded byte array deserialize it back into its object state.
	 *
	 * @param lookups The individual class initializers to use.
	 * @return The object the byte array once was or null if not an encoded byte array.
	 * @throws IOException if an I/O error occurs while reading stream heade
	 * @throws ClassNotFoundException if the class of the serialized object cannot be found
	 */
	public <T> T fromByteArray(ClassLookup... lookups) throws IOException, ClassNotFoundException {
		if (obj == null || !byte[].class.isAssignableFrom(obj.getClass())) return null;
		byte[] ar = (byte[]) obj;
		ByteArrayInputStream input = new ByteArrayInputStream(ar);
		LabyrinthObjectInputStream inputStream = new LabyrinthObjectInputStream(input);
		for (ClassLookup l : lookups) {
			inputStream.add(l);
		}
		return (T) inputStream.readObject();

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
		byte[] serial = Base64.getDecoder().decode(obj.toString());
		ByteArrayInputStream input = new ByteArrayInputStream(serial);
		LabyrinthObjectInputStream inputStream = new LabyrinthObjectInputStream(input);
		return inputStream.readObject();
	}

	/**
	 * If the provided object is an encoded byte array deserialize it back into its object state.
	 *
	 * @param classLoader The classloader to use for deserialization.
	 * @return The object the byte array once was or null if not an encoded byte array.
	 * @throws IOException if an I/O error occurs while reading stream heade
	 * @throws ClassNotFoundException if the class of the serialized object cannot be found
	 */
	public <T> T fromByteArray(@NotNull ClassLoader classLoader) throws IOException, ClassNotFoundException {
		if (obj == null || !byte[].class.isAssignableFrom(obj.getClass())) return null;
		byte[] ar = (byte[]) obj;
		ByteArrayInputStream input = new ByteArrayInputStream(ar);
		LabyrinthObjectInputStream inputStream = new LabyrinthObjectInputStream(input).setLoader(classLoader);
		return (T) inputStream.readObject();

	}

	/**
	 * The original stored object retaining all values converted back to an object.
	 * <p>
	 * WARN: You will need to pass a type to the object upon use.
	 *
	 * @param classLoader The classloader to use for deserialization.
	 * @return the deserialized form of an object with its original state
	 * @throws IOException typically, if a class has been modified in comparison
	 * to its original structure
	 * @throws ClassNotFoundException if the class could not be located properly
	 */
	public Object deserialized(@NotNull ClassLoader classLoader) throws IOException, ClassNotFoundException {
		byte[] serial = Base64.getDecoder().decode(obj.toString());
		ByteArrayInputStream input = new ByteArrayInputStream(serial);
		LabyrinthObjectInputStream inputStream = new LabyrinthObjectInputStream(input).setLoader(classLoader);
		return inputStream.readObject();
	}

	/**
	 * Deserialize an object of specified type from a string.
	 * <p>
	 * Primarily for misc use, deserialization is handled internally for normal object use from containers.
	 *
	 * @param type the type this object represents
	 * @param <R> the type this object represents
	 * @return a deserialized object or null
	 */
	public <R> @Nullable R deserialize(@NotNull Class<R> type) {
		try {
			Object o = deserialized();
			if (o == null) return null;
			if (type.isAssignableFrom(o.getClass())) {
				return (R) o;
			} else {
				throw new IllegalArgumentException(o.getClass().getSimpleName() + " is not assignable from " + type.getSimpleName());
			}
		} catch (IOException | ClassNotFoundException e) {
			LabyrinthProvider.getService(Service.MESSENGER).getEmptyMailer().error("- " + e.getMessage()).queue();
		}
		return null;
	}

	/**
	 * Deserialize an object of specified type from a string.
	 * <p>
	 * Primarily for misc use, deserialization is handled internally for normal object use from containers.
	 *
	 * @param classLoader The classloader to use for deserialization.
	 * @param type the type this object represents
	 * @param <R> the type this object represents
	 * @return a deserialized object or null
	 */
	public <R> @Nullable R deserialize(@NotNull Class<R> type, @NotNull ClassLoader classLoader) {
		try {
			Object o = deserialized(classLoader);
			if (o == null) return null;
			if (type.isAssignableFrom(o.getClass())) {
				return (R) o;
			} else {
				throw new IllegalArgumentException(o.getClass().getSimpleName() + " is not assignable from " + type.getSimpleName());
			}
		} catch (IOException | ClassNotFoundException e) {
			LabyrinthProvider.getService(Service.MESSENGER).getEmptyMailer().error("- " + e.getMessage()).queue();
		}
		return null;
	}

}
