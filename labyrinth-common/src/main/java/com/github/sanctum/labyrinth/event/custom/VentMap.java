package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.task.Schedule;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class VentMap {

	final LinkedList<Vent.Subscription<?>> subscriptions = new LinkedList<>();

	public <T extends Vent> void unsubscribe(Class<T> eventType, String key) {
		for (Vent.Subscription<?> s : subscriptions) {
			if (s.getEventType().isAssignableFrom(eventType)) {
				if (s.getKey().isPresent() && s.getKey().get().equals(key)) {
					Schedule.sync(() -> subscriptions.remove(s)).waitReal(1);
					break;
				}
			}
		}
	}

	public <T extends Vent> void unsubscribeAll(Class<T> eventType, String key) {
		for (Vent.Subscription<?> s : subscriptions) {
			if (s.getEventType().isAssignableFrom(eventType)) {
				if (s.getKey().isPresent() && s.getKey().get().equals(key)) {
					Schedule.sync(() -> subscriptions.remove(s)).waitReal(1);
				}
			}
		}
	}

	public List<Vent.Subscription<?>> getSubscriptions(Plugin plugin) {
		return subscriptions.stream().filter(s -> s.getUser().equals(plugin)).collect(Collectors.toList());
	}

	public <T extends Vent> Vent.Subscription<?> getSubscription(Class<T> eventType, String key) {
		return subscriptions.stream().filter(s -> s.getEventType().isAssignableFrom(eventType) && s.getKey().isPresent() && s.getKey().get().equals(key)).findFirst().orElse(null);
	}

}
