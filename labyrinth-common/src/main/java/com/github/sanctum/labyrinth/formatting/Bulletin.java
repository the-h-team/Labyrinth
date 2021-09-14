package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.annotation.Json;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

public abstract class Bulletin implements Iterable<Section> {

	protected final List<Section> TEXT = new ArrayList<>();

	public abstract Bulletin append(Section text);

	public abstract Bulletin append(Bulletin message);

	public abstract Bulletin append(@Json String message);

	public abstract BaseComponent bake();

	public abstract BaseComponent[] build();

	public abstract @Json String toJson();

	public abstract void send(Player target);

	public abstract void send(Predicate<Player> predicate);

	public interface Factory extends Section.Factory, ToolTip.Factory{

		default Bulletin message() {
			return new BulletinMessage();
		}

	}
}
