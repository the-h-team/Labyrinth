package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.formatting.component.NewComponent;
import com.github.sanctum.labyrinth.formatting.component.OldComponent;
import com.github.sanctum.labyrinth.formatting.string.WrappedComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class TextLib {
	private static TextLib instance = null;

	private TextLib() {
	}

	/**
	 * Attach code reference information to a specified component click event.
	 *
	 * @param component The text component to attach runnable information to.
	 * @return A wrapped text component with applicable data.
	 */
	public WrappedComponent wrap(TextComponent component) {
		return new WrappedComponent(component);
	}

	/**
	 * Run a specific action in the event of clicking the provided component.
	 *
	 * @param action    The code to run when this component is clicked.
	 * @param component The component to justify action to.
	 * @return The newly action wrapped component.
	 */
	public TextComponent execute(Applicable action, TextComponent component) {
		return wrap(component).accept(action).toReal();
	}

	/**
	 * Run a specific action in the event of clicking the provided component.
	 *
	 * @param action    The code to run when this component is clicked.
	 * @param component The component to justify action to.
	 * @return The newly action wrapped component.
	 */
	public TextComponent execute(Applicable action, Consumer<TextComponent> component) {
		TextComponent comp = new TextComponent();
		component.accept(comp);
		return wrap(comp).accept(action).toReal();
	}

	/**
	 * Write hover meta logic to an existing {@link TextComponent}
	 *
	 * @param source    The player to format placeholders from.
	 * @param component The original component to use.
	 * @param messages  The hover messages to add to the component.
	 * @return A hover formatted text component.
	 */
	public TextComponent format(Player source, Consumer<TextComponent> component, String... messages) {
		TextComponent comp = new TextComponent();
		component.accept(comp);
		List<Content> array = new ArrayList<>();
		for (String msg : messages) {
			array.add(new Text(StringUtils.use(msg).translate(source) + "\n"));
		}
		comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, array));
		return comp;
	}

	/**
	 * Write hover meta logic to an existing {@link TextComponent}
	 *
	 * @param source    The player to format placeholders from.
	 * @param component The original component to use.
	 * @param messages  The hover messages to add to the component.
	 * @return A hover formatted text component.
	 */
	public TextComponent format(Player source, TextComponent component, String... messages) {
		List<Content> array = new ArrayList<>();
		for (String msg : messages) {
			array.add(new Text(StringUtils.use(msg).translate(source) + "\n"));
		}
		component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, array));
		return component;
	}

	/**
	 * Meta: {Hoverable}
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NH' )
	 *
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

	/**
	 * Meta: {Hoverable}
	 *
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NH' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage);

	/**
	 * Meta: {Hoverable}
	 *
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NHN' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverTextMessage);

	/**
	 * Meta: {Hoverable}
	 *
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NHNH' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message);

	/**
	 * Meta: {Suggestable}
	 * Suggest commands from interaction with the message.
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NH' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textSuggestable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage, String commandName);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NH' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage, String commandName);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NHN' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'NHNH' )
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * ( AsyncChatEvent -> player -> 'HHH' )
	 *
	 * @return The compiled TextComponent object.
	 */
	public abstract TextComponent textRunnable(OfflinePlayer source, String hoverText, String hoverText2, String hoverText3, String hoverTextMessage, String hover2TextMessage, String hover3TextMessage, String commandName, String commandName2, String commandName3);

	/**
	 * Encapsulate an already instantiated {@link TextLib} instance
	 *
	 * @param library The operations to run whilst using the text library.
	 */
	public static void consume(Consumer<TextLib> library) {
		library.accept(getInstance());
	}

	public static TextLib getInstance() {
		if (instance == null) {
			if (Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")) {
				instance = new TextLib() {
					final NewComponent textNew = new NewComponent();

					@Override
					public TextComponent textHoverable(String normalText, String hoverText, String hoverTextMessage) {
						return textNew.textHoverable(normalText, hoverText, hoverTextMessage);
					}

					@Override
					public TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverTextMessage) {
						return textNew.textHoverable(normalText, hoverText, normalText2, hoverTextMessage);
					}

					@Override
					public TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message) {
						return textNew.textHoverable(normalText, hoverText, normalText2, hoverText2, hoverTextMessage, hoverText2Message);
					}

					@Override
					public TextComponent textSuggestable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
						return textNew.textSuggestable(normalText, hoverText, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
						return textNew.textRunnable(normalText, hoverText, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName) {
						return textNew.textRunnable(normalText, hoverText, normalText2, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2) {
						return textNew.textRunnable(normalText, hoverText, normalText2, hoverText2, hoverTextMessage, hoverText2Message, commandName, commandName2);
					}

					@Override
					public TextComponent textRunnable(String hoverText, String hoverText2, String hoverText3, String hoverTextMessage, String hover2TextMessage, String hover3TextMessage, String commandName, String commandName2, String commandName3) {
						return textNew.textRunnable(hoverText, hoverText2, hoverText3, hoverTextMessage, hover2TextMessage, hover3TextMessage, commandName, commandName2, commandName3);
					}

					@Override
					public TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage) {
						return textNew.textHoverable(source, normalText, hoverText, hoverTextMessage);
					}

					@Override
					public TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverTextMessage) {
						return textNew.textHoverable(source, normalText, hoverText, normalText2, hoverTextMessage);
					}

					@Override
					public TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message) {
						return textNew.textHoverable(source, normalText, hoverText, normalText2, hoverText2, hoverTextMessage, hoverText2Message);
					}

					@Override
					public TextComponent textSuggestable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage, String commandName) {
						return textNew.textSuggestable(source, normalText, hoverText, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage, String commandName) {
						return textNew.textRunnable(source, normalText, hoverText, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName) {
						return textNew.textRunnable(source, normalText, hoverText, normalText2, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2) {
						return textNew.textRunnable(source, normalText, hoverText, normalText2, hoverText2, hoverTextMessage, hoverText2Message, commandName, commandName2);
					}

					@Override
					public TextComponent textRunnable(OfflinePlayer source, String hoverText, String hoverText2, String hoverText3, String hoverTextMessage, String hover2TextMessage, String hover3TextMessage, String commandName, String commandName2, String commandName3) {
						return textNew.textRunnable(source, hoverText, hoverText2, hoverText3, hoverTextMessage, hover2TextMessage, hover3TextMessage, commandName, commandName2, commandName3);
					}
				};
			} else {
				instance = new TextLib() {
					final OldComponent textOld = new OldComponent();
					@Override
					public TextComponent textHoverable(String normalText, String hoverText, String hoverTextMessage) {
						return textOld.textHoverable(normalText, hoverText, hoverTextMessage);
					}

					@Override
					public TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverTextMessage) {
						return textOld.textHoverable(normalText, hoverText, normalText2, hoverTextMessage);
					}

					@Override
					public TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message) {
						return textOld.textHoverable(normalText, hoverText, normalText2, hoverText2, hoverTextMessage, hoverText2Message);
					}

					@Override
					public TextComponent textSuggestable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
						return textOld.textSuggestable(normalText, hoverText, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
						return textOld.textRunnable(normalText, hoverText, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName) {
						return textOld.textRunnable(normalText, hoverText, normalText2, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2) {
						return textOld.textRunnable(normalText, hoverText, normalText2, hoverText2, hoverTextMessage, hoverText2Message, commandName, commandName2);
					}

					@Override
					public TextComponent textRunnable(String hoverText, String hoverText2, String hoverText3, String hoverTextMessage, String hover2TextMessage, String hover3TextMessage, String commandName, String commandName2, String commandName3) {
						return textOld.textRunnable(hoverText, hoverText2, hoverText3, hoverTextMessage, hover2TextMessage, hover3TextMessage, commandName, commandName2, commandName3);
					}

					@Override
					public TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage) {
						return textOld.textHoverable(source, normalText, hoverText, hoverTextMessage);
					}

					@Override
					public TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverTextMessage) {
						return textOld.textHoverable(source, normalText, hoverText, normalText2, hoverTextMessage);
					}

					@Override
					public TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message) {
						return textOld.textHoverable(source, normalText, hoverText, normalText2, hoverText2, hoverTextMessage, hoverText2Message);
					}

					@Override
					public TextComponent textSuggestable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage, String commandName) {
						return textOld.textSuggestable(source, normalText, hoverText, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage, String commandName) {
						return textOld.textRunnable(source, normalText, hoverText, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName) {
						return textOld.textRunnable(source, normalText, hoverText, normalText2, hoverTextMessage, commandName);
					}

					@Override
					public TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2) {
						return textOld.textRunnable(source, normalText, hoverText, normalText2, hoverText2, hoverTextMessage, hoverText2Message, commandName, commandName2);
					}

					@Override
					public TextComponent textRunnable(OfflinePlayer source, String hoverText, String hoverText2, String hoverText3, String hoverTextMessage, String hover2TextMessage, String hover3TextMessage, String commandName, String commandName2, String commandName3) {
						return textOld.textRunnable(source, hoverText, hoverText2, hoverText3, hoverTextMessage, hover2TextMessage, hover3TextMessage, commandName, commandName2, commandName3);
					}
				};
			}
		}
		return instance;
	}

}

