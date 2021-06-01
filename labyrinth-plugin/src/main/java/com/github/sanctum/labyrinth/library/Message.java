package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.formatting.string.ColoredString;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Message {

	private Logger logger = Logger.getLogger("Minecraft");
	private String prefix;
	private Player p;


	public Message(Plugin plugin) {
		this.logger = plugin.getLogger();
	}

	/**
	 * NOTE: Only for console use
	 * Send easy messages through console with prefix specification.
	 *
	 * @param prefix The prefix to be used for console.
	 * @deprecated Use {@link Message#loggedFor(Plugin)}
	 */
	@Deprecated
	public Message(String prefix) {
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
	 * @param p      The player to use.
	 * @param prefix The prefix to send.
	 */
	public Message(Player p, String prefix) {
		this.p = p;
		this.prefix = prefix;
	}

	/**
	 * Update the player to recieve the messages.
	 *
	 * @param player The player to now recieve messages.
	 */
	public Message assignPlayer(Player player) {
		this.p = player;
		return this;
	}

	/**
	 * Update the prefix used for the message.
	 *
	 * @param prefix The prefix to use.
	 */
	public Message setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	/**
	 * Send a string message to a player automatically colored.
	 *
	 * @param text The context to send the player
	 */
	public Message send(String text) {
		String result;
		if (prefix == null || prefix.isEmpty()) {
			result = StringUtils.use(text).translate();
		} else {
			result = StringUtils.use(prefix + " " + text).translate();
		}
		p.sendMessage(result);
		return this;
	}

	/**
	 * Send the player an interactive chat component.
	 *
	 * @param component The component to build and send.
	 */
	public Message build(BaseComponent component) {
		p.spigot().sendMessage(component);
		return this;
	}

	/**
	 * Send a player the list of interactive chat components.
	 *
	 * @param components The list of interactive chat to send.
	 */
	public Message build(BaseComponent... components) {
		p.spigot().sendMessage(components);
		return this;
	}

	/**
	 * Send an action bar message to the player.
	 *
	 * @param text The message to display.
	 * @return The same message object.
	 */
	public Message action(String text) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ColoredString(text, ColoredString.ColorType.HEX).toComponent());
		return this;
	}

	/**
	 * NOTE: Only for console use.
	 * Send console an info message.
	 *
	 * @param text The text to be used within the message
	 */
	public Message info(String text) {
		logger.info(text);
		return this;
	}

	/**
	 * NOTE: Only for console use.
	 * Send console an error message.
	 *
	 * @param text The text to be used within the message
	 */
	public Message error(String text) {
		logger.severe(text);
		return this;
	}

	/**
	 * NOTE: Only for console use.
	 * Send console a warning message.
	 *
	 * @param text The text to be used within the message
	 */
	public Message warn(String text) {
		logger.warning(text);
		return this;
	}

	/**
	 * Use this to easily send message degrees to console.
	 *
	 * @param plugin The plugin to log console messages for.
	 * @return A console messagiwng object.
	 */
	public static Message loggedFor(Plugin plugin) {
		return new Message(plugin);
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
