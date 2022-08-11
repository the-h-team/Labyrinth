package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.panther.util.Applicable;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

/**
 * @inheritDoc
 */
public class FancyMessage extends MessageBuilder {

	private Chunk latest() {
		return TEXT.get(Math.max(0, TEXT.size() - 1));
	}

	public FancyMessage() {
	}

	public FancyMessage(String text) {
		then(text);
	}


	/**
	 * Append a new text section at the the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param text The text to use.
	 * @return The same bulletin builder.
	 */
	public FancyMessage then(String text) {
		append(new TextChunk(text));
		return this;
	}

	/**
	 * Append a new text section at the the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param bool The boolean to use.
	 * @return The same bulletin builder.
	 */
	public FancyMessage then(boolean bool) {
		return then(String.valueOf(bool));
	}

	/**
	 * Append a new text section at the the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param i The int to use.
	 * @return The same bulletin builder.
	 */
	public FancyMessage then(int i) {
		return then(String.valueOf(i));
	}

	/**
	 * Append a new text section at the the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param d The double to use.
	 * @return The same bulletin builder.
	 */
	public FancyMessage then(double d) {
		return then(String.valueOf(d));
	}

	/**
	 * Append a new text section at the the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param l The long to use.
	 * @return The same bulletin builder.
	 */
	public FancyMessage then(long l) {
		return then(String.valueOf(l));
	}

	/**
	 * Style the text section at the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param style The styling to use
	 * @return The same bulletin builder.
	 */
	public FancyMessage style(ChatColor style) {
		if (latest() != null) {
			if (latest().isEmpty()) {
				latest().style(style);
			}
		}
		return this;
	}

	/**
	 * Style the text section at the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param style The styling to use
	 * @return The same bulletin builder.
	 */
	public FancyMessage style(ChatColor... style) {
		if (latest() != null) {
			if (latest().isEmpty()) {
				latest().style(style);
			}
		}
		return this;
	}

	/**
	 * Style the text section at the tail end of this object.
	 *
	 * <p>Any prior styling using this method will be reset.</p>
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param color The gradient to use.
	 * @return The same bulletin builder.
	 */
	public FancyMessage style(CustomColor color) {
		if (latest() != null) {
			if (latest().isEmpty()) {
				latest().style(color);
			}
		}
		return this;
	}

	/**
	 * Color the text section at the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param color The chat color to use.
	 * @return The same bulletin builder.
	 */
	public FancyMessage color(ChatColor color) {
		if (latest() != null) {
			if (latest().isEmpty()) {
				latest().color(color);
			}
		}
		return this;
	}

	/**
	 * Color the text section at the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param color The color to use.
	 * @return The same bulletin builder.
	 */
	public FancyMessage color(Color color) {
		if (latest() != null) {
			if (latest().isEmpty()) {
				latest().color(color);
			}
		}
		return this;
	}

	/**
	 * Add a hover tooltip to the text section at the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param text the text to display within the tooltip.
	 * @return The same bulletin builder.
	 */
	public FancyMessage hover(String text) {
		if (latest() != null) {
			latest().bind(new ToolTip.Text(text));
		}
		return this;
	}

	/**
	 * Add a hover tooltip to the text section at the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param itemStack the item to display within the tooltip
	 * @return The same bulletin builder.
	 */
	public FancyMessage hover(ItemStack itemStack) {
		if (latest() != null) {
			latest().bind(new ToolTip.Item(itemStack));
		}
		return this;
	}

	/**
	 * Add a text suggestion tooltip to the text section at the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param command The command/text to suggest.
	 * @return The same bulletin builder.
	 */
	public FancyMessage suggest(String command) {
		if (latest() != null) {
			latest().bind(new ToolTip.Suggestion(command));
		}
		return this;
	}

	/**
	 * Add a url opening tooltip to the text section at the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param url The url to open.
	 * @return The same bulletin builder.
	 */
	public FancyMessage url(String url) {
		if (latest() != null) {
			latest().bind(new ToolTip.Url(url));
		}
		return this;
	}

	/**
	 * Add a text copying tooltip to the text section at the tail end of this object.
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param text The text to copy.
	 * @return The same bulletin builder.
	 */
	public FancyMessage copy(String text) {
		if (latest() != null) {
			latest().bind(new ToolTip.Copy(text));
		}
		return this;
	}

	/**
	 * Add a command executing tooltip to the text section at the tail end of this object
	 * <p>
	 * The "tail end" is the most recently parsed section.
	 *
	 * @param command The command to run
	 * @return The same bulletin builder.
	 */
	public FancyMessage command(String command) {
		if (latest() != null) {
			latest().bind(new ToolTip.Command(command));
		}
		return this;
	}

	/**
	 * Add a custom action tooltip to the text section at the tail end of this object.
	 *
	 * @param data The operation to run
	 * @return The same bulletin builder.
	 */
	public FancyMessage action(Applicable data) {
		if (latest() != null) {
			latest().bind(new ToolTip.Action(data));
		}
		return this;
	}

	@Override
	public FancyMessage append(Chunk text) {
		return (FancyMessage) super.append(text);
	}

	@Override
	public FancyMessage append(Message message) {
		return (FancyMessage) super.append(message);
	}

	@Override
	public FancyMessage append(String message) {
		return (FancyMessage) super.append(message);
	}
}
