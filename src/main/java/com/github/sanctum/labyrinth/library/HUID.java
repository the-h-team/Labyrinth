package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.formatting.string.RandomID;
import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;

public class HUID implements Serializable {

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
		return hUID.equals(huid.hUID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hUID);
	}

	/**
	 * Convert the HUID into string format (####-####-####)
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
	 * @return A completely randomized HUID
	 */
	public static HUID randomID() {
		HUID result = new HUID();
		result.setId();
		return result;
	}

	/**
	 * Convert a string form HUID back from it's string into an HUID object.
	 * @param wID The written ID to convert to object form.
	 * @return The HUID object.
	 * @throws TypeNotPresentException Throw's exception if the id being parsed isn't representative of HUID type.
	 */
	public static HUID fromString(String wID) {
		if (!StringUtils.isAlphanumeric(wID) && !wID.contains("-")) {
			throw new TypeNotPresentException("HUID", new Throwable("[Labyrinth] - Unable to parse HUID, not alphanumeric."));
		}
		if (wID.replace("-", "").length() != 12) {
			try {
				return fromOldString(wID);
			} catch (NullPointerException e) {
				throw new TypeNotPresentException("HUID", new Throwable("[Labyrinth] - Unable to parse HUID, size greater/less than 12 char spec."));
			}
		}
		return new HUID(wID.replace("-", ""));
	}

	private static HUID fromOldString(String wID) {
		if (!wID.contains("-")) {
			throw new NullPointerException("[hempCore] - Unable to parse HUID");
		}
		if (wID.replace("-", "").length() != 6) {
			throw new NullPointerException("[hempCore] - Unable to parse HUID");
		}
		Labyrinth.getInstance().getLogger().warning("- Mocking full size NEW HUID, this is not guaranteed to work and could cause problems.");
		String add = new RandomID(6).generate();
		return new HUID(wID.replace("-", "") + add);
	}

}
