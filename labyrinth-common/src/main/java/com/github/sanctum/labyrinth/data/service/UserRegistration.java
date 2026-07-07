package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.LabyrinthPlayer;
import com.github.sanctum.labyrinth.data.PlayerInfo;
import com.github.sanctum.labyrinth.data.ServiceType;
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
			final PantherMap<String, LabyrinthPlayer> cache = new PantherEntryMap<>();

			public Deployable<LabyrinthPlayer> load(@NotNull LabyrinthPlayer user) {
				return Deployable.of(user, u -> {
					if (u instanceof PlayerInfo) {
						PlayerInfo.register((PlayerInfo) u);
					} else cache.computeIfAbsent(u.getName(), u);
				}, 0);
			}

			public Deployable<LabyrinthPlayer> unload(@NotNull LabyrinthPlayer user) {
				return Deployable.of(user, u -> cache.remove(u.getName()), 0);
			}

			public Deployable<LabyrinthPlayer> get(@NotNull String name) {
				return Deployable.of(() -> Optional.ofNullable(cache.get(name)).orElseGet(() -> PlayerInfo.of(name)), 0);
			}

			public Deployable<PantherCollection<LabyrinthPlayer>> getAll() {
				return Deployable.of(cache::values, 0);
			}
		};
		UserRegistration finalTest = test;
		LabyrinthProvider.getInstance().getServiceManager().load(new ServiceType<>(() -> finalTest));
		return test;
	}

	Deployable<LabyrinthPlayer> load(@NotNull LabyrinthPlayer user);

	Deployable<LabyrinthPlayer> unload(@NotNull LabyrinthPlayer user);

	Deployable<LabyrinthPlayer> get(@NotNull String name);

	Deployable<PantherCollection<LabyrinthPlayer>> getAll();

}
