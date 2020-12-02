package com.youtube.hempfest.hempcore.library;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class HFEncoded implements Serializable {

	private Object obj;

	public HFEncoded(Object obj) {
		this.obj = obj;
	}

	public String serialize(Object obj) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(output);
		outputStream.writeObject(obj);
		outputStream.flush();

		byte[] serial = output.toByteArray();
		return Base64.getEncoder().encodeToString(serial);
	}

	public Object deserialized(String objSerial) throws IOException, ClassNotFoundException {
		byte[] serial = Base64.getDecoder().decode(objSerial);
		ByteArrayInputStream input = new ByteArrayInputStream(serial);
		BukkitObjectInputStream inputStream = new BukkitObjectInputStream(input);
		return inputStream.readObject();
	}

}
