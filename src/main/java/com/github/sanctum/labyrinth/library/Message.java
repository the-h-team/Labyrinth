package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.formatting.string.ColoredString;
import java.util.logging.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Message {

	private final Logger logger = Logger.getLogger("Minecraft");
	private String prefix;
	private Player p;


	/**
	 * NOTE: Only for console use
	 * Send easy messages through console with prefix specification.
	 *
	 * @param prefix The prefix to be used for console.
	 */
	public Message(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Update the player to recieve the messages.
	 *
	 * @param player The player to now recieve messages.
	 */
	public void assignPlayer(Player player) {
		this.p = player;
	}

	/**
	 * Update the prefix used for the message.
	 *
	 * @param prefix The prefix to use.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Send a specified player messages.
	 *
	 * @param p The player to use.
	 */
	public Message(Player p) {
		this.p = p;
		this.prefix = "";
	}

	/**
	 * Send a specified player messages with a specified prefix.
	 *
	 * @param p The player to use.
	 * @param prefix The prefix to send.
	 */
	public Message(Player p, String prefix) {
		this.p = p;
		this.prefix = prefix;
	}

	/**
	 * Send a string message to a player automatically colored.
	 *
	 * @param text The context to send the player
	 */
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

	/**
	 * Send the player an interactive chat component.
	 *
	 * @param component The component to build and send.
	 */
	public void build(BaseComponent component) {
		p.spigot().sendMessage(component);
	}

	/**
	 * Send a player the list of interactive chat components.
	 *
	 * @param components The list of interactive chat to send.
	 */
	public void build(BaseComponent... components) {
		p.spigot().sendMessage(components);
	}

	/**
	 * NOTE: Only for console use.
	 * Send console an info message.
	 *
	 * @param text The text to be used within the message
	 */
	public void info(String text) {
		logger.info(String.format("[%s] - " + text, prefix));
	}

	/**
	 * NOTE: Only for console use.
	 * Send console an error message.
	 *
	 * @param text The text to be used within the message
	 */
	public void error(String text) {
		logger.severe(String.format("[%s] - " + text, prefix));
	}

	/**
	 * NOTE: Only for console use.
	 * Send console a warning message.
	 *
	 * @param text The text to be used within the message
	 */
	public void warn(String text) {
		logger.severe(String.format("[%s] - " + text, prefix));
	}

	/**
	 * Use this to easily send message degrees to console.
	 *
	 * @param plugin The plugin to log console messages for.
	 * @return A console messagiwng object.
	 */
	public static Message loggedFor(Plugin plugin) {
		return new Message(plugin.getName());
	}

	/**
	 * Use this to easily send message's to player or console
	 *
	 * @param p The target.
	 * @return A player & console messaging object.
	 */
	public static Message form(Player p) {
		return new Message(p);
	}


}
