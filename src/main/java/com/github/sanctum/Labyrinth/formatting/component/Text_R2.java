package com.github.sanctum.Labyrinth.formatting.component;


import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class Text_R2 {

	private static String color(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}

	public static TextComponent textHoverable(String normalText, String hoverText, String hoverTextMessage) {
		TextComponent text = new TextComponent(color(normalText));
		TextComponent hover = new TextComponent(color(hoverText));
		text.addExtra(hover);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		return text;
	}

	public static TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverTextMessage) {
		TextComponent text = new TextComponent(color(normalText));
		TextComponent hover = new TextComponent(color(hoverText));
		TextComponent text2 = new TextComponent(color(normalText2));
		text.addExtra(hover);
		text.addExtra(text2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		return text;
	}

	public static TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message) {
		TextComponent text = new TextComponent(color(normalText));
		TextComponent hover = new TextComponent(color(hoverText));
		TextComponent text2 = new TextComponent(color(normalText2));
		TextComponent hover2 = new TextComponent(color(hoverText2));
		text.addExtra(hover);
		text.addExtra(text2);
		text.addExtra(hover2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		hover2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverText2Message)).create()));
		return text;
	}

	public static TextComponent textSuggestable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
		TextComponent text = new TextComponent(color(normalText));
		TextComponent hover = new TextComponent(color(hoverText));
		text.addExtra(hover);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + commandName));
		return text;
	}

	public static TextComponent textRunnable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
		TextComponent text = new TextComponent(color(normalText));
		TextComponent hover = new TextComponent(color(hoverText));
		text.addExtra(hover);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName));
		return text;
	}

	public static TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName) {
		TextComponent text = new TextComponent(color(normalText));
		TextComponent hover = new TextComponent(color(hoverText));
		TextComponent text2 = new TextComponent(color(normalText2));
		text.addExtra(hover);
		text.addExtra(text2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName));
		return text;
	}

	public static TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2) {
		TextComponent text = new TextComponent(color(normalText));
		TextComponent hover = new TextComponent(color(hoverText));
		TextComponent text2 = new TextComponent(color(normalText2));
		TextComponent hover2 = new TextComponent(color(hoverText2));
		text.addExtra(hover);
		text.addExtra(text2);
		text.addExtra(hover2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverTextMessage)).create()));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName));
		hover2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hoverText2Message)).create()));
		hover2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName2));
		return text;
	}

	public static TextComponent textRunnable(String hoverText, String hoverText2, String hoverTextBody3, String hoverTextMessage, String hoverText2Message, String hoverMessage3, String commandName, String commandName2, String commandName3) {
		TextComponent hover = new TextComponent(color(hoverText));
		TextComponent hover2 = new TextComponent(color(hoverText2));
		TextComponent hover3 = new TextComponent(color(hoverTextBody3));
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


}
