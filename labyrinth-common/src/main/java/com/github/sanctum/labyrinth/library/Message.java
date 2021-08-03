package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.formatting.string.ColoredString;
import java.util.function.Predicate;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * @author Hempfest
 */
@SuppressWarnings("UnusedReturnValue")
public class Message {

	private Logger logger = Logger.getLogger("Minecraft");
	private Plugin plugin;
	private String prefix;
	private Player p;


	public Message(Plugin plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();
	}

	/**
	 * Send a specified player messages.
	 *
	 * @param p the player to use
	 */
	public Message(Player p) {
		this.p = p;
		this.prefix = "";
	}

	/**
	 * Send the specified player messages with a specified prefix.
	 *
	 * @param p the player to use
	 * @param prefix the prefix to send
	 */
	public Message(Player p, String prefix) {
		this.p = p;
		this.prefix = prefix;
	}

	/**
	 * Update the player to receive the messages.
	 *
	 * @param player the new player should now receive messages
	 * @return this Message instance
	 */
	public Message setPlayer(Player player) {
		this.p = player;
		return this;
	}

	/**
	 * @deprecated Use {@link Message#setPlayer(Player)}
	 */
	@Deprecated
	public Message assignPlayer(Player player) {
		this.p = player;
		return this;
	}

	/**
	 * Update the prefix used for the message.
	 *
	 * @param prefix the prefix to use
	 * @return this Message instance
	 */
	public Message setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	/**
	 * Send a string message to a player automatically colored.
	 *
	 * @param text the context to send the player
	 * @return this Message instance
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
	 * Broadcast a message publicly.
	 *
	 * @param text the context to broadcast
	 * @return this Message instance
	 */
	public Message broadcast(String text) {
		if (plugin != null) {
			if (this.prefix == null) {
				this.prefix = "&7[&e" + plugin.getName() + "&7]&r";
			}
			Bukkit.broadcastMessage(StringUtils.use(prefix + " " + text).translate());
		}
		return this;
	}

	/**
	 * Broadcast a message publicly to those with permission.
	 *
	 * @param text the context to broadcast
	 * @return this Message instance
	 */
	public Message broadcast(String text, String permission) {
		if (plugin != null) {
			if (this.prefix == null) {
				this.prefix = "&7[&e" + plugin.getName() + "&7]&r";
			}
			Bukkit.broadcast(StringUtils.use(prefix + " " + text).translate(), permission);
		}
		return this;
	}

	/**
	 * Broadcast a message publicly to those who meet the desired terms.
	 *
	 * @param text The text to broadcast.
	 * @param player The prerequisite to receiving the message.
	 * @return this Message instance.
	 */
	public Message broadcast(String text, Predicate<Player> player) {
		if (plugin != null) {
			if (this.prefix == null) {
				this.prefix = "&7[&e" + plugin.getName() + "&7]&r";
			}
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (player.test(p)) {
					p.sendMessage(StringUtils.use(prefix + " " + text).translate());
				}
			}
		} else {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (player.test(p)) {
					p.sendMessage(StringUtils.use(text).translate());
				}
			}
		}
		return this;
	}

	/**
	 * Send the player an interactive chat component.
	 *
	 * @param component the component to build and send
	 * @return this Message instance
	 */
	public Message build(BaseComponent component) {
		p.spigot().sendMessage(component);
		return this;
	}

	/**
	 * Send a player the list of interactive chat components.
	 *
	 * @param components the list of interactive chat to send
	 * @return this Message instance
	 */
	public Message build(BaseComponent... components) {
		p.spigot().sendMessage(components);
		return this;
	}

	/**
	 * Send an action bar message to the player.
	 *
	 * @param text the message to display
	 * @return this Message instance
	 */
	public Message action(String text) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ColoredString(text, ColoredString.ColorType.HEX).toComponent());
		return this;
	}

	/**
	 * <strong>NOTE: Only for console use.</strong>
	 * <p>
	 * Send console an info message.
	 *
	 * @param text the text to be used within the message
	 */
	public Message info(String text) {
		logger.info(text);
		return this;
	}

	/**
	 * <strong>NOTE: Only for console use.</strong>
	 * <p>
	 * Send console an error message.
	 *
	 * @param text the text to be used within the message
	 */
	public Message error(String text) {
		logger.severe(text);
		return this;
	}

	/**
	 * <strong>NOTE: Only for console use.</strong>
	 * <p>
	 * Send console a warning message.
	 *
	 * @param text the text to be used within the message
	 */
	public Message warn(String text) {
		logger.warning(text);
		return this;
	}

	/**
	 * Use this to easily send message degrees to console.
	 *
	 * @param plugin the plugin to log console messages for
	 * @return a console messaging object
	 */
	public static Message loggedFor(Plugin plugin) {
		return new Message(plugin);
	}

	/**
	 * Use this to easily send messages to player or server.
	 *
	 * @param player the target player
	 * @return a player &amp; server messaging object
	 */
	public static Message form(Player player) {
		return new Message(player);
	}


}
