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
	 * @param name the name of the color
	 * @return this GradientColor object
	 */
	public GradientColor name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Provide context to wrap.
	 *
	 * @param context the context to wrap
	 * @return this GradientColor object
	 */
	public GradientColor context(String context) {
		this.context = context;
		return this;
	}

	/**
	 * Get the name of this GradientColor.
	 *
	 * @return the name of the color
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * Get the untranslated, formatted context.
	 *
	 * @return the untranslated, gradient-formatted context
	 */
	@Override
	public String join() {
		return "<" + start + ">" + this.context + "</" + end + ">";
	}

	/**
	 * Translate the colorized context.
	 *
	 * @return the translated gradient formatted context
	 * as a {@linkplain BaseComponent} array
	 */
	@Override
	public BaseComponent[] build() {
		return TextComponent.fromLegacyText(translate());
	}

	/**
	 * Translate the colorized context to JSON.
	 *
	 * @return the translated gradient formatted context
	 */
	@Override
	public String translate() {
		return new ColoredString(join(), ColoredString.ColorType.HEX).toString();
	}

}
