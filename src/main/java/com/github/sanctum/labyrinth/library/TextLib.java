package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.formatting.component.Text;
import com.github.sanctum.labyrinth.formatting.component.Text_R2;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

public abstract class TextLib {
	private static TextLib instance = null;

	private TextLib() { }

	/**
	 * Meta: {Hoverable}
	 *
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NH' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textHoverable(String normalText, String hoverText, String hoverTextMessage);

	/**
	 * Meta: {Hoverable}
	 *
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NHN' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverTextMessage);

	/**
	 * Meta: {Hoverable}
	 *
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NHNH' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message);

	/**
	 * Meta: {Suggestable}
	 * Suggest commands from interaction with the message.
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NH' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textSuggestable(String normalText, String hoverText, String hoverTextMessage, String commandName);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NH' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textRunnable(String normalText, String hoverText, String hoverTextMessage, String commandName);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NHN' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NHNH' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'HHH' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textRunnable(String hoverText, String hoverText2, String hoverText3, String hoverTextMessage, String hover2TextMessage, String hover3TextMessage, String commandName, String commandName2, String commandName3);

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
					public TextComponent textRunnable(String hoverText, String hoverText2, String hoverText3, String hoverTextMessage, String hover2TextMessage, String hover3TextMessage, String commandName, String commandName2, String commandName3) {
						return text1_16.textRunnable(hoverText, hoverText2, hoverText3, hoverTextMessage, hover2TextMessage, hover3TextMessage, commandName, commandName2, commandName3);
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
					public TextComponent textRunnable(String hoverText, String hoverText2, String hoverText3, String hoverTextMessage, String hover2TextMessage, String hover3TextMessage, String commandName, String commandName2, String commandName3) {
						return Text_R2.textRunnable(hoverText, hoverText2, hoverText3, hoverTextMessage, hover2TextMessage, hover3TextMessage, commandName, commandName2, commandName3);
					}
				};
			}
		}
		return instance;
	}

}

