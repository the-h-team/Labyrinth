package com.github.sanctum.labyrinth.formatting.string;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.melion.rgbchat.chat.TextColor;

/**
 * Encapsulate Hexadecimal gradient information and apply it to provided text.
 */
public class GradientColor implements CustomColor {

	private final CharSequence start;

	private final CharSequence end;

	private final String context;

	private String name;

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
	 * @param name
	 * @return
	 */
	public GradientColor name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * @return
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * @return
	 */
	@Override
	public String join() {
		return "<" + start + ">" + this.context + "</" + end + ">";
	}

	/**
	 * @return
	 */
	@Override
	public BaseComponent[] build() {
		return TextComponent.fromLegacyText(translate());
	}

	/**
	 * @return
	 */
	@Override
	public String translate() {
		return new ColoredString(join(), ColoredString.ColorType.HEX).toString();
	}

}
