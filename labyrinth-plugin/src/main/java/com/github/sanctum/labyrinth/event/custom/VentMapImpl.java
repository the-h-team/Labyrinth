package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.task.Schedule;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.bukkit.plugin.Plugin;

public final class VentMapImpl extends VentMap {

	@Override
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

	@Override
	public <T extends Vent> void unsubscribeAll(Class<T> eventType, String key) {
		for (Vent.Subscription<?> s : subscriptions) {
			if (s.getEventType().isAssignableFrom(eventType)) {
				if (s.getKey().isPresent() && s.getKey().get().equals(key)) {
					Schedule.sync(() -> subscriptions.remove(s)).waitReal(1);
				}
			}
		}
	}

	@Override
	public void unsubscribeAll(String key) {
		for (Vent.Subscription<?> s : subscriptions) {
            if (s.getKey().isPresent() && s.getKey().get().equals(key)) {
                Schedule.sync(() -> subscriptions.remove(s)).waitReal(1);
            }
		}
	}

    @Override
    public void unsubscribeAll(Predicate<Class<?>> fun, String key) {
        for (Vent.Subscription<?> s : subscriptions) {
            if (s.getKey().isPresent() && s.getKey().get().equals(key)) {
                if (fun.test(s.getEventType())) {
                    Schedule.sync(() -> subscriptions.remove(s)).waitReal(1);
                }
            }
        }
    }

    @Override
	public List<Vent.Subscription<?>> narrow(Plugin plugin) {
		return subscriptions.stream().filter(s -> s.getUser().equals(plugin)).collect(Collectors.toList());
	}

	@Override
	public <T extends Vent> Vent.Subscription<?> get(Class<T> eventType, String key) {
		return subscriptions.stream().filter(s -> s.getEventType().isAssignableFrom(eventType) && s.getKey().isPresent() && s.getKey().get().equals(key)).findFirst().orElse(null);
	}

}
