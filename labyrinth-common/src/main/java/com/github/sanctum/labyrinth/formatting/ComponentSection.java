package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.labyrinth.library.StringUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Color;

public class ComponentSection extends Section {

	private final Map<ToolTip.Type, ToolTip<?>> CONTEXT = new HashMap<>();
	private final TextComponent parent;

	public ComponentSection(BaseComponent[] parent) {
		this.parent = new TextComponent(parent);
	}

	@Override
	public ComponentSection append(String text) {
		String now = parent.getText();
		parent.setText(now + text);
		return this;
	}

	@Override
	public ComponentSection style(ChatColor style) {
		List<ChatColor> targets =
				Arrays.asList(ChatColor.BOLD,
						ChatColor.ITALIC,
						ChatColor.UNDERLINE,
						ChatColor.STRIKETHROUGH,
						ChatColor.RESET,
						ChatColor.MAGIC);
		if (targets.contains(style)) {
			String now = parent.getText();
			parent.setText(style + now);
		} else throw new IllegalArgumentException("Bulletin: Invalid text style provided.");
		return this;
	}

	@Override
	public ComponentSection style(CustomColor color) {
		String now = parent.getText();
		String stripped = net.md_5.bungee.api.ChatColor.stripColor(StringUtils.use(now).translate());
		parent.setText(color.context(stripped).join());
		return this;
	}

	@Override
	public Section color(ChatColor color) {
		String now = parent.getText();
		parent.setText(color + now);
		return this;
	}

	@Override
	public Section color(Color color) {
		String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
		String now = parent.getText();
		parent.setText(hex + now);
		return this;
	}

	@Override
	public ComponentSection bind(ToolTip<?> context) {
		this.CONTEXT.put(context.getType(), context);
		return this;
	}

	@Override
	public ComponentSection setText(String text) {
		parent.setText(text);
		return this;
	}

	@Override
	public String getText() {
		return parent.getText();
	}

	@Override
	public BaseComponent toComponent() {
		for (ToolTip<?> context : this.CONTEXT.values()) {
			switch (context.getType()) {
				case COMMAND:
					parent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, (String)context.get()));
					break;
				case HOVER:
					if (context instanceof ToolTip.Text) {
						if (parent.getHoverEvent() == null) {
							if (LabyrinthProvider.getInstance().isLegacy()) {
								parent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(StringUtils.use((String) context.get()).translate()).create()));
							} else {
								parent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(StringUtils.use((String) context.get()).translate()))));
							}
						} else {
							if (LabyrinthProvider.getInstance().isLegacy()) {
								parent.getHoverEvent().addContent(new Text(new ComponentBuilder(StringUtils.use((String) context.get()).translate()).create()));
							} else {
								parent.getHoverEvent().addContent(new Text(TextComponent.fromLegacyText(StringUtils.use((String) context.get()).translate())));
							}
						}
					}
					if (context instanceof ToolTip.Item) {
						ToolTip.Item item = (ToolTip.Item)context;
						BaseComponent[] components = new BaseComponent[]{new TextComponent(item.toJson())};
						if (parent.getHoverEvent() == null) {
							parent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, components));
						} else {
							parent.getHoverEvent().addContent(new Text(components));
						}
					}
					break;
				case ACTION:
					ToolTip.Action action = (ToolTip.Action)context;
					LabyrinthProvider.getInstance().addComponent(action);
					parent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + action.getId()));
					break;
				case SUGGEST:
					parent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, (String) context.get()));
					break;
				case URL:
					parent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, (String) context.get()));
					break;
				case COPY:
					parent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, (String) context.get()));
					break;
			}
		}
		return parent;
	}
}
