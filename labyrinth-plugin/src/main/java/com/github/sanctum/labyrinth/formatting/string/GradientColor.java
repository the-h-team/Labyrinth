package com.github.sanctum.labyrinth.formatting.string;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.melion.rgbchat.chat.TextColor;

/**
 * Encapsulate Hexadecimal gradient information and apply it to provided text.
 *
 * @author Hempfest
 */
public class GradientColor implements CustomColor {

	private final CharSequence start;

	private final CharSequence end;

	private String context;

	private String name;

	public GradientColor(CharSequence start, CharSequence end) {
		this.start = start;
		this.end = end;
	}

	public GradientColor(String context, CharSequence start, CharSequence end) {
		this.context = context;
		this.start = start;
		this.end = end;
	}

	public GradientColor(String context, String name, CharSequence start, CharSequence end) {
		this.name = name;
		this.context = context;
		this.start = start;
		this.end = end;
	}

	@Override
	public TextColor[] colors() {
		return new TextColor[]{new TextColor(start.toString()), new TextColor(end.toString())};
	}

	/**
	 * Provide a name for this color.
	 *
	 * @param name The name of the color.
	 * @return The same gradient color object.
	 */
	public GradientColor name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Provide context to wrap.
	 *
	 * @param context The context to wrap.
	 * @return The same gradient color object.
	 */
	public GradientColor context(String context) {
		this.context = context;
		return this;
	}

	/**
	 * @return The name of the color
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * Gets the un-translated formatted context.
	 *
	 * @return The gradient formatted context un-translated.
	 */
	@Override
	public String join() {
		return "<" + start + ">" + this.context + "</" + end + ">";
	}

	/**
	 * Translate the colorized context.
	 *
	 * @return The translated gradient formatted context as a base component array.
	 */
	@Override
	public BaseComponent[] build() {
		return TextComponent.fromLegacyText(translate());
	}

	/**
	 * Translate the colorized context.
	 *
	 * @return The translated gradient formatted context.
	 */
	@Override
	public String translate() {
		return new ColoredString(join(), ColoredString.ColorType.HEX).toString();
	}

}
