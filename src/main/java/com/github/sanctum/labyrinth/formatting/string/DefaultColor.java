package com.github.sanctum.labyrinth.formatting.string;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.melion.rgbchat.chat.TextColor;

public enum DefaultColor implements CustomColor {

	MANGO("#269947", "#e69f12"),
	VELVET("#e62012", "#8c0b10"),
	GALAXY("#1f1c4d", "#991c4e");

	protected final CharSequence start;
	protected final CharSequence end;

	DefaultColor(CharSequence start, CharSequence end) {
		this.start = start;
		this.end = end;
	}

	public GradientColor gradient(String context) {
		return new GradientColor(context, name(), this.start, this.end);
	}

	@Override
	public String join() {
		return "<" + this.start + ">" + "</" + this.end + ">";
	}

	public String join(String context) {
		return join().replace("<" + this.start + ">", "<" + this.start + ">" + context);
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
