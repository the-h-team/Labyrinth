package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.LabyrinthUser;
import com.github.sanctum.labyrinth.data.ServiceType;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthEntryMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;
import com.github.sanctum.labyrinth.library.Deployable;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public interface UserRegistration extends Service {

	static UserRegistration getInstance() {
		UserRegistration test = LabyrinthProvider.getService(UserRegistration.class);
		if (test != null) return test;
		test = new UserRegistration() {
			final LabyrinthMap<String, LabyrinthUser> cache = new LabyrinthEntryMap<>();

			public Deployable<LabyrinthUser> load(@NotNull LabyrinthUser user) {
				return Deployable.of(user, u -> {
					if (u instanceof PlayerSearch) {
						PlayerSearch.register((PlayerSearch) u);
					} else cache.computeIfAbsent(u.getName(), u);
				});
			}

			public Deployable<LabyrinthUser> unload(@NotNull LabyrinthUser user) {
				return Deployable.of(user, u -> cache.remove(u.getName()));
			}

			public Deployable<LabyrinthUser> get(@NotNull String name) {
				return Deployable.of(() -> Optional.ofNullable(cache.get(name)).orElseGet(() -> PlayerSearch.of(name)));
			}

			public Deployable<LabyrinthCollection<LabyrinthUser>> getAll() {
				return Deployable.of(cache::values);
			}
		};
		UserRegistration finalTest = test;
		LabyrinthProvider.getInstance().getServiceManager().load(new ServiceType<>(() -> finalTest));
		return test;
	}

	Deployable<LabyrinthUser> load(@NotNull LabyrinthUser user);

	Deployable<LabyrinthUser> unload(@NotNull LabyrinthUser user);

	Deployable<LabyrinthUser> get(@NotNull String name);

	Deployable<LabyrinthCollection<LabyrinthUser>> getAll();

}
