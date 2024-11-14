package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.panther.event.Vent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * You know how sometimes plugin load order can make certain services do something before able resulting in loading problems. Well this ensures everything is loaded beforehand. No more delayed tasks.
 */
public class EnableAfterEvent extends Vent {
    final String[] enabled;
    public EnableAfterEvent(String[] enabled) {
        super((Host) LabyrinthProvider.getInstance().getPluginInstance(), false);
        this.enabled = enabled;
    }

    public boolean isLoaded(@NotNull String name) {
        return Arrays.stream(enabled).anyMatch( s -> s.equalsIgnoreCase(name));
    }
}
