package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.formatting.ComponentChunk;
import java.awt.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.melion.rgbchat.api.RGBApi;
import org.bukkit.ChatColor;

/**
 * An object-oriented way of color translating text.
 *
 * @author Hempfest
 */
public class ColoredString {


	private final ColorType type;
	private final String text;

	public ColoredString(String text) {
		this.text = text;
		if (LabyrinthProvider.getInstance().isNew()) {
			this.type = ColorType.HEX;
		} else {
			this.type = ColorType.MC;
		}
	}

	public ColoredString(String text, ColorType type) {
		this.text = text;
		this.type = type;
	}

	public enum ColorType {
		MC, MC_COMPONENT, HEX
	}

	@Note("Convert color object to html #Hex format")
	public static String fromAwt(Color c) {
		StringBuilder sb = new StringBuilder("#");

		if (c.getRed() < 16) sb.append('0');
		sb.append(Integer.toHexString(c.getRed()));

		if (c.getGreen() < 16) sb.append('0');
		sb.append(Integer.toHexString(c.getGreen()));

		if (c.getBlue() < 16) sb.append('0');
		sb.append(Integer.toHexString(c.getBlue()));

		return sb.toString();
	}

	/**
	 * Translate the text within a string body to color
	 */
	@Override
	public String toString() {
		String r = "No context to return";
		switch (type) {
			case MC:
				r = ChatColor.translateAlternateColorCodes('&', text);
				break;
			case MC_COMPONENT:
				r = new ComponentChunk(toComponent()).toJson(); // NEW * convert to json and read as string.
				break;
			case HEX:
				if (LabyrinthProvider.getInstance().isLegacy()) {
					r = ChatColor.translateAlternateColorCodes('&', text);
				} else {
					r = ChatColor.translateAlternateColorCodes('&', RGBApi.toColoredMessage(text));
				}
				break;
		}
		return r;
	}

	/**
	 * Translate the text within a TextComponent body to color
	 *
	 * @return Returns a string of text embedded as a Component
	 */
	public TextComponent toComponent() {
		return LabyrinthProvider.getInstance().isNew() ? new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', RGBApi.toColoredMessage(text)))) : new TextComponent(ChatColor.translateAlternateColorCodes('&', text));
	}


}
