package com.github.sanctum.skulls;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.data.container.CollectionTask;
import com.github.sanctum.labyrinth.data.service.Counter;
import com.github.sanctum.labyrinth.data.service.LabyrinthOption;
import com.github.sanctum.labyrinth.event.EnableAfterEvent;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.event.Subscribe;
import com.github.sanctum.panther.event.Vent;
import com.github.sanctum.panther.event.VentMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A custom skinned player skull with an attached reference to its category and possible owner. If no owner is present then this head is considered {@link SkullReferenceType#CUSTOM}
 *
 * All base64 head values found from "https://minecraft-heads.com/custom-heads" are supported.
 */
public abstract class CustomHead implements SkullReferenceMeta {

	protected UUID owner;

	protected CustomHead() {}

	protected CustomHead(UUID owner) {
		this.owner = owner;
	}

	static {
		VentMap.getInstance().subscribe((Vent.Host) LabyrinthProvider.getInstance().getPluginInstance(), new DefaultHead());
	}

	@Subscribe
	void onLoad(EnableAfterEvent e) {
		if (LabyrinthOption.HEAD_PRE_CACHE.enabled()) {
			SkullReferenceUtility.heads.stream().filter(h -> h.getType() == SkullReferenceType.PLAYER).forEach(h -> TaskScheduler.of(() -> SkullReferenceUtility.heads.remove(h)).schedule());
			SkullReferenceUtility.heads.addAll(loadAll());
			SkullReferenceUtility.loaded = true;
		}
	}

	@Override
	public abstract @NotNull ItemStack getItem();

	@Override
	public abstract @NotNull String getName();

	@Override
	public abstract @NotNull String getCategory();

	@Override
	public Optional<UUID> getId() {
		return Optional.ofNullable(this.owner);
	}

	@Override
	public SkullReferenceType getType() {
		return this.getId().isPresent() ? SkullReferenceType.PLAYER : SkullReferenceType.CUSTOM;
	}

	private static List<CustomHead> loadAll() {
		List<CustomHead> list = new LinkedList<>();
		if (!SkullReferenceUtility.loaded) {
			final OfflinePlayer[] players = Bukkit.getOfflinePlayers();
			final LabyrinthAPI api = LabyrinthProvider.getInstance();
			if (players.length >= 500) {
				Counter<Long> count = Counter.newInstance();
				CollectionTask<OfflinePlayer> cache = CollectionTask.process(players, "USER-CACHE", 20, player -> {
                    SkullReferenceLookup search;
                    try {
                        search = new SkullReferenceLookup(player.getUniqueId());
						if (player.getName() != null) {
							list.add(new DefaultHead(player.getName(), "Human", search.getResult(), player.getUniqueId()));
						}
                    } catch (InvalidSkullReferenceException e) {
                     api.getLogger().severe("- Unable to load skull reference data for user '" + player.getName() + "' skipping...");
					 count.add();
                    }
                });
				api.getLogger().warning("- A-lot of players, splitting the workload...");
				api.getLogger().warning("- You can turn off skull pre-caching in the labyrinth config.");
				api.getScheduler(TaskService.ASYNCHRONOUS).repeat(cache, 0, 50); // repeat the task every 1 tick therefore loading only 20 users every tick instead of all at once.
				while (cache.getCompletion() < 100) {
					try {
						Thread.sleep(1L); // make main thread wait to continue after all users are loaded.
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (count.get() > 0) {
					api.getLogger().warning(count.get() + " non-premium players accounted for.");
				}
			} else {
				TaskScheduler.of(() -> {
					int count = 0;
					for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                        SkullReferenceLookup search;
                        try {
                            search = new SkullReferenceLookup(player.getUniqueId());
							if (player.getName() != null) {
								list.add(new DefaultHead(player.getName(), "Human", search.getResult(), player.getUniqueId()));
							}
                        } catch (InvalidSkullReferenceException e) {
							api.getLogger().severe("- Unable to load skull reference data for user '" + player.getName() + "' skipping...");
							count++;
                        }
                    }
					if (count > 0) {
						api.getLogger().warning(count + " non-premium players accounted for.");
					}
				}).scheduleAsync();
			}
		}
		return list;
	}

}
