package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.data.JsonAdapter;
import com.github.sanctum.labyrinth.data.NodePointer;
import com.github.sanctum.labyrinth.interfacing.JsonIntermediate;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import net.md_5.bungee.api.chat.BaseComponent;
import net.melion.rgbchat.chat.TextColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

/**
 * @author Hempfest
 */
@NodePointer(value = "CustomColor", type = RandomHex.class)
@SerializableAs("CustomColor")
@DelegateDeserialization(RandomHex.class)
public interface CustomColor extends JsonAdapter<CustomColor>, JsonIntermediate, ConfigurationSerializable {

	String name();

	String join();

	String translate();

	BaseComponent[] build();

	CustomColor context(String context);

	TextColor[] colors();

	default String getStart() {
		return "";
	}

	default String getEnd() {
		return "";
	}

	static CustomColor deserialize(Map<String, Object> map) {
		return new RandomHex((String) map.get("color1"), (String) map.get("color2"), (String) map.get("name"));
	}

	@Override
	default @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("color1", getStart());
		map.put("color2", getEnd());
		map.put("name", name());
		return map;
	}

	@Override
	default JsonElement write(CustomColor randomHex) {
		JsonObject o = new JsonObject();
		o.addProperty("color1", randomHex.getStart());
		o.addProperty("color2", randomHex.getEnd());
		o.addProperty("name", randomHex.name());
		return o;
	}

	@Override
	default CustomColor read(Map<String, Object> object) {
		String start = (String) object.get("color1");
		String end = (String) object.get("color2");
		String name = (String) object.get("name");
		return new RandomHex(start, end, name);
	}

	@Override
	default Class<CustomColor> getClassType() {
		return CustomColor.class;
	}


}
