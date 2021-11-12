package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.library.StringUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class ComponentUtil {

	public static void addContent(BaseComponent component, String context) {
		if (component.getHoverEvent() == null) {
			component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(StringUtils.use(context).translate()))));
		} else {
			if (component.getHoverEvent().getContents().size() == 1) {
				component.getHoverEvent().addContent(new Text("\n" + StringUtils.use(context).translate() + "\n" + StringUtils.use("&r").translate()));
			} else {
				component.getHoverEvent().addContent(new Text(StringUtils.use(context).translate() + "\n" + StringUtils.use("&r").translate()));
			}
		}
	}

	public static void addContent(BaseComponent component, String... context) {
		for (String c : context) {
			addContent(component, c);
		}
	}

	public static String serialize(BaseComponent... components) {
		return new ComponentChunk(components).toJson();
	}

	public static BaseComponent[] deserialize(@Json String components) {
		return new MessageBuilder().append(new JsonChunk(components)).build();
	}


}
