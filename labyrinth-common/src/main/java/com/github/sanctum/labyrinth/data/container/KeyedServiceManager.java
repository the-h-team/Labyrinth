package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An alternative to bukkit service provision.
 * @author Hempfest
 */
public class KeyedServiceManager<K> {

	private final Set<RegisteredKeyedService<?, ?>> registry = new HashSet<>();

	public <T> void register(@NotNull T service, @NotNull K key, @NotNull ServicePriority priority) {
		this.registry.add(new RegisteredKeyedService<>(service, key, priority));
	}


	public void unregisterAll(@NotNull K key) {
		for (RegisteredKeyedService<?, ?> service : this.registry) {
			if (key.getClass().isAssignableFrom(service.getKey().getClass())) {
				if (key.equals(service.getKey())) {
					TaskScheduler.of(() -> this.registry.remove(service)).schedule();
				}
			}
		}
	}

	public <T> void unregisterAll(@NotNull Class<T> service) {
		for (RegisteredKeyedService<?, ?> s : this.registry) {
			if (service.isAssignableFrom(s.getSuperClass())) {
				TaskScheduler.of(() -> this.registry.remove(s)).schedule();
			}
		}
	}

	public <T> void unregister(@NotNull Class<T> service) {
		for (RegisteredKeyedService<?, ?> s : this.registry) {
			if (service.isAssignableFrom(s.getSuperClass())) {
				TaskScheduler.of(() -> this.registry.remove(s)).schedule();
				break;
			}
		}
	}

	public <T> void unregister(@NotNull T service) {
		for (RegisteredKeyedService<?, ?> s : this.registry) {
			if (s.getService().equals(service)) {
				TaskScheduler.of(() -> this.registry.remove(s)).schedule();
				break;
			}
		}
	}

	public <T> @Nullable T load(@NotNull Class<T> service, K key) {
		T serv = null;
		for (RegisteredKeyedService<?, ?> s : this.registry.stream().sorted(Comparator.comparingInt(value -> value.getPriority().ordinal())).collect(Collectors.toList())) {
			if (service.isAssignableFrom(s.getSuperClass()) && Objects.equals(s.getKey(), key)) {
				serv = (T) s.getService();
			}
		}
		return serv;
	}

	public <T> @Nullable T load(@NotNull Class<T> service, ServicePriority priority) {
		T serv = null;
		for (RegisteredKeyedService<?, ?> s : this.registry.stream().sorted(Comparator.comparingInt(value -> value.getPriority().ordinal())).collect(Collectors.toList())) {
			if (service.isAssignableFrom(s.getSuperClass()) && s.getPriority() == priority) {
				serv = (T) s.getService();
			}
		}
		return serv;
	}

	public <T> @Nullable T load(@NotNull Class<T> service) {
		T serv = null;
		for (RegisteredKeyedService<?, ?> s : this.registry.stream().sorted(Comparator.comparingInt(value -> value.getPriority().ordinal())).collect(Collectors.toList())) {
			if (service.isAssignableFrom(s.getSuperClass())) {
				serv = (T) s.getService();
			}
		}
		return serv;
	}

	public @Nullable <T> RegisteredKeyedService<T, K> getRegistration(@NotNull Class<T> service) {
		for (RegisteredKeyedService<?, ?> s : this.registry.stream().sorted(Comparator.comparingInt(value -> value.getPriority().ordinal())).collect(Collectors.toList())) {
			if (service.isAssignableFrom(s.getSuperClass())) {
				return (RegisteredKeyedService<T, K>) s;
			}
		}
		return null;
	}

	public @Nullable <T> RegisteredKeyedService<T, K> getRegistration(@NotNull Class<T> service, K key) {
		for (RegisteredKeyedService<?, ?> s : this.registry) {
			if (service.isAssignableFrom(s.getSuperClass()) && Objects.equals(key, s.getKey())) {
				return (RegisteredKeyedService<T, K>) s;
			}
		}
		return null;
	}


	public @NotNull List<RegisteredKeyedService<?, K>> getRegistrations(@NotNull K key) {
		List<RegisteredKeyedService<?, K>> services = new ArrayList<>();

		for (RegisteredKeyedService<?, ?> s : this.registry) {
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
		for (RegisteredKeyedService<?, ?> s : this.registry) {
			if (service.isAssignableFrom(s.getSuperClass())) {
				services.add((RegisteredKeyedService<T, K>) s);
			}
		}
		return services;
	}


	public @NotNull Collection<Class<?>> getKnownServices() {
		return this.registry.stream().map(RegisteredKeyedService::getSuperClass).collect(Collectors.toList());
	}


	public <T> boolean isProvided(@NotNull Class<T> service) {
		return this.registry.stream().anyMatch(r -> service.isAssignableFrom(r.getSuperClass()));
	}
}
