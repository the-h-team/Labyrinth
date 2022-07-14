package com.github.sanctum.labyrinth.library;

import com.github.sanctum.panther.annotation.Note;
import com.github.sanctum.panther.util.HFEncoded;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	}

	@Note("Delegate for both serialization transactions")
	public static LabyrinthEncoded of(@NotNull Object obj) {
		return new LabyrinthEncoded(obj);
	}

	Function<ByteArrayOutputStream, ObjectOutputStream> outputFunction() {
		return byteArrayOutputStream -> {
			try {
				return new LabyrinthObjectOutputStream(byteArrayOutputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		};
	}

	Function<ByteArrayInputStream, ObjectInputStream> inputFunction() {
		return byteArrayInputStream -> {
			try {
				return new LabyrinthObjectInputStream(byteArrayInputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		};
	}

	public byte[] toByteArray() {
		return super.toByteArray(outputFunction());
	}

	public String serialize() throws IllegalStateException {
		return super.serialize(outputFunction());
	}

	public <T> T fromByteArray() throws IOException, ClassNotFoundException {
		return super.fromByteArray(inputFunction());
	}

	public Object deserialized(ClassLookup... lookups) throws IOException, ClassNotFoundException {
		return super.deserialized(inputFunction(), lookups);
	}

	public <T> T fromByteArray(ClassLookup... lookups) throws IOException, ClassNotFoundException {
		return super.fromByteArray(inputFunction(), lookups);
	}

	public Object deserialized() throws IOException, ClassNotFoundException {
		return super.deserialized(inputFunction());
	}

	public <T> T fromByteArray(@NotNull ClassLoader classLoader) throws IOException, ClassNotFoundException {
		return super.fromByteArray(inputFunction(), classLoader);
	}

	public Object deserialized(@NotNull ClassLoader classLoader) throws IOException, ClassNotFoundException {
		return super.deserialized(inputFunction(), classLoader);
	}

	public <R> @Nullable R deserialize(@NotNull Class<R> type) {
		return super.deserialize(inputFunction(), type);
	}

	public <R> @Nullable R deserialize(@NotNull Class<R> type, @NotNull ClassLoader classLoader) {
		return super.deserialize(inputFunction(), type, classLoader);
	}
}
