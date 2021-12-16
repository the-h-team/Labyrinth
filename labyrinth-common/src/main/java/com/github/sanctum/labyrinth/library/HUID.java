package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.formatting.string.RandomID;
import com.github.sanctum.labyrinth.formatting.string.SpecialID;
import java.io.Serializable;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * Hempfest.Unique.Identifier, a 12 character long string containing letters and numbers.
 *
 * @author Hempfest
 */
public final class HUID implements CharSequence, Serializable {

	private static final String WHITESPACE = "";
	private static final char DIVIDER = '-';
	private static final long serialVersionUID = -1397776894898458349L;
	private final String id;

	HUID(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof HUID)) return false;
		HUID huid = (HUID) o;
		return huid.id.equals(id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public int length() {
		return id.length();
	}

	@Override
	public char charAt(int index) {
		return id.charAt(index);
	}

	@NotNull
	@Override
	public CharSequence subSequence(int start, int end) {
		return id.subSequence(start, end);
	}

	/**
	 * Convert the HUID into string format (####-####-####).
	 *
	 * @return the HUID in string form
	 */
	@Override
	public @NotNull String toString() {
		return new StringBuilder(id).insert(4, '-').insert(9, '-').toString();
	}

	/**
	 * Get a completely random HUID.
	 *
	 * @return a completely random HUID
	 */
	public static HUID randomID() {
		return new HUID(new RandomID(12).generate());
	}

	/**
	 * Try to parse an object as a Hempfest Unique Identifier, if the passed value contains
	 * the correct '<strong>XXXX-XXXX-XXXX</strong>' format use {@link ParsedHUID#toID()} following a prior {@link ParsedHUID#isValid()} check
	 * otherwise create a new self persistent id based on the hash identity of the provided object with {@link ParsedHUID#newID()}.
	 *
	 * Self persistent id creation using hash identity is universal for primitives like strings.
	 * Example, the username "Hempfest" no matter what within <strong>ANY</strong> JVM will return the HUID <strong>DDec-eDCe-DAXw</strong> with an assortment length of 12.
	 * Try it your yourself!
	 *
	 * @param value The object to parse
	 * @return An HUID object parser.
	 */
	public static ParsedHUID parseID(final @NotNull Object value) {
		return new ParsedHUID() {

			private final String test;

			{
				test = value.toString().replace(String.valueOf(DIVIDER), WHITESPACE);
			}

			@Override
			public boolean isValid() {
				return StringUtils.isAlphanumeric(test) && test.length() == 12;
			}

			@Override
			public HUID toID() {
				if (value instanceof HUID) return (HUID) value;
				return new HUID(test);
			}

			@Override
			public HUID newID() {
				return new HUID(SpecialID.builder().setLength(12).build(value).toString());
			}

			@Override
			public HUID newID(Character... characters) {
				return new HUID(SpecialID.builder().setLength(12).setOptions(characters).build(value).toString());
			}

			@Override
			public String toString() {
				return isValid() ? test : newID().toString();
			}
		};
	}

	/**
	 * Convert a string-form HUID back from its string representation
	 * into an HUID object.
	 *
	 * @deprecated Use {@link HUID#parseID(Object)} instead!
	 * @param id the written ID to convert to object form
	 * @return the HUID object or null if not representative of an HUID
	 */
	@Deprecated
	public static HUID fromString(String id) {
		String test = id.replace(String.valueOf(DIVIDER), WHITESPACE);
		return !StringUtils.isAlphanumeric(test) || test.length() != 12 ? null : new HUID(test);
	}

}
