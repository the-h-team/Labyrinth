package com.github.sanctum.labyrinth.formatting.component;

import com.github.sanctum.labyrinth.formatting.string.ColoredString;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class NewComponent {
	
	private static String color(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public TextComponent textHoverable(String normalText, String hoverText, String hoverTextMessage) {
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new net.md_5.bungee.api.chat.hover.content.Text(color(hoverTextMessage)))));
		return text;
	}
	
	public TextComponent textHoverable(String normalText, String hoverText, String normalText2, String hoverTextMessage) {
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent text2 = new ColoredString(normalText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		text.addExtra(text2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new net.md_5.bungee.api.chat.hover.content.Text(color(hoverTextMessage))))); 
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
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new net.md_5.bungee.api.chat.hover.content.Text(color(hoverTextMessage)))));
		hover2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new net.md_5.bungee.api.chat.hover.content.Text(color(hoverText2Message))))); 
		return text;
	}
		// *
		//
		// SEND TEXT THE PLAYER CAN BE SUGGESTED TO EXECUTE COMMANDS WITH
		//
	
	public TextComponent textSuggestable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new net.md_5.bungee.api.chat.hover.content.Text(color(hoverTextMessage)))));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + commandName));
		return text;
	}
		// *
		//
		// SEND TEXT THE PLAYER CAN RUN COMMANDS WITH
		//
	
	public TextComponent textRunnable(String normalText, String hoverText, String hoverTextMessage, String commandName) {
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new net.md_5.bungee.api.chat.hover.content.Text(new ColoredString(hoverTextMessage, ColoredString.ColorType.HEX).toString()))));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName)); 
		return text;
	}
	
	public TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverTextMessage, String commandName) {
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent text2 = new ColoredString(normalText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		text.addExtra(text2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new net.md_5.bungee.api.chat.hover.content.Text(new ColoredString(hoverTextMessage, ColoredString.ColorType.HEX).toString()))));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName)); 
		return text;
	}
	
	public TextComponent textRunnable(String normalText, String hoverText, String normalText2, String hoverText2, String hoverTextMessage, String hoverText2Message, String commandName, String commandName2) {
		TextComponent text = new ColoredString(normalText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent text2 = new ColoredString(normalText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover2 = new ColoredString(hoverText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		text.addExtra(hover);
		text.addExtra(text2);
		text.addExtra(hover2);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new net.md_5.bungee.api.chat.hover.content.Text(new ColoredString(hoverTextMessage, ColoredString.ColorType.HEX).toString()))));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName));
		hover2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new net.md_5.bungee.api.chat.hover.content.Text(new ColoredString(hoverText2Message, ColoredString.ColorType.HEX).toString()))));
		hover2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName2));
		return text;
	}
	
	public TextComponent textRunnable(String hoverText, String hoverText2, String hoverTextBody3, String hoverTextMessage, String hoverText2Message, String hoverMessage3, String commandName, String commandName2, String commandName3) {
		TextComponent hover = new ColoredString(hoverText, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover2 = new ColoredString(hoverText2, ColoredString.ColorType.MC_COMPONENT).toComponent();
		TextComponent hover3 = new ColoredString(hoverTextBody3, ColoredString.ColorType.MC_COMPONENT).toComponent();
		hover.addExtra(hover2);
		hover.addExtra(hover3);
		hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new net.md_5.bungee.api.chat.hover.content.Text(new ColoredString(hoverTextMessage, ColoredString.ColorType.HEX).toString()))));
		hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName));
		hover2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new net.md_5.bungee.api.chat.hover.content.Text(new ColoredString(hoverText2Message, ColoredString.ColorType.HEX).toString()))));
		hover2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName2));
		hover3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new net.md_5.bungee.api.chat.hover.content.Text(new ColoredString(hoverMessage3, ColoredString.ColorType.HEX).toString()))));
		hover3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName3));
		return hover;
	}
	
	

}
