package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.data.JsonAdapter;
import com.github.sanctum.labyrinth.data.NodePointer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.melion.rgbchat.chat.TextColor;
import org.bukkit.configuration.serialization.SerializableAs;

/**
 * @author Hempfest
 */
@NodePointer(value = "CustomColor", type = RandomHex.class)
@SerializableAs("CustomColor")
public interface CustomColor extends JsonAdapter<CustomColor> {

	String name();

	String join();

	String translate();

	BaseComponent[] build();

	CustomColor context(String context);

	TextColor[] colors();

	default String getStart() {
		return "";
	}

	default String getEnd() {
		return "";
	}

}
