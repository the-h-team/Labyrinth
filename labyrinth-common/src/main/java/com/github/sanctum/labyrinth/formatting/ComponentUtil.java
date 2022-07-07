package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.library.StringUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.jetbrains.annotations.NotNull;

public class ComponentUtil {

	public static void addContent(BaseComponent component, String context) {
		if (component.getHoverEvent() == null) {
			component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(StringUtils.use(context).translate()))));
		} else {
			component.getHoverEvent().addContent(new Text(TextComponent.fromLegacyText(StringUtils.use(context).translate())));
		}
	}

	public static void addContent(BaseComponent component, String... context) {
		for (String c : context) {
			addContent(component, c);
		}
	}

	public static void copyMeta(@NotNull BaseComponent component, @NotNull TextComponent target) {
		if (component.hasFormatting()) target.copyFormatting(component);
		if (component.getHoverEvent() != null) target.setHoverEvent(component.getHoverEvent());
		if (component.getClickEvent() != null) target.setClickEvent(component.getClickEvent());
	}

	public static String serialize(BaseComponent... components) {
		return new ComponentChunk(components).toJson();
	}

	public static BaseComponent[] deserialize(@Json String components) {
		return new MessageBuilder().append(new JsonChunk(components)).build();
	}


}
