package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.annotation.Note;
import java.awt.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.melion.rgbchat.chat.TextColor;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * An enum for labyrinth default gradient colors and color translation utility.
 *
 * @author Hempfest
 */
public enum DefaultColor implements CustomColor {

	MANGO("#269947", "#e69f12"),
	VELVET("#e62012", "#8c0b10"),
	RANDOM(new RandomHex().toString(), new RandomHex().toString()),
	GALAXY("#1f1c4d", "#991c4e");

	private final CharSequence start;
	private final CharSequence end;
	private String context;

	DefaultColor(CharSequence start, CharSequence end) {
		this.start = start;
		this.end = end;
	}

	private static double getDistance(Color c1, Color c2) {
		double rmean = (c1.getRed() + c2.getRed()) / 2.0;
		double r = c1.getRed() - c2.getRed();
		double g = c1.getGreen() - c2.getGreen();
		int b = c1.getBlue() - c2.getBlue();
		double weightR = 2 + rmean / 256.0;
		double weightG = 4.0;
		double weightB = 2 + (255 - rmean) / 256.0;
		return weightR * r * r + weightG * g * g + weightB * b * b;
	}

	private static boolean areIdentical(Color c1, Color c2) {
		return Math.abs(c1.getRed() - c2.getRed()) <= 5 &&
				Math.abs(c1.getGreen() - c2.getGreen()) <= 5 &&
				Math.abs(c1.getBlue() - c2.getBlue()) <= 5;
	}

	@Note("Easily convert any color into a game only color")
	public static @NotNull ChatColor fromAwt(Color color) {
		int index = 0;
		double best = -1;
		for (int i = 0; i < ImageBreakdown.VANILLA_COLORS.length; i++) {
			if (areIdentical(ImageBreakdown.VANILLA_COLORS[i], color)) {
				return ChatColor.values()[i];
			}
		}
		for (int i = 0; i < ImageBreakdown.VANILLA_COLORS.length; i++) {
			double distance = getDistance(color, ImageBreakdown.VANILLA_COLORS[i]);
			if (distance < best || best == -1) {
				best = distance;
				index = i;
			}
		}
		// Minecraft has 15 colors
		return ChatColor.values()[index];
	}

	public DefaultColor wrap(String context) {
		this.context = context;
		return this;
	}

	@Override
	public String join() {
		return "<" + this.start + ">" + this.context + "</" + this.end + ">";
	}

	public String join(String context) {
		return "<" + this.start + ">" + context + "</" + this.end + ">";
	}

	@Override
	public BaseComponent[] build() {
		return TextComponent.fromLegacyText(translate());
	}

	@Override
	public CustomColor context(String context) {
		this.context = context;
		return this;
	}

	@Override
	public TextColor[] colors() {
		return new TextColor[]{new TextColor(this.start.toString()), new TextColor(this.end.toString())};
	}

	@Override
	public String getStart() {
		return start.toString();
	}

	@Override
	public String getEnd() {
		return end.toString();
	}

	public BaseComponent[] build(String context) {
		return TextComponent.fromLegacyText(translate(context));
	}

	@Override
	public String translate() {
		return new ColoredString(join(), ColoredString.ColorType.HEX).toString();
	}

	public String translate(String context) {
		return new ColoredString(join(context), ColoredString.ColorType.HEX).toString();
	}

	public String getName() {
		return name();
	}



	@Override
	public String toString() {
		return getStart();
	}
}
