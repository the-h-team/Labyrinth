package com.github.sanctum.skulls;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Encapsulate player data and search online for skin results.
 */
public class HeadLookup {

	protected String name = null;
	protected String id = null;
	protected String value = null;

	// TODO: throw to prevent invalid object state and allow field finality
	public HeadLookup(String name) {
		this.name = name;
		try {
			Gson g = new Gson();
			String signature = getNonSessionContent(name);
			JsonObject obj = g.fromJson(signature, JsonObject.class);
			this.id = obj.get("id").toString().replace("\"", "");
			String sessionContent = getSessionContent(this.id);
			obj = g.fromJson(sessionContent, JsonObject.class);
			String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
			String decoded = new String(Base64.getDecoder().decode(value));
			obj = g.fromJson(decoded, JsonObject.class);
			String skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
			byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();
			this.value = new String(Base64.getEncoder().encode(skinByte));
		} catch (Exception ignored) {
		}
	}

	// TODO: throw to prevent invalid object state and allow field finality
	public HeadLookup(UUID id) {
		try {
			Gson g = new Gson();
			String signature = getSessionContent(id.toString());
			JsonObject obj = g.fromJson(signature, JsonObject.class);

			this.id = obj.get("id").toString().replace("\"", "");
			this.name = obj.get("name").toString().replace("\"", "");
			String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
			String decoded = new String(Base64.getDecoder().decode(value));
			obj = g.fromJson(decoded, JsonObject.class);
			String skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
			byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();
			this.value = new String(Base64.getEncoder().encode(skinByte));
		} catch (Exception ignored) {
		}
	}

	/**
	 * Get the name of this search.
	 *
	 * @return the name of this search
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the user id for this search.
	 *
	 * @return the user id for this search
	 */
	public UUID getUUID() {
		// TODO: Remove null-check after above refactor
		return this.id != null ? UUID.fromString(this.id) : null;
	}

	private String getSessionContent(String string) {
		URL url;
		BufferedReader in = null;
		StringBuilder sb = new StringBuilder();
		try {
			url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + string);
			in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
			String str;
			while ((str = in.readLine()) != null) {
				sb.append(str);
			}
		} catch (Exception ignored) {
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ignored) {
			}
		}
		return sb.toString();
	}

	private String getNonSessionContent(String string) {
		URL url;
		BufferedReader in = null;
		StringBuilder sb = new StringBuilder();
		try {
			url = new URL("https://api.mojang.com/users/profiles/minecraft/" + string);
			in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
			String str;
			while ((str = in.readLine()) != null) {
				sb.append(str);
			}
		} catch (Exception ignored) {
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ignored) {
			}
		}
		return sb.toString();
	}

	// TODO: With value never null, nullity should match CustomerHeadLoader.provide(String)
	/**
	 * Get the desired skin applied to a player skull.
	 *
	 * @return the desired skin applied to a player skull
	 */
	public @Nullable ItemStack getResult() {
		if (value != null) {
			return CustomHeadLoader.provide(this.value);
		}
		return null;
	}


}