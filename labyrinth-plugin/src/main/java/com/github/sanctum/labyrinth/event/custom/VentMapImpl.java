package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.task.Schedule;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

//FIXME
public final class VentMapImpl extends VentMap {

	@Override //TODO
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

	@Override //TODO
	public <T extends Vent> void unsubscribeAll(@NotNull Class<T> eventType, @NotNull String key) {
		for (Vent.Subscription<?> s : subscriptions) {
			if (s.getEventType().isAssignableFrom(eventType)) {
				if (s.getKey().isPresent() && s.getKey().get().equals(key)) {
					Schedule.sync(() -> subscriptions.remove(s)).waitReal(1);
				}
			}
		}
	}

	@Override //TODO
	public void unsubscribeAll(@NotNull String key) {
		for (Vent.Subscription<?> s : subscriptions) {
			if (s.getKey().isPresent() && s.getKey().get().equals(key)) {
				Schedule.sync(() -> subscriptions.remove(s)).waitReal(1);
			}
		}
	}

	@Override //TODO
	public void unsubscribeAll(Predicate<Vent.Subscription<?>> fun) {
		for (Vent.Subscription<?> s : subscriptions) {
			if (fun.test(s)) {
				Schedule.sync(() -> subscriptions.remove(s)).waitReal(1);
			}
		}
	}

	@Override //TODO
	public void unregister(@NotNull Object listener) {
		for (VentListener l : listeners) {
			if (listener.equals(l) || listener.equals(l.getListener())) {
				Schedule.sync(() -> listeners.remove(l)).run();
				break;
			}
		}
	}

	@Override //TODO
	public void unregisterAll(@NotNull Plugin host) {
		for (VentListener l : listeners) {
			if (l.getHost().equals(host)) {
				Schedule.sync(() -> listeners.remove(l)).run();
			}
		}
	}

	@Override //TODO
	public List<VentListener> list(@NotNull Plugin plugin) {
		return listeners.stream().filter(l -> l.getHost().equals(plugin)).collect(Collectors.toList());
	}

	@Override //TODO
	public List<Vent.Subscription<?>> narrow(@NotNull Plugin plugin) {
		return subscriptions.stream().filter(s -> s.getUser().equals(plugin)).collect(Collectors.toList());
	}

	@Override //TODO
	public VentListener get(String key) {
		for (VentListener listener : listeners) {
			if (listener.getKey() != null && listener.getKey().equals(key)) {
				return listener;
			}
		}
		return null;
	}

	@Override //TODO
	public <T extends Vent> Vent.Subscription<T> get(@NotNull Class<T> eventType, @NotNull String key) {
		return (Vent.Subscription<T>) subscriptions.stream().filter(s -> s.getEventType().isAssignableFrom(eventType) && s.getKey().isPresent() && s.getKey().get().equals(key)).findFirst().orElse(null);
	}

	@Override //TODO
	public void register(@NotNull Plugin host, @NotNull Object listener) {
		VentListener list = new VentListener(host, listener);
		listeners.add(list);
		//return list;
	}

	@Override //TODO
	public void registerAll(@NotNull Plugin host, @NotNull Object... listener) {
		//Set<VentListener> set = new HashSet<>();
		for (Object o : listener) {
			VentListener list = new VentListener(host, listener);
			listeners.add(list);
			//set.add(list);
		}
	}

	@Override //TODO
	public <T extends Vent> void subscribe(Vent.Subscription<T> subscription) {
		if (subscription == null) {
			LabyrinthProvider.getInstance().getLogger().severe("Null subscription found from unknown source (Not labyrinth).");
			return;
		}
		subscriptions.add(subscription);
	}

	@Override //TODO
	public void chain(Vent.Link link) {
		if (link == null) {
			LabyrinthProvider.getInstance().getLogger().severe("Null subscription link found from unknown source (Not labyrinth).");
			return;
		}
		Vent.Subscription<?> parent = null;
		for (Vent.Subscription<?> sub : link.subscriptions) {
			if (sub.getParent() != null) {
				parent = sub.getParent();
			}
			subscriptions.add(sub);
		}

		if (parent != null) {
			subscriptions.add(parent);
		}

	}
}
