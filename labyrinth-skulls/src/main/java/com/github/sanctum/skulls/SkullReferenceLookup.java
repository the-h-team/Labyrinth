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
import org.jetbrains.annotations.NotNull;

/**
 * Encapsulate player data and search online for skin results.
 */
public class SkullReferenceLookup {

	final String name, id, value;
	final Gson gson = new Gson();

	public SkullReferenceLookup(String name) throws InvalidSkullReferenceException {
		this.name = name;
		try {
			JsonObject obj = getMojangContent(name);
			this.id = obj.get("id").toString().replace("\"", "");
            obj = getProfileContent(UUID.fromString(this.id));
			String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
			String decoded = new String(Base64.getDecoder().decode(value));
			obj = gson.fromJson(decoded, JsonObject.class);
			String skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
			byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();
			this.value = new String(Base64.getEncoder().encode(skinByte));
		} catch (Exception e) {
			throw new InvalidSkullReferenceException("There was a problem finding matching results for '" + name + "' within skull reference lookup.");
		}
	}

	public SkullReferenceLookup(UUID id) throws InvalidSkullReferenceException {
		try {
			JsonObject obj = getProfileContent(id);
			this.id = obj.get("id").toString().replace("\"", "");
			this.name = obj.get("name").toString().replace("\"", "");
			String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
			String decoded = new String(Base64.getDecoder().decode(value));
			obj = gson.fromJson(decoded, JsonObject.class);
			String skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
			byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();
			this.value = new String(Base64.getEncoder().encode(skinByte));
		} catch (Exception e) {
			throw new InvalidSkullReferenceException("There was a problem finding matching results for '" + id.toString() + "' within skull reference lookup.");
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
		return UUID.fromString(this.id);
	}

	/**
	 * Get the desired skin applied to a player skull.
	 *
	 * @return the desired skin applied to a player skull or default if not successful.
	 */
	public @NotNull ItemStack getResult() {
		return SkullReferenceDocker.provide(this.value);
	}

	private JsonObject getProfileContent(UUID id) {
		URL url;
		BufferedReader in = null;
		StringBuilder sb = new StringBuilder();
		try {
			url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + id.toString());
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
		return gson.fromJson(sb.toString(), JsonObject.class);
	}

	private JsonObject getMojangContent(String name) {
		URL url;
		BufferedReader in = null;
		StringBuilder sb = new StringBuilder();
		try {
			url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
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
		return gson.fromJson(sb.toString(), JsonObject.class);
	}


}