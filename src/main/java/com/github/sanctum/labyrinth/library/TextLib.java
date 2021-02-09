package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.formatting.component.Text;
import com.github.sanctum.labyrinth.formatting.component.Text_R2;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

public abstract class TextLib {
	private static TextLib instance = null;

	private TextLib() { }

	public abstract TextComponent textHoverable(String normalText, String hoverText, String hoverTextMessage);
	public abstract TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverTextMessage);
	public abstract TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message);
	public abstract TextComponent textSuggestable(String normalText, String hoverText, String hoverTextMessage, String commandName);
	public abstract TextComponent textRunnable(String normalText, String hoverText, String hoverTextMessage, String commandName);
	public abstract TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName);
	public abstract TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2);
	public abstract TextComponent textRunnable(String hoverText, String hoverText2, String hoverTextBody3, String hoverTextMessage, String hoverText2Message, String hoverMessage3, String commandName, String commandName2, String commandName3);

	public static TextLib getInstance() {
		if (instance == null) {
			if (Bukkit.getVersion().contains("1.16")) {
				instance = new TextLib() {
					final Text text1_16 = new Text();
					@Override
					public TextComponent textHoverable(String normalText, String hoverText, String hoverTextMessage) {
						return text1_16.textHoverable(normalText, hoverText, hoverTextMessage);
					}

					@Override
					public TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverTextMessage) {
						return text1_16.textHoverable(normalText, hoverText, normalText2, hoverTextMessage);
					}

					@Override
					public TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message) {
						return text1_16.textHoverable(normalText, hoverText, normalText2, hoverText2, hoverTextMessage, hoverText2Message);
					}

					@Override
					public TextComponent textSuggestable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
						return text1_16.textSuggestable(normalText, hoverText, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
						return text1_16.textRunnable(normalText, hoverText, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName) {
						return text1_16.textRunnable(normalText, hoverText, normalText2, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2) {
						return text1_16.textRunnable(normalText, hoverText, normalText2, hoverText2, hoverTextMessage, hoverText2Message, commandName, commandName2);
					}

					@Override
					public TextComponent textRunnable(String hoverText, String hoverText2, String hoverTextBody3, String hoverTextMessage, String hoverText2Message, String hoverMessage3, String commandName, String commandName2, String commandName3) {
						return text1_16.textRunnable(hoverText, hoverText2, hoverTextBody3, hoverTextMessage, hoverText2Message, hoverMessage3, commandName, commandName2, commandName3);
					}
				};
			} else {
				instance = new TextLib() {
					@Override
					public TextComponent textHoverable(String normalText, String hoverText, String hoverTextMessage) {
						return Text_R2.textHoverable(normalText, hoverText, hoverTextMessage);
					}

					@Override
					public TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverTextMessage) {
						return Text_R2.textHoverable(normalText, hoverText, normalText2, hoverTextMessage);
					}

					@Override
					public TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message) {
						return Text_R2.textHoverable(normalText, hoverText, normalText2, hoverText2, hoverTextMessage, hoverText2Message);
					}

					@Override
					public TextComponent textSuggestable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
						return Text_R2.textSuggestable(normalText, hoverText, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
						return Text_R2.textRunnable(normalText, hoverText, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName) {
						return Text_R2.textRunnable(normalText, hoverText, normalText2, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2) {
						return Text_R2.textRunnable(normalText, hoverText, normalText2, hoverText2, hoverTextMessage, hoverText2Message, commandName, commandName2);
					}

					@Override
					public TextComponent textRunnable(String hoverText, String hoverText2, String hoverTextBody3, String hoverTextMessage, String hoverText2Message, String hoverMessage3, String commandName, String commandName2, String commandName3) {
						return Text_R2.textRunnable(hoverText, hoverText2, hoverTextBody3, hoverTextMessage, hoverText2Message, hoverMessage3, commandName, commandName2, commandName3);
					}
				};
			}
		}
		return instance;
	}

}

