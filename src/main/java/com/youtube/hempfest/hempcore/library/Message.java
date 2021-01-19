package com.youtube.hempfest.hempcore.library;

import com.youtube.hempfest.hempcore.HempCore;
import com.youtube.hempfest.hempcore.formatting.string.ColoredString;
import java.util.logging.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Message {

	private final Logger logger = Logger.getLogger("Minecraft");
	private final String prefix;
	private Player p;

	/*
	*
	* Use only for console formatting.
	*
	 */
	public Message(String prefix) {
		this.prefix = prefix;
	}

	public void assignPlayer(Player player) {
		this.p = player;
	}

	public Message(Player p, String prefix) {
		this.p = p;
		this.prefix = prefix;
	}

	public void send(String text) {
		String result;
		if (prefix == null || prefix.isEmpty()) {
			if (Bukkit.getVersion().contains("1.16")) {
				result = new ColoredString(text, ColoredString.ColorType.HEX).toString();
			} else {
				result = new ColoredString(text, ColoredString.ColorType.MC).toString();
			}
		} else {
			if (Bukkit.getVersion().contains("1.16")) {
				result = new ColoredString(prefix + " " + text, ColoredString.ColorType.HEX).toString();
			} else {
				result = new ColoredString(prefix + " " + text, ColoredString.ColorType.MC).toString();
			}
		}
		p.sendMessage(result);
	}

	public void build(TextComponent component) {
		p.spigot().sendMessage(component);
	}

	public void build(BaseComponent... components) {
		p.spigot().sendMessage(components);
	}

	public void info(String text) {
		logger.info(String.format("[%s] - " + text, prefix));
	}

	public void error(String text) {
		logger.severe(String.format("[%s] - " + text, prefix));
	}

	public void warn(String text) {
		logger.severe(String.format("[%s] - " + text, prefix));
	}


}
