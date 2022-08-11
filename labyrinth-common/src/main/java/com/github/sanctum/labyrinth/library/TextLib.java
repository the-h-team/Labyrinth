package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.formatting.component.NewComponent;
import com.github.sanctum.labyrinth.formatting.component.OldComponent;
import com.github.sanctum.labyrinth.formatting.component.WrappedComponent;
import com.github.sanctum.panther.util.Applicable;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author [ms5984, Hempfest]
 */
public abstract class TextLib {
	private static TextLib instance = null;

	protected TextLib() {
	}

	/**
	 * Attach code reference information to a specified component click event.
	 *
	 * @param component the text component to attach runnable information to
	 * @return a wrapped text component with applicable data
	 */
	public WrappedComponent wrap(TextComponent component) {
		return new WrappedComponent(component);
	}

	/**
	 * Run a specific action in the event of clicking the provided component.
	 *
	 * @param action the code to run when this component is clicked
	 * @param component the component to justify action to
	 * @return the new action wrapped component
	 */
	public TextComponent execute(Applicable action, TextComponent component) {
		return wrap(component).accept(action).toReal();
	}

	/**
	 * Run a specific action in the event of clicking the provided component.
	 *
	 * @param action the code to run when this component is clicked
	 * @param component the component to justify action to
	 * @return the new action wrapped component
	 */
	public TextComponent execute(Applicable action, Consumer<TextComponent> component) {
		TextComponent comp = new TextComponent();
		component.accept(comp);
		return wrap(comp).accept(action).toReal();
	}

	/**
	 * Write hover meta logic to an existing {@link TextComponent}.
	 *
	 * @param source the player to format placeholders from
	 * @param component the original component to use
	 * @param messages the hover messages to add to the component
	 * @return a hover formatted text component
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
	 * Write hover meta logic to an existing {@link TextComponent}.
	 *
	 * @param source the player to format placeholders from
	 * @param component the original component to use
	 * @param messages the hover messages to add to the component
	 * @return a hover formatted text component
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
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NH' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textHoverable(String normalText, String hoverText, String hoverTextMessage);

	/**
	 * Meta: {Hoverable}
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NHN' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverTextMessage);

	/**
	 * Meta: {Hoverable}
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NHNH' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message);

	/**
	 * Meta: {Suggestable}
	 * Suggest commands from interaction with the message.
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NH' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textSuggestable(String normalText, String hoverText, String hoverTextMessage, String commandName);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NH' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textRunnable(String normalText, String hoverText, String hoverTextMessage, String commandName);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NHN' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NHNH' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'HHH' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textRunnable(String hoverText, String hoverText2, String hoverText3, String hoverTextMessage, String hover2TextMessage, String hover3TextMessage, String commandName, String commandName2, String commandName3);

	/**
	 * Meta: {Hoverable}
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NH' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage);

	/**
	 * Meta: {Hoverable}
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NHN' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverTextMessage);

	/**
	 * Meta: {Hoverable}
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NHNH' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message);

	/**
	 * Meta: {Suggestable}
	 * Suggest commands from interaction with the message.
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NH' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textSuggestable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage, String commandName);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NH' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage, String commandName);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NHN' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'NHNH' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2);

	/**
	 * Meta: {Runnable}
	 * Run commands from interaction with the message.
	 * <p>
	 * Compiles a color translated text component where N = normal, and H = hoverable
	 * <pre>( AsyncChatEvent -&gt; player -&gt; 'HHH' )</pre>
	 *
	 * @return the compiled TextComponent
	 */
	public abstract TextComponent textRunnable(OfflinePlayer source, String hoverText, String hoverText2, String hoverText3, String hoverTextMessage, String hover2TextMessage, String hover3TextMessage, String commandName, String commandName2, String commandName3);

	/**
	 * Perform operations with the extant {@link TextLib} instance.
	 *
	 * @param library an operation to perform using the instance
	 */
	public static void consume(Consumer<TextLib> library) {
		library.accept(getInstance());
	}

	public static TextLib getInstance() {
		if (instance == null) {
			if (LabyrinthProvider.getInstance().isNew()) {
				instance = new NewComponent();
			} else {
				instance = new OldComponent();
			}
		}
		return instance;
	}

}

