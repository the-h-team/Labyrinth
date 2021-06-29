package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.library.StringUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.melion.rgbchat.api.RGBApi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * @author Hempfest
 */
public class ColoredString {


	private final ColorType chosen;
	private final String text;

	public ColoredString(String text) {
		this.text = text;
		if (Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")) {
			this.chosen = ColorType.HEX;
		} else {
			this.chosen = ColorType.MC;
		}
	}

	public ColoredString(String text, ColorType type) {
		this.text = text;
		this.chosen = type;
	}

	public enum ColorType {
		MC, MC_COMPONENT, HEX
	}

	/**
	 * Translate the text within a string body to color
	 */
	public String toString() {
		String r = "No context to return";
		switch (chosen) {
			case MC:
				r = ChatColor.translateAlternateColorCodes('&', text);
				break;
			case MC_COMPONENT:
				r = "Cannot convert raw component to String";
				break;
			case HEX:
				if (Labyrinth.isLegacy()) {
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
		return Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") ? translateHexComponent(text) : new TextComponent(StringUtils.use(text).translate());
	}

	private TextComponent translateHexComponent(String text) {
		return new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', RGBApi.toColoredMessage(text))));
	}


}
