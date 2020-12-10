package com.youtube.hempfest.hempcore.library;

import com.youtube.hempfest.hempcore.formatting.string.RandomID;
import java.io.Serializable;
import java.util.Objects;

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
			sb.insert(3, '-');
			return sb.toString();
		}
		return hUID;
	}

	private void setId() {
		this.hUID = new RandomID(6).generate();
	}

	public static HUID randomID() {
		HUID result = new HUID();
		result.setId();
		return result;
	}

	public static HUID fromString(String wID) {
		if (!wID.contains("-")) {
			throw new NullPointerException("[hempCore] - Unable to parse HUID");
		}
		if (wID.replace("-", "").length() != 6) {
			throw new NullPointerException("[hempCore] - Unable to parse HUID");
		}
		return new HUID(wID.replace("-", ""));
	}

}
