package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.panther.annotation.Json;
import com.github.sanctum.panther.util.Deployable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

/**
 * <h3>Easy {@link BaseComponent} building utilizing every possible meta combination.</h3>
 *
 * <p>Use this object for parsing chat component information.</p>
 *
 * <h3>----------------------------------------</h3>
 * <p>
 * You have public access to the impl through {@link FancyMessage};
 * <pre>{@code new FancyMessage()
 *  .then("Hello").color(ORANGE).style(BOLD, UNDERLINE)
 *  .then(" ")
 *  .then("[").color(FUCHSIA)
 *  .then("Hello").color(MAROON).hover("Hello viewer!")
 *  .then("]").color(FUCHSIA);}</pre>
 *
 * <h3>----------------------------------------</h3>
 * <p>
 * Take an entirely different approach to building if you want!
 * Access to building can be achieved through {@link Message.Factory};
 * <pre>{@code message()
 * .append(text("Hello").color(ORANGE).style(BOLD, UNDERLINE))
 * .append(text(" "))
 * .append(text("[").color(BLUE))
 * .append(text("Hello").color(GREEN).bind(hover("Hello viewer!")))
 * .append(text("]").color(BLUE));}</pre>
 *
 * @apiNote Look for string methods/areas marked with {@link Json} they <strong>require</strong> json formatting.
 */
public abstract class Message implements Iterable<Message.Chunk> {

	protected final List<Chunk> TEXT = new ArrayList<>();

	/**
	 * Append a section to the tail end of this object.
	 *
	 * @param section The section to append.
	 * @return The same message builder.
	 */
	public abstract Message append(Chunk section);

	/**
	 * Append an entire message to the tail end of this one.
	 *
	 * @param message The message to append.
	 * @return The same message builder.
	 */
	public abstract Message append(Message message);

	/**
	 * Append an entire message or section to the tail end of this object.
	 *
	 * @param jsonMessage The json translated component(s)
	 * @return The same message builder.
	 */
	public abstract Message append(@Json String jsonMessage);

	/**
	 * Compress each individual component section within this message into a singular base component.
	 *
	 * @return The compressed message with retained formatting.
	 */
	public abstract BaseComponent bake();

	/**
	 * Build every stylized section within this message.
	 *
	 * @return An array of styled base components.
	 */
	public abstract BaseComponent[] build();

	/**
	 * Translate every single reduced section within this object to a singular json object.
	 *
	 * @return The translated message.
	 */
	public abstract @Json
	String toJson();

	public final Chunk get(int index) {
		return TEXT.get(index);
	}

	public final int length() {
		return TEXT.size();
	}

	public boolean isEmpty() {
		return TEXT.isEmpty();
	}

	/**
	 * Send the stylized message to a specified player.
	 *
	 * @param target The player to send to.
	 * @return a deployable message.
	 */
	public abstract Deployable<Void> send(Player target);

	/**
	 * Send the stylized message to specified players.
	 *
	 * @param predicate The player predicate.
	 * @return a deployable message.
	 */
	public abstract Deployable<Void> send(Predicate<Player> predicate);

	public abstract Deployable<Void> clear();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Message)) return false;
		Message chunks = (Message) o;
		return Objects.equals(toJson(), chunks.toJson());
	}

	@Override
	public int hashCode() {
		return Objects.hash(toJson());
	}

	/**
	 * An overall native method factory to easily accessing new message,section & tooltip objects
	 */
	public interface Factory extends Chunk.Factory, ToolTip.Factory {

		default Message message() {
			return new FancyMessage();
		}

	}

	public abstract static class Chunk implements Iterable<ToolTip<?>>{

		public abstract Chunk append(String text);

		public abstract Chunk append(int i);

		public abstract Chunk append(double d);

		public abstract Chunk append(long l);

		public abstract Chunk style(ChatColor style);

		public abstract Chunk style(ChatColor... style);

		public abstract Chunk style(CustomColor color);

		public abstract Chunk color(ChatColor color);

		public abstract Chunk color(Color color);

		public abstract Chunk bind(ToolTip<?> context);

		public abstract Chunk setText(String text);

		public abstract Chunk replace(String text, String replacement);

		public abstract String getText();

		public boolean isEmpty() {
			return getText() != null && !getText().isEmpty();
		}

		public abstract BaseComponent toComponent();

		public @Json String toJson() {
			return ComponentSerializer.toString(toComponent());
		}

		public interface Factory {

			default Chunk json(@Json String section) {
				return new JsonChunk(section);
			}

			default Chunk text(String text) {
				return new TextChunk(text);
			}

			default Chunk text(String text, Color color) {
				return new TextChunk(text, color);
			}

			default Chunk text(String text, ChatColor color) {
				return new TextChunk(text, color);
			}

			default Chunk text(String text, CustomColor color) {
				return new TextChunk(text, color);
			}

		}
	}
}
