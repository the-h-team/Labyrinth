package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.task.Schedule;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class VentMapImpl extends VentMap {

	@Override
	public <T extends Vent> void unsubscribe(@NotNull Class<T> eventType, @NotNull String key) {
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
	public <T extends Vent> void unsubscribeAll(@NotNull Class<T> eventType, @NotNull String key) {
		for (Vent.Subscription<?> s : subscriptions) {
			if (s.getEventType().isAssignableFrom(eventType)) {
				if (s.getKey().isPresent() && s.getKey().get().equals(key)) {
					Schedule.sync(() -> subscriptions.remove(s)).waitReal(1);
				}
			}
		}
	}

	@Override
	public void unsubscribeAll(@NotNull String key) {
		for (Vent.Subscription<?> s : subscriptions) {
            if (s.getKey().isPresent() && s.getKey().get().equals(key)) {
                Schedule.sync(() -> subscriptions.remove(s)).waitReal(1);
            }
		}
	}

    @Override
    public void unsubscribeAll(Predicate<Vent.Subscription<?>> fun) {
        for (Vent.Subscription<?> s : subscriptions) {
	        if (fun.test(s)) {
		        Schedule.sync(() -> subscriptions.remove(s)).waitReal(1);
	        }
        }
    }

	@Override
	public void unregister(@NotNull Object listener) {
		for (RegisteredListener l : listeners) {
			if (listener.equals(l.getListener())) {
				Schedule.sync(() -> listeners.remove(l)).run();
				break;
			}
		}
	}

	@Override
	public void unregisterAll(@NotNull Plugin host) {
		for (RegisteredListener l : listeners) {
			if (l.getHost().equals(host)) {
				Schedule.sync(() -> listeners.remove(l)).run();
			}
		}
	}

	@Override
	public List<RegisteredListener> list(@NotNull Plugin plugin) {
		return listeners.stream().filter(l -> l.getHost().equals(plugin)).collect(Collectors.toList());
	}

	@Override
	public List<Vent.Subscription<?>> narrow(@NotNull Plugin plugin) {
		return subscriptions.stream().filter(s -> s.getUser().equals(plugin)).collect(Collectors.toList());
	}

	@Override
	public <T extends Vent> Vent.Subscription<T> get(@NotNull Class<T> eventType, @NotNull String key) {
		return (Vent.Subscription<T>) subscriptions.stream().filter(s -> s.getEventType().isAssignableFrom(eventType) && s.getKey().isPresent() && s.getKey().get().equals(key)).findFirst().orElse(null);
	}

}
