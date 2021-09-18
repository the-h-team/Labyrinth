package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.library.Deployable;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @inheritDoc
 */
public class MessageBuilder extends Message {

	public MessageBuilder() {
	}

	@Override
	public Message append(Chunk text) {
		TEXT.add(text);
		return this;
	}

	@Override
	public Message append(Message message) {
		if (Objects.equals(message, this)) return this;
		TEXT.addAll(message.TEXT);
		return this;
	}

	@Override
	public Message append(@Json String message) {
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
		return TEXT.stream().map(Chunk::toComponent).toArray(BaseComponent[]::new);
	}

	@Override
	public @Json
	String toJson() {
		return ComponentSerializer.toString(build());
	}

	@Override
	public Deployable<Void> send(Player target) {
		return Deployable.of(null, unused -> LabyrinthProvider
				.getService(Service.MESSENGER)
				.getEmptyMailer(target)
				.chat(build()).deploy());
	}

	@Override
	public Deployable<Void> send(Predicate<Player> predicate) {
		return Deployable.of(null, unused -> LabyrinthProvider
				.getService(Service.MESSENGER)
				.getEmptyMailer()
				.announce(predicate, build()).deploy());
	}

	@NotNull
	@Override
	public Iterator<Chunk> iterator() {
		return TEXT.iterator();
	}

	@Override
	public void forEach(Consumer<? super Chunk> action) {
		TEXT.forEach(action);
	}

	@Override
	public Spliterator<Chunk> spliterator() {
		return TEXT.spliterator();
	}

}
