package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.task.Schedule;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class KeyedServiceManager<K> {

	private final Set<RegisteredKeyedService<?, ?>> REGISTRY = new HashSet<>();

	public <T> void register(@NotNull T provider, @NotNull K key, @NotNull ServicePriority priority) {
		this.REGISTRY.add(new RegisteredKeyedService<>(provider, key, priority));
	}


	public void unregisterAll(@NotNull K key) {
		for (RegisteredKeyedService<?, ?> service : this.REGISTRY) {
			if (key.getClass().isAssignableFrom(service.getKey().getClass())) {
				if (key.equals(service.getKey())) {
					Schedule.sync(() -> this.REGISTRY.remove(service)).run();
				}
			}
		}
	}

	public <T> void unregisterAll(@NotNull Class<T> service) {
		for (RegisteredKeyedService<?, ?> s : this.REGISTRY) {
			if (service.isAssignableFrom(s.getSuperClass())) {
				Schedule.sync(() -> this.REGISTRY.remove(s)).run();
			}
		}
	}

	public <T> void unregister(@NotNull Class<T> service) {
		for (RegisteredKeyedService<?, ?> s : this.REGISTRY) {
			if (service.isAssignableFrom(s.getSuperClass())) {
				Schedule.sync(() -> this.REGISTRY.remove(s)).run();
				break;
			}
		}
	}

	public <T> void unregister(@NotNull T provider) {
		for (RegisteredKeyedService<?, ?> service : this.REGISTRY) {
			if (service.getService().equals(provider)) {
				Schedule.sync(() -> this.REGISTRY.remove(service)).run();
				break;
			}
		}
	}


	public <T> @Nullable T load(@NotNull Class<T> service) {
		T serv = null;
		for (RegisteredKeyedService<?, ?> s : this.REGISTRY.stream().sorted(Comparator.comparingInt(value -> value.getPriority().ordinal())).collect(Collectors.toList())) {
			if (service.isAssignableFrom(s.getSuperClass())) {
				serv = (T) s.getService();
			}
		}
		return serv;
	}


	public @Nullable <T> RegisteredKeyedService<T, K> getRegistration(@NotNull Class<T> service, K key) {
		for (RegisteredKeyedService<?, ?> s : this.REGISTRY) {
			if (service.isAssignableFrom(s.getSuperClass()) && Objects.equals(key, s.getKey())) {
				return (RegisteredKeyedService<T, K>) s;
			}
		}
		return null;
	}


	public @NotNull List<RegisteredKeyedService<?, K>> getRegistrations(@NotNull K key) {
		List<RegisteredKeyedService<?, K>> services = new ArrayList<>();

		for (RegisteredKeyedService<?, ?> s : this.REGISTRY) {
			if (key.getClass().isAssignableFrom(s.getKey().getClass())) {
				if (Objects.equals(key, s.getKey())) {
					services.add((RegisteredKeyedService<?, K>) s);
				}
			}
		}
		return services;
	}


	public @NotNull <T> List<RegisteredKeyedService<T, K>> getRegistrations(@NotNull Class<T> service) {
		List<RegisteredKeyedService<T, K>> services = new ArrayList<>();
		for (RegisteredKeyedService<?, ?> s : this.REGISTRY) {
			if (service.isAssignableFrom(s.getSuperClass())) {
				services.add((RegisteredKeyedService<T, K>) s);
			}
		}
		return services;
	}


	public @NotNull Collection<Class<?>> getKnownServices() {
		return this.REGISTRY.stream().map(RegisteredKeyedService::getSuperClass).collect(Collectors.toList());
	}


	public <T> boolean isProvided(@NotNull Class<T> service) {
		return this.REGISTRY.stream().anyMatch(r -> service.isAssignableFrom(r.getSuperClass()));
	}
}
