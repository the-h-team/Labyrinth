package com.github.sanctum.labyrinth.formatting.string;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.melion.rgbchat.chat.TextColor;

/**
 * @author Hempfest
 */
public enum DefaultColor implements CustomColor {

	MANGO("#269947", "#e69f12"),
	VELVET("#e62012", "#8c0b10"),
	RANDOM(new RandomHex().toString(), new RandomHex().toString()),
	GALAXY("#1f1c4d", "#991c4e");

	protected final CharSequence start;
	protected final CharSequence end;
	protected String context;

	DefaultColor(CharSequence start, CharSequence end) {
		this.start = start;
		this.end = end;
	}

	public DefaultColor wrap(String context) {
		this.context = context;
		return this;
	}

	@Override
	public String join() {
		return "<" + this.start + ">" + this.context + "</" + this.end + ">";
	}

	public String join(String context) {
		return "<" + this.start + ">" + context + "</" + this.end + ">";
	}

	@Override
	public BaseComponent[] build() {
		return TextComponent.fromLegacyText(translate());
	}

	@Override
	public TextColor[] colors() {
		return new TextColor[]{new TextColor(this.start.toString()), new TextColor(this.end.toString())};
	}

	public BaseComponent[] build(String context) {
		return TextComponent.fromLegacyText(translate(context));
	}

	@Override
	public String translate() {
		return new ColoredString(join(), ColoredString.ColorType.HEX).toString();
	}

	public String translate(String context) {
		return new ColoredString(join(context), ColoredString.ColorType.HEX).toString();
	}
}
