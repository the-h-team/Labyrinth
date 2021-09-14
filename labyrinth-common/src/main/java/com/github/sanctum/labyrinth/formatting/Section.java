package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Color;

public abstract class Section{

	public abstract Section append(String text);

	public abstract Section style(ChatColor style);

	public abstract Section style(CustomColor color);

	public abstract Section color(ChatColor color);

	public abstract Section color(Color color);

	public abstract Section bind(ToolTip<?> context);

	public abstract Section setText(String text);

	public abstract String getText();

	public abstract BaseComponent toComponent();

	public @Json String toJson() {
		return ComponentSerializer.toString(toComponent());
	}

	public interface Factory {

		default Section json(@Json String section) {
			return new JsonSection(section);
		}

		default Section text(String text) {
			return new TextSection(text);
		}

		default Section text(String text, Color color) {
			return new TextSection(text, color);
		}

		default Section text(String text, ChatColor color) {
			return new TextSection(text, color);
		}

		default Section text(String text, CustomColor color) {
			return new TextSection(text, color);
		}

	}
}
