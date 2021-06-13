package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.task.Schedule;
import java.util.LinkedList;

public final class VentMap {

	protected final LinkedList<Vent.Subscription<?>> SUBSCRIPTIONS = new LinkedList<>();

	public <T extends Vent> void unsubscribe(Class<T> eventType, String key) {
		for (Vent.Subscription<?> s : SUBSCRIPTIONS) {
			if (s.getEventType().isAssignableFrom(eventType)) {
				if (s.getKey().isPresent() && s.getKey().get().equals(key)) {
					Schedule.sync(() -> SUBSCRIPTIONS.remove(s)).waitReal(1);
					break;
				}
			}
		}
	}

	public <T extends Vent> void unsubscribeAll(Class<T> eventType, String key) {
		for (Vent.Subscription<?> s : SUBSCRIPTIONS) {
			if (s.getEventType().isAssignableFrom(eventType)) {
				if (s.getKey().isPresent() && s.getKey().get().equals(key)) {
					Schedule.sync(() -> SUBSCRIPTIONS.remove(s)).waitReal(1);
				}
			}
		}
	}

	public <T extends Vent> Vent.Subscription<?> getSubscription(Class<T> eventType, String key) {
		return SUBSCRIPTIONS.stream().filter(s -> s.getEventType().isAssignableFrom(eventType) && s.getKey().isPresent() && s.getKey().get().equals(key)).findFirst().orElse(null);
	}

}
