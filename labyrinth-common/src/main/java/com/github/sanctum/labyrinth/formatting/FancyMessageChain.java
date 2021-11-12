package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.library.Deployable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FancyMessageChain implements Iterable<Message>{

	private final List<Message> messages = new ArrayList<>();

	public FancyMessageChain append(Message... message) {
		messages.addAll(Arrays.asList(message));
		return this;
	}

	public FancyMessageChain append(Consumer<FancyMessage> layout) {
		FancyMessage message = new FancyMessage();
		layout.accept(message);
		messages.add(message);
		return this;
	}

	public Message get(int index) {
		return messages.get(index);
	}

	public int count() {
		return messages.size();
	}

	public void clear() {
		messages.clear();
	}

	public List<Message> getMessages() {
		return messages;
	}

	public Deployable<Void> send(Player target) {
		return Deployable.of(null, unused -> getMessages().forEach(message -> message.send(target)));
	}

	@NotNull
	@Override
	public Iterator<Message> iterator() {
		return messages.iterator();
	}

	@Override
	public void forEach(Consumer<? super Message> action) {
		messages.forEach(action);
	}

	@Override
	public Spliterator<Message> spliterator() {
		return messages.spliterator();
	}
}
