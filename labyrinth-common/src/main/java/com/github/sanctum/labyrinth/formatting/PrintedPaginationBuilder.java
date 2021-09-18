package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.library.Mailer;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.TextLib;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class PrintedPaginationBuilder {

	private final List<BaseComponent> toSend = new LinkedList<>();

	private final int max;

	private String prefix = "";

	private String suffix = "";

	private int page;

	private Player player;


	protected PrintedPaginationBuilder(int max) {
		this.max = max;
	}


	public PrintedPaginationBuilder setPlayer(Player player) {
		this.player = player;
		return this;
	}

	public PrintedPaginationBuilder setPage(int page) {
		this.page = page;
		return this;
	}

	public PrintedPaginationBuilder setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public PrintedPaginationBuilder setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}

	<T> void build(PaginatedList<T> list) {
		TextLib component = TextLib.getInstance();
		int next = page + 1;
		int last = Math.max(page - 1, 1);
		if (!this.prefix.isEmpty()) {
			Mailer.empty(player).chat(this.prefix).deploy();
		}
		List<BaseComponent> toSend = new LinkedList<>();
		if (page == 1) {
			if (page == max) {
				toSend.add(component.textHoverable("", "&8« ", "&cYou are on the first page already."));
				toSend.add(component.textHoverable("&f<&7" + page + "&f/&7" + max + "&f>", "", ""));
				toSend.add(component.textHoverable("", " &8»", "&cYou are already on the last page."));
				player.spigot().sendMessage(toSend.toArray(new BaseComponent[0]));
				return;
			}
			toSend.add(component.textHoverable("", "&8« ", "&cYou are on the first page already."));
			toSend.add(component.textHoverable("&f<&7" + page + "&f/&7" + max + "&f>", "", ""));
			toSend.add(component.execute(() -> list.get(next), component.textHoverable("", " &3»", "&aGoto the next page.")));
			player.spigot().sendMessage(toSend.toArray(new BaseComponent[0]));
			return;
		}
		if (page == max) {
			toSend.add(component.execute(() -> list.get(last), component.textHoverable("", "&3« ", "&aGoto the previous page.")));
			toSend.add(component.textHoverable("&f<&7" + page + "&f/&7" + max + "&f>", "", ""));
			toSend.add(component.textHoverable("", " &8»", "&cYou are already on the last page."));
			player.spigot().sendMessage(toSend.toArray(new BaseComponent[0]));
			return;
		}
		if (next <= max) {
			toSend.add(component.execute(() -> list.get(last), component.textHoverable("", "&3« ", "&aGoto the previous page.")));
			toSend.add(component.textHoverable("&f<&7" + page + "&f/&7" + max + "&f>", "", ""));
			toSend.add(component.execute(() -> list.get(next), component.textHoverable("", " &3»", "&aGoto the next page.")));
			player.spigot().sendMessage(toSend.toArray(new BaseComponent[0]));
		}
		if (!this.suffix.isEmpty()) {
			Mailer.empty(this.player).chat(this.suffix).deploy();
		}
	}
}
