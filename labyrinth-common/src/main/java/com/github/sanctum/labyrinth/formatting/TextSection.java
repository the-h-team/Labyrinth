package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.labyrinth.library.ListUtils;
import com.github.sanctum.labyrinth.library.StringUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Color;

public class TextSection extends Message.Chunk {

	private final Map<ToolTip.Type, ToolTip<?>> CONTEXT = new HashMap<>();
	private String text;
	private String style;
	private String color;

	public TextSection(String text) {
		this.text = text;
	}

	public TextSection(String text, Color color) {
		String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
		this.text = hex + text;
	}

	public TextSection(String text, ChatColor color) {
		this.text = color + text;
	}

	public TextSection(String text, CustomColor color) {
		this.text = color.context(text).join();
	}

	@Override
	public TextSection append(String text) {
		this.text = this.text + text;
		return this;
	}

	@Override
	public Message.Chunk append(int i) {
		return append(String.valueOf(i));
	}

	@Override
	public Message.Chunk append(double d) {
		return append(String.valueOf(d));
	}

	@Override
	public Message.Chunk append(long l) {
		return append(String.valueOf(l));
	}

	@Override
	public TextSection style(ChatColor style) {
		List<ChatColor> targets =
				Arrays.asList(ChatColor.BOLD,
						ChatColor.ITALIC,
						ChatColor.UNDERLINE,
						ChatColor.STRIKETHROUGH,
						ChatColor.RESET,
						ChatColor.MAGIC);
		if (targets.contains(style)) {
			this.style = style.toString();
		} else throw new IllegalArgumentException("Message: Invalid text style provided.");
		return this;
	}

	@Override
	public Message.Chunk style(ChatColor... style) {
		List<ChatColor> targets =
				Arrays.asList(ChatColor.BOLD,
						ChatColor.ITALIC,
						ChatColor.UNDERLINE,
						ChatColor.STRIKETHROUGH,
						ChatColor.RESET,
						ChatColor.MAGIC);
		for (ChatColor s : style) {
			if (!targets.contains(s)) {
				throw new IllegalArgumentException("Message: Invalid text style provided.");
			}
		}
		this.style = ListUtils.use(style).join(colors -> colors.stream().map(ChatColor::toString).collect(Collectors.joining()));
		return this;
	}

	@Override
	public TextSection style(CustomColor color) {
		String stripped = net.md_5.bungee.api.ChatColor.stripColor(StringUtils.use(this.text).translate());
		this.color = null;
		this.style = null;
		this.text = color.context(stripped).join();
		return this;
	}

	@Override
	public Message.Chunk color(ChatColor color) {
		this.color = color.toString();
		return this;
	}

	@Override
	public Message.Chunk color(Color color) {
		this.color = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
		return this;
	}

	@Override
	public TextSection bind(ToolTip<?> context) {
		this.CONTEXT.put(context.getType(), context);
		return this;
	}

	@Override
	public TextSection setText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public BaseComponent toComponent() {
		TextComponent component = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(this.text).translate()));
		if (this.style != null) {
			if (this.color != null) {
				component = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(this.color + this.style + this.text).translate()));
			} else component = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(this.style + this.text).translate()));
		} else if (this.color != null) {
			component = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(this.color + this.text).translate()));
		}
		for (ToolTip<?> context : this.CONTEXT.values()) {
			switch (context.getType()) {
				case COMMAND:
					component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, (String)context.get()));
					break;
				case HOVER:
					if (context instanceof ToolTip.Text) {
						if (component.getHoverEvent() == null) {
							if (LabyrinthProvider.getInstance().isLegacy()) {
								component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(StringUtils.use((String) context.get()).translate()).create()));
							} else {
								component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(TextComponent.fromLegacyText(StringUtils.use((String) context.get()).translate()))));
							}
						} else {
							if (LabyrinthProvider.getInstance().isLegacy()) {
								component.getHoverEvent().addContent(new Text(new ComponentBuilder(StringUtils.use((String) context.get()).translate()).create()));
							} else {
								component.getHoverEvent().addContent(new net.md_5.bungee.api.chat.hover.content.Text(TextComponent.fromLegacyText(StringUtils.use((String) context.get()).translate())));
							}
						}
					}
					if (context instanceof ToolTip.Item) {
						ToolTip.Item item = (ToolTip.Item)context;
						BaseComponent[] components = new BaseComponent[]{new TextComponent(item.toJson())};
						if (component.getHoverEvent() == null) {
							component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, components));
						} else {
							component.getHoverEvent().addContent(new Text(components));
						}
					}
					break;
				case ACTION:
					ToolTip.Action action = (ToolTip.Action)context;
					LabyrinthProvider.getInstance().registerComponent(action).deploy();
					component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + action.getId()));
					break;
				case SUGGEST:
					component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, (String) context.get()));
					break;
				case URL:
					component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, (String) context.get()));
					break;
				case COPY:
					component.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, (String) context.get()));
					break;
			}
		}
		return component;
	}
}
