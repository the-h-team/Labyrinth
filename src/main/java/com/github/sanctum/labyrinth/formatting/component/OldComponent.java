package com.github.sanctum.labyrinth.formatting.component;


import com.github.sanctum.labyrinth.formatting.string.ColoredString;
import com.github.sanctum.labyrinth.library.StringUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public class OldComponent {

	private static String color(String text) {
		return StringUtils.translate(text);
	}

	private static String color(OfflinePlayer source, String text) {
		return StringUtils.translate(source, text);
	}

	public TextComponent textHoverable(String normalText, String hoverText, String hoverTextMessage) {
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		return text;
	}

	public TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverTextMessage) {
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent text2 = new ColoredString(normalText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		text.addExtra(text2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverTextMessage).create()));
		return text;
	}

	public TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message) {
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent text2 = new ColoredString(normalText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover2 = new ColoredString(hoverText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		text.addExtra(text2);
		text.addExtra(hover2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		hover2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverText2Message)).create()));
		return text;
	}

	public TextComponent textSuggestable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + commandName));
		return text;
	}

	public TextComponent textRunnable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName));
		return text;
	}

	public TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName) {
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent text2 = new ColoredString(normalText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		text.addExtra(text2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName));
		return text;
	}

	public TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2) {
		TextComponent text = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent text2 = new ColoredString(normalText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover2 = new ColoredString(hoverText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		text.addExtra(text2);
		text.addExtra(hover2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName));
		hover2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverText2Message)).create()));
		hover2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName2));
		return text;
	}

	public TextComponent textRunnable(String hoverText, String hoverText2, String hoverTextBody3, String hoverTextMessage, String hoverText2Message, String hoverMessage3, String commandName, String commandName2, String commandName3) {
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover2 = new ColoredString(hoverText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover3 = new ColoredString(hoverTextBody3, ColoredString.ColorType.MC_COMPONENT).toComponent();
		hover.addExtra(hover2);
		hover.addExtra(hover3);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName));
		hover2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverText2Message)).create()));
		hover2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName2));
		hover3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverMessage3)).create()));
		hover3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName3));
		return hover;
	}

	public TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage) {
		normalText = PlaceholderAPI.setPlaceholders(source, normalText);
		hoverText = PlaceholderAPI.setPlaceholders(source, hoverText);
		hoverTextMessage = PlaceholderAPI.setPlaceholders(source, hoverTextMessage);
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(source, hoverTextMessage)).create()));
		return text;
	}

	public TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverTextMessage) {
		normalText = PlaceholderAPI.setPlaceholders(source, normalText);
		hoverText = PlaceholderAPI.setPlaceholders(source, hoverText);
		hoverTextMessage = PlaceholderAPI.setPlaceholders(source, hoverTextMessage);
		normalText2 = PlaceholderAPI.setPlaceholders(source, normalText2);
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent text2 = new ColoredString(normalText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		text.addExtra(text2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(source, hoverTextMessage)).create()));
		return text;
	}

	public TextComponent textHoverable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message) {
		normalText = PlaceholderAPI.setPlaceholders(source, normalText);
		hoverText = PlaceholderAPI.setPlaceholders(source, hoverText);
		hoverTextMessage = PlaceholderAPI.setPlaceholders(source, hoverTextMessage);
		hoverText2Message = PlaceholderAPI.setPlaceholders(source, hoverText2Message);
		normalText2 = PlaceholderAPI.setPlaceholders(source, normalText2);
		hoverText2 = PlaceholderAPI.setPlaceholders(source, hoverText2);
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent text2 = new ColoredString(normalText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover2 = new ColoredString(hoverText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		text.addExtra(text2);
		text.addExtra(hover2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(source, hoverTextMessage)).create()));
		hover2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(source, hoverText2Message)).create()));
		return text;
	}
	// *
	//
	// SEND TEXT THE PLAYER CAN BE SUGGESTED TO EXECUTE COMMANDS WITH
	//

	public TextComponent textSuggestable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage, String commandName) {
		normalText = PlaceholderAPI.setPlaceholders(source, normalText);
		hoverText = PlaceholderAPI.setPlaceholders(source, hoverText);
		hoverTextMessage = PlaceholderAPI.setPlaceholders(source, hoverTextMessage);
		commandName = PlaceholderAPI.setPlaceholders(source, commandName);
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(source, hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + commandName));
		return text;
	}
	// *
	//
	// SEND TEXT THE PLAYER CAN RUN COMMANDS WITH
	//

	public TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String hoverTextMessage, String commandName) {
		normalText = PlaceholderAPI.setPlaceholders(source, normalText);
		hoverText = PlaceholderAPI.setPlaceholders(source, hoverText);
		hoverTextMessage = PlaceholderAPI.setPlaceholders(source, hoverTextMessage);
		commandName = PlaceholderAPI.setPlaceholders(source, commandName);
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(source, hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName));
		return text;
	}

	public TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName) {
		normalText = PlaceholderAPI.setPlaceholders(source, normalText);
		normalText2 = PlaceholderAPI.setPlaceholders(source, normalText2);
		hoverText = PlaceholderAPI.setPlaceholders(source, hoverText);
		hoverTextMessage = PlaceholderAPI.setPlaceholders(source, hoverTextMessage);
		commandName = PlaceholderAPI.setPlaceholders(source, commandName);
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent text2 = new ColoredString(normalText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		text.addExtra(text2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(source, hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName));
		return text;
	}

	public TextComponent textRunnable(OfflinePlayer source, String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2) {
		normalText = PlaceholderAPI.setPlaceholders(source, normalText);
		normalText2 = PlaceholderAPI.setPlaceholders(source, normalText2);
		hoverText = PlaceholderAPI.setPlaceholders(source, hoverText);
		hoverText2 = PlaceholderAPI.setPlaceholders(source, hoverText2);
		hoverTextMessage = PlaceholderAPI.setPlaceholders(source, hoverTextMessage);
		hoverText2Message = PlaceholderAPI.setPlaceholders(source, hoverText2Message);
		commandName = PlaceholderAPI.setPlaceholders(source, commandName);
		commandName2 = PlaceholderAPI.setPlaceholders(source, commandName2);
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent text2 = new ColoredString(normalText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover2 = new ColoredString(hoverText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		text.addExtra(text2);
		text.addExtra(hover2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(source, hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName));
		hover2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(source, hoverText2Message)).create()));
		hover2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName2));
		return text;
	}

	public TextComponent textRunnable(OfflinePlayer source, String hoverText, String hoverText2, String hoverTextBody3, String hoverTextMessage, String hoverText2Message, String hoverMessage3, String commandName, String commandName2, String commandName3) {
		hoverText = PlaceholderAPI.setPlaceholders(source, hoverText);
		hoverText2 = PlaceholderAPI.setPlaceholders(source, hoverText2);
		hoverTextBody3 = PlaceholderAPI.setPlaceholders(source, hoverTextBody3);
		hoverTextMessage = PlaceholderAPI.setPlaceholders(source, hoverTextMessage);
		hoverText2Message = PlaceholderAPI.setPlaceholders(source, hoverText2Message);
		hoverMessage3 = PlaceholderAPI.setPlaceholders(source, hoverMessage3);
		commandName = PlaceholderAPI.setPlaceholders(source, commandName);
		commandName2 = PlaceholderAPI.setPlaceholders(source, commandName2);
		commandName3 = PlaceholderAPI.setPlaceholders(source, commandName3);
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover2 = new ColoredString(hoverText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover3 = new ColoredString(hoverTextBody3, ColoredString.ColorType.MC_COMPONENT).toComponent();
		hover.addExtra(hover2);
		hover.addExtra(hover3);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(source, hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName));
		hover2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(source, hoverText2Message)).create()));
		hover2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName2));
		hover3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(source, hoverMessage3)).create()));
		hover3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName3));
		return hover;
	}


}
