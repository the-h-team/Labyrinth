package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.formatting.string.RandomID;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * @author Hempfest
 */
public class HUID {

	private String hUID;

	private HUID() {
	}

	private HUID(String hUID) {
		this.hUID = hUID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof HUID)) return false;
		HUID huid = (HUID) o;
		return huid.hUID.equalsIgnoreCase(hUID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hUID);
	}

	/**
	 * Convert the HUID into string format (####-####-####).
	 *
	 * @return the HUID in string form
	 */
	public String toString() {
		if (!hUID.contains("-")) {
			StringBuilder sb = new StringBuilder(hUID);
			sb.insert(4, '-');
			sb.insert(9, '-');
			return sb.toString().endsWith("-") ? sb.substring(0, sb.length() - 1) : sb.toString();
		}
		return hUID;
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
	 * Convert a string-form HUID back from its string representation
	 * into an HUID object.
	 *
	 * @param wID the written ID to convert to object form
	 * @return the HUID object
	 */
	public static HUID fromString(String wID) {
		if (!StringUtils.isAlphanumeric(wID) && !wID.contains("-")) {
			return null;
		}
		if (wID.replace("-", "").length() != 12) {
			return null;
		}
		return new HUID(wID.replace("-", ""));
	}

}
