package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.formatting.string.ColoredString;
import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.labyrinth.formatting.string.FormattedString;
import com.github.sanctum.labyrinth.library.ListUtils;
import com.github.sanctum.labyrinth.library.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

public class ComponentChunk extends Message.Chunk {

	private final List<ToolTip<?>> CONTEXT = new ArrayList<>();
	private TextComponent parent;
	private String color;
	private String style;

	public ComponentChunk(BaseComponent... parent) {
		this.parent = new TextComponent(parent);
	}

	@Override
	public ComponentChunk append(String text) {
		String now = parent.getText();
		parent.setText(now + text);
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
	public ComponentChunk style(ChatColor style) {
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
	public ComponentChunk style(CustomColor color) {
		String now = parent.getText();
		this.style = null;
		this.color = null;
		String stripped = net.md_5.bungee.api.ChatColor.stripColor(StringUtils.use(now).translate());
		parent.setText(color.context(stripped).join());
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
	public ComponentChunk bind(ToolTip<?> context) {
		this.CONTEXT.add(context);
		return this;
	}

	@Override
	public ComponentChunk setText(String text) {
		parent.setText(text);
		return this;
	}

	@Override
	public Message.Chunk replace(String text, String replacement) {
		TextComponent replace = new TextComponent();
		List<BaseComponent> components = new ArrayList<>();
		for (BaseComponent c : parent.getExtra()) {
			TextComponent n = new TextComponent(new FormattedString(c.toLegacyText()).replace(text, replacement).get());
			ComponentUtil.copyMeta(c, n);
			components.add(n);
		}
		replace.setExtra(components);
		this.parent = replace;
		return this;
	}

	List<BaseComponent> getAll(Function<String, String> function, List<BaseComponent> list) {
		List<BaseComponent> components = new ArrayList<>();
		for (BaseComponent c : list) {
			if (c.getExtra() != null) {
				for (BaseComponent inner : getAll(function, c.getExtra())) {
					TextComponent t = new ColoredString(function.apply(inner.toLegacyText()), ColoredString.ColorType.MC_COMPONENT).toComponent();
					ComponentUtil.copyMeta(inner, t);
					components.add(t);
				}
			} else {
				TextComponent t = new ColoredString(function.apply(c.toLegacyText()), ColoredString.ColorType.MC_COMPONENT).toComponent();
				ComponentUtil.copyMeta(c, t);
				components.add(t);
			}
		}
		return components;
	}

	public Message.Chunk map(Function<String, String> function) {
		TextComponent replace = new TextComponent();
		List<BaseComponent> components = getAll(function, parent.getExtra());
		replace.setExtra(components);
		this.parent = replace;
		return this;
	}

	@Override
	public String getText() {
		return parent.getText();
	}

	@Override
	public BaseComponent toComponent() {
		if (this.style != null) {
			if (this.color != null) {
				String now = parent.getText();
				parent.setText(color + style + now);
			} else parent.setText(style + parent.getText());
		} else if (this.color != null) {
			String now = parent.getText();
			parent.setText(color + now);
		}
		List<String> adjusted = new ArrayList<>();
		this.CONTEXT.stream().filter(t -> t instanceof ToolTip.Text).forEach(toolTip -> adjusted.add(((ToolTip.Text) toolTip).get()));
		ListUtils.use(adjusted).append(object -> object + "\n").forEach(context -> {
			if (LabyrinthProvider.getInstance().isLegacy()) {
				parent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(StringUtils.use(context).translate()).create()));
			} else {
				ComponentUtil.addContent(parent, context);
			}
		});

		for (ToolTip<?> context : this.CONTEXT) {
			switch (context.getType()) {
				case COMMAND:
					parent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, (String) context.get()));
					break;
				case HOVER:
					if (context instanceof ToolTip.Item) {
						ToolTip.Item item = (ToolTip.Item) context;
						BaseComponent[] components = new BaseComponent[]{new TextComponent(item.toJson())};
						if (parent.getHoverEvent() == null) {
							parent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, components));
						}
					}
					break;
				case ACTION:
					ToolTip.Action action = (ToolTip.Action) context;
					LabyrinthProvider.getInstance().registerComponent(action).deploy();
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

	@NotNull
	@Override
	public Iterator<ToolTip<?>> iterator() {
		return CONTEXT.iterator();
	}

	@Override
	public void forEach(Consumer<? super ToolTip<?>> action) {
		CONTEXT.forEach(action);
	}

	@Override
	public Spliterator<ToolTip<?>> spliterator() {
		return CONTEXT.spliterator();
	}

}
