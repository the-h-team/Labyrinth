package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.LabyrinthUser;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherEntryMap;
import com.github.sanctum.panther.container.PantherMap;
import com.github.sanctum.panther.util.Deployable;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public interface UserRegistration extends Service {

	static UserRegistration getInstance() {
		UserRegistration test = LabyrinthProvider.getService(UserRegistration.class);
		if (test != null) return test;
		test = new UserRegistration() {
			final PantherMap<String, LabyrinthUser> cache = new PantherEntryMap<>();

			public Deployable<LabyrinthUser> load(@NotNull LabyrinthUser user) {
				return Deployable.of(user, u -> {
					if (u instanceof PlayerSearch) {
						PlayerSearch.register((PlayerSearch) u);
					} else cache.computeIfAbsent(u.getName(), u);
				}, 0);
			}

			public Deployable<LabyrinthUser> unload(@NotNull LabyrinthUser user) {
				return Deployable.of(user, u -> cache.remove(u.getName()), 0);
			}

			public Deployable<LabyrinthUser> get(@NotNull String name) {
				return Deployable.of(() -> Optional.ofNullable(cache.get(name)).orElseGet(() -> PlayerSearch.of(name)), 0);
			}

			public Deployable<PantherCollection<LabyrinthUser>> getAll() {
				return Deployable.of(cache::values, 0);
			}
		};
		UserRegistration finalTest = test;
		LabyrinthProvider.getInstance().getServiceManager().newLoader(UserRegistration.class).supply(finalTest);
		return test;
	}

	Deployable<LabyrinthUser> load(@NotNull LabyrinthUser user);

	Deployable<LabyrinthUser> unload(@NotNull LabyrinthUser user);

	Deployable<LabyrinthUser> get(@NotNull String name);

	Deployable<PantherCollection<LabyrinthUser>> getAll();

}
