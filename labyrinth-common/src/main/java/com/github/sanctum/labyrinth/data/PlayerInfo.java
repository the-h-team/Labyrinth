package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.common.StopWatch;
import com.github.sanctum.labyrinth.data.container.CollectionTask;
import com.github.sanctum.labyrinth.data.service.Service;
import com.github.sanctum.labyrinth.data.service.TaskService;
import com.github.sanctum.labyrinth.formatting.string.BufferedHeadSkin;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherEntryMap;
import com.github.sanctum.panther.container.PantherList;
import com.github.sanctum.panther.container.PantherMap;
import com.github.sanctum.panther.util.Deployable;
import com.github.sanctum.panther.util.SpecialID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * An object wrapper responsible for holding player information.
 */
public abstract class PlayerInfo implements LabyrinthPlayer, Service {

    static final PantherMap<String, PlayerInfo> lookups = new PantherEntryMap<>();

    public abstract @NotNull SpecialID getSpecialId();

    public final StopWatch.Recording getPlaytime() {
        return StopWatch.Recording.subtract(getPlayer().getFirstPlayed());
    }

    public abstract BufferedHeadSkin getHeadImage();

    public static PlayerInfo of(@NotNull OfflinePlayer player) {
        String name = player.getName();
        if (name == null) {
            LabyrinthProvider.getInstance().getLogger().severe("- Attempted and failed to register corrupt player data (" + player + "), this is not the fault of labyrinth.");
            return null;
        }
        return lookups.computeIfAbsent(name, s -> new LabyrinthPlayerImpl(player));
    }

    public static PlayerInfo of(String name) {
        return lookups.get(name);
    }

    public static void register(@NotNull PlayerInfo search) {
        lookups.put(search.getName(), search);
    }

    public static PantherCollection<PlayerInfo> values() {
        return new PantherList<>(lookups.values());
    }

    public static Deployable<Void> reload() {
        return Deployable.of(() -> {
            PlayerInfo.lookups.clear();
            final OfflinePlayer[] players = Bukkit.getOfflinePlayers();
            final LabyrinthAPI api = LabyrinthProvider.getInstance();
            int runtime = players.length >= 1000 ? TaskService.ASYNCHRONOUS : TaskService.SYNCHRONOUS;
            CollectionTask<OfflinePlayer> cache = CollectionTask.process(players, "USER-CACHE", 20, runtime, PlayerInfo::of);
            if (players.length >= 500) {
                if (players.length >= 1000) {
                    api.getLogger().warning("- Whoa large amounts of registered players, splitting the workload...");
                    api.getScheduler(runtime).repeat(cache, 0, 50); // repeat the task every 1 tick therefore loading only 20 users every tick instead of all at once.
                } else {
                    api.getLogger().warning("- A-lot of registered players, splitting the workload...");
                    api.getScheduler(TaskService.ASYNCHRONOUS).repeat(cache, 0, 50); // repeat the task every 1 tick therefore loading only 20 users every tick instead of all at once.
                }
                while (cache.getCompletion() < 100) {
                    try {
                        Thread.sleep(1L); // make main thread wait to continue after all users are loaded.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (OfflinePlayer op : players) PlayerInfo.of(op);
            }
            return null;
        }, 0);
    }

}
