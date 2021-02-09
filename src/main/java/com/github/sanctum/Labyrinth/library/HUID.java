package com.github.sanctum.Labyrinth.library;

import com.github.sanctum.Labyrinth.formatting.string.RandomID;
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

	public String toString() {
		if (!hUID.contains("-")) {
			StringBuilder sb = new StringBuilder(hUID);
			sb.insert(4, '-');
			return sb.toString().endsWith("-") ? sb.substring(0, sb.length() - 1) : sb.toString();
		}
		return hUID;
	}

	private void setId() {
		this.hUID = new RandomID(10).generate();
	}

	public static HUID randomID() {
		HUID result = new HUID();
		result.setId();
		return result;
	}

	public static HUID fromString(String wID) {
		if (!StringUtils.isAlphanumeric(wID) && !wID.contains("-")) {
			throw new TypeNotPresentException("HUID", new Throwable("[Labyrinth] - Unable to parse HUID, not alphanumeric."));
		}
		if (wID.replace("-", "").length() != 10) {
			throw new TypeNotPresentException("HUID", new Throwable("[Labyrinth] - Unable to parse HUID, size exceeds 10 char limit."));
		}
		return new HUID(wID.replace("-", ""));
	}

}
