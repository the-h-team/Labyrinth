package com.youtube.hempfest.hempcore.library;

import com.youtube.hempfest.hempcore.formatting.string.RandomID;
import java.io.Serializable;

public class HUID implements Serializable {

	private String hUID;

	private HUID() {
	}

	private HUID(String hUID) {
		this.hUID = hUID;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(hUID);
		sb.insert(3, '-');
		return sb.toString();
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
		return new HUID(wID);
	}

}
