package com.github.sanctum.labyrinth.formatting.string;

import net.md_5.bungee.api.chat.BaseComponent;
import net.melion.rgbchat.chat.TextColor;

/**
 * @author Hempfest
 */
public interface CustomColor {

	String name();

	String join();

	String translate();

	BaseComponent[] build();

	CustomColor context(String context);

	TextColor[] colors();

}
