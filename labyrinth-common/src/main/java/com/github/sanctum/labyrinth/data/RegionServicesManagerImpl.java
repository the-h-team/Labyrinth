package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.task.Schedule;
import org.bukkit.Bukkit;

/**
 * Default {@link RegionServicesManager} implementation.
 */
public final class RegionServicesManagerImpl extends RegionServicesManager {

    final Cuboid.FlagManager flagManager = new Cuboid.FlagManager();

    @Override
    public boolean isRegistered(Cuboid.Flag flag) {
        if (flag == null) return false;
        return getFlagManager().getFlags().stream().anyMatch(f -> f.getId().equals(flag.getId()));
    }

    @Override
    public boolean unregister(Cuboid.Flag flag) {
        if (flag == null) return false;
        for (Region r : Region.cache().list()) {
            r.getFlags().forEach(f -> {
                if (f.getId().equals(flag.getId())) {
                    Schedule.sync(() -> r.removeFlag(f)).run();
                }
            });
        }
        return getFlagManager().getFlags().removeIf(f -> f.getId().equals(flag.getId()));
    }

    @Override
    public boolean register(Cuboid.Flag flag) {
        if (flag == null) return false;
        if (getFlagManager().getFlags().stream().noneMatch(f -> f.getId().equals(flag.getId()))) {
            getFlagManager().getFlags().add(flag);
            return true;
        }
        return false;
    }

    @Override
    public boolean load(Cuboid.Flag flag) {
        if (flag == null) return false;
        Bukkit.getPluginManager().registerEvents(flag, LabyrinthProvider.getInstance().getPluginInstance());
        return getFlagManager().getFlags().add(flag);
    }

    @Override
    public void load(Vent.Subscription<?> subscription) {
        Vent.subscribe(subscription);
    }

    @Override
    public <T extends Vent> void unload(Class<T> type, String key) {
        LabyrinthProvider.getInstance().getEventMap().unsubscribe(type, key);
    }

    @Override
    public <T extends Vent> void unloadAll(Class<T> type, String key) {
        LabyrinthProvider.getInstance().getEventMap().unsubscribeAll(type, key);
    }

    @Override
    public Cuboid.FlagManager getFlagManager() {
        return flagManager;
    }

}
