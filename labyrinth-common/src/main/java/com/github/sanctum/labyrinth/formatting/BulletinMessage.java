package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.library.Message;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BulletinMessage extends Bulletin {

	@Override
	public Bulletin append(Section text) {
		TEXT.add(text);
		return this;
	}

	@Override
	public Bulletin append(Bulletin message) {
		if (Objects.equals(message, this)) return this;
		TEXT.addAll(message.TEXT);
		return this;
	}

	@Override
	public Bulletin append(@Json String message) {
		TEXT.add(new JsonSection(message));
		return this;
	}

	@Override
	public BaseComponent bake() {
		BaseComponent first = TEXT.get(0).toComponent();
		for (int i = 1; i < TEXT.size(); i++) {
			first.addExtra(TEXT.get(i).toComponent());
		}
		return first;
	}

	@Override
	public BaseComponent[] build() {
		return TEXT.stream().map(Section::toComponent).toArray(BaseComponent[]::new);
	}

	@Override
	public @Json String toJson() {
		return ComponentSerializer.toString(build());
	}

	@Override
	public void send(Player target) {
		Message.form(target).build(build());
	}

	@Override
	public void send(Predicate<Player> predicate) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (predicate.test(p)) {
				Message.form(p).build(build());
			}
		}
	}

	@NotNull
	@Override
	public Iterator<Section> iterator() {
		return TEXT.iterator();
	}

	@Override
	public void forEach(Consumer<? super Section> action) {
		TEXT.forEach(action);
	}

	@Override
	public Spliterator<Section> spliterator() {
		return TEXT.spliterator();
	}

}
