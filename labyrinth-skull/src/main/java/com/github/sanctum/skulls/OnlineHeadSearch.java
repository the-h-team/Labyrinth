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
 * Encapsulate data and search online for results.
 */
public class OnlineHeadSearch {

	private String name = null;
	private String id = null;
	private String value = null;

	public OnlineHeadSearch(String name) {
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

	public OnlineHeadSearch(UUID id) {
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
	 * @return The name belonging to the search.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The user id for this search.
	 */
	public UUID getUUID() {
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

	/**
	 * @return The desired skin applied to a player skull or bare.
	 */
	public @Nullable ItemStack getResult() {
		if (value != null) {
			return CustomHeadLoader.provide(this.value);
		}
		return null;
	}


}