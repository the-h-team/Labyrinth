package com.github.sanctum.labyrinth.formatting.string;

import java.util.Random;
import org.bukkit.Color;

public class RandomHex extends Number {

	private final int hex;
	private final GradientColor color;

	public RandomHex() {
		Random random = new Random();
		String color1 = String.format("#%06x", random.nextInt(0xffffff + 1));
		String color2 = String.format("#%06x", random.nextInt(0xffffff + 1));
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
}
