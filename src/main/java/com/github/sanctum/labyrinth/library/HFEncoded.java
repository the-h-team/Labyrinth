package com.github.sanctum.labyrinth.library;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class HFEncoded {

	private Object obj;

	private String objSerial;

	/**
	 * Convert the entire object into a string while retaining all of its values.
	 *
	 * <p>WARNING: Making changes to objects then attempting to load/reuse older un-modified obejcts
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
	 * <p>WARNING: Making changes to objects then attempting to load/reuse older un-modified obejcts
	 * could have negative effects, ensure you have proper object handling when dealing with serialization.</p>
	 *
	 * @param objSerial The serialized object string to convert.
	 */
	public HFEncoded(String objSerial) { this.objSerial = objSerial; }

	/**
	 * The original stored object retaining all values converted to a string.
	 *
	 * @return Get's a serialized hash for this object with retained values.
	 * @throws IOException Throw's an exception if Bukkit cannot serialize the object due to no
	 * inheritance of the java.io Serializable interface.
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
	 * @throws IOException Throw's IO if something had been modified in comparison to
	 * it's original methods/class structure.
	 * @throws ClassNotFoundException Throw's exception if the class was unable to be
	 * located properly.
	 */
	public Object deserialized() throws IOException, ClassNotFoundException {
		byte[] serial = Base64.getDecoder().decode(objSerial);
		ByteArrayInputStream input = new ByteArrayInputStream(serial);
		BukkitObjectInputStream inputStream = new BukkitObjectInputStream(input);
		return inputStream.readObject();
	}

}
