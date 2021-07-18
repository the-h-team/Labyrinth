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
	 * Convert the HUID into string format (####-####-####)
	 *
	 * @return The HUID in string form.
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

	private void setId() {
		this.hUID = new RandomID(12).generate();
	}

	/**
	 * Get a completely random HUID
	 *
	 * @return A completely randomized HUID
	 */
	public static HUID randomID() {
		HUID result = new HUID();
		result.setId();
		return result;
	}

	/**
	 * Convert a string form HUID back from it's string into an HUID object.
	 *
	 * @param wID The written ID to convert to object form.
	 * @return The HUID object.
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
