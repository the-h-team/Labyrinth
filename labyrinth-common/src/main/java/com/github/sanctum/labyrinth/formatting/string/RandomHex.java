package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.annotation.NodePointer;
import com.github.sanctum.labyrinth.data.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.melion.rgbchat.chat.TextColor;
import org.bukkit.Color;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

@NodePointer("com.github.sanctum.labyrinth.formatting.string.RandomHex")
public class RandomHex extends Number implements JsonAdapter<RandomHex>, ConfigurationSerializable, CustomColor {

	private static final long serialVersionUID = -7282478033342958031L;
	private final int hex;
	private final String id;
	private String con;
	private final String c1;
	private final String c2;
	private final GradientColor color;

	protected RandomHex(String c1, String c2, String name) {
		this.id = name;
		this.c1 = c1;
		this.c2 = c2;
		this.color = new GradientColor(this.c1, this.c2);
		this.hex = java.awt.Color.decode(this.c1).getRGB();
	}

	public RandomHex() {
		Random random = new Random();
		String color1 = String.format("#%06x", random.nextInt(0xffffff + 1));
		String color2 = String.format("#%06x", random.nextInt(0xffffff + 1));
		this.id = new RandomID(6, color1 + color2).generate();
		this.c1 = color1;
		this.c2 = color2;
		this.color = new GradientColor(color1, color2);
		this.hex = java.awt.Color.decode(color1).getRGB();
	}

	public Color toColor() {
		return Color.fromRGB(intValue());
	}

	@Override
	public String toString() {
		return hex + "";
	}

	public GradientColor toGradient() {
		return color;
	}

	@Override
	public int intValue() {
		return hex;
	}

	@Override
	public long longValue() {
		return intValue();
	}

	@Override
	public float floatValue() {
		return intValue();
	}

	@Override
	public double doubleValue() {
		return intValue();
	}

	@Override
	public JsonElement write(RandomHex randomHex) {
		JsonObject o = new JsonObject();
		o.addProperty("color1", randomHex.c1);
		o.addProperty("color2", randomHex.c2);
		o.addProperty("name", randomHex.id);
		return o;
	}

	@Override
	public RandomHex read(Map<String, Object> object) {
		String start = (String) object.get("color1");
		String end = (String) object.get("color2");
		String name = (String) object.get("name");
		return new RandomHex(start, end, name);
	}

	@Override
	public Class<RandomHex> getClassType() {
		return RandomHex.class;
	}

	@Override
	public String name() {
		return this.id;
	}

	@Override
	public String join() {
		return "<" + c1 + ">" + this.con + "</" + c2 + ">";
	}

	@Override
	public BaseComponent[] build() {
		return TextComponent.fromLegacyText(translate());
	}

	@Override
	public String translate() {
		return new ColoredString(join(), ColoredString.ColorType.HEX).toString();
	}

	@Override
	public CustomColor context(String context) {
		this.con = context;
		return this;
	}

	@Override
	public TextColor[] colors() {
		return new TextColor[]{new TextColor(c1), new TextColor(c2)};
	}

	public static RandomHex deserialize(Map<String, Object> map) {
		return new RandomHex((String)map.get("color1"), (String)map.get("color2"), (String)map.get("name"));
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("color1", c1);
		map.put("color2", c2);
		map.put("name", id);
		return map;
	}
}
