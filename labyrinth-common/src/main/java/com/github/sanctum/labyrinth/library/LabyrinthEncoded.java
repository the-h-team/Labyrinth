package com.github.sanctum.labyrinth.library;

import com.github.sanctum.panther.annotation.Note;
import com.github.sanctum.panther.util.HFEncoded;
import org.jetbrains.annotations.NotNull;

/**
 * A class for serializing/deserializing object's with support for {@link java.io.Serializable}, {@link org.bukkit.configuration.serialization.ConfigurationSerializable} & {@link com.github.sanctum.panther.file.JsonAdapter}
 */
public class LabyrinthEncoded extends HFEncoded {

	/**
	 * Convert the entire object into a string while retaining all of its values.
	 *
	 * <p>WARNING: Making changes to objects then attempting to attach/reuse older un-modified objects
	 * could have negative effects, ensure you have proper object handling when dealing with serialization.</p>
	 *
	 * @param obj a Serializable object (Java Serializable and/or Bukkit's ConfigurationSerializable)
	 */
	public LabyrinthEncoded(Object obj) {
		super(obj);
		setInput(LabyrinthObjectInputStream.class);
		setOutput(LabyrinthObjectOutputStream.class);
	}

	@Note("Delegate for both serialization transactions")
	public static LabyrinthEncoded of(@NotNull Object obj) {
		return new LabyrinthEncoded(obj);
	}

}
