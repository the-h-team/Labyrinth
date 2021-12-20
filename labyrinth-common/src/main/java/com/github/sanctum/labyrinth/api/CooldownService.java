package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.library.ParsedTimeFormat;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * Provides cooldown information.
 */
public interface CooldownService extends Service {
    /**
     * Get a list of all pre-cached cooldowns.
     *
     * @return a list of all cached cooldowns
     */
    @NotNull List<Cooldown> getCooldowns();

    /**
     * Get a cooldown by its specified key.
     *
     * @param id The id of the cooldown to get.
     * @return The desired cooldown or null if not found.
     */
    @Nullable Cooldown getCooldown(String id);

    /**
     * Remove a cooldown from cache.
     *
     * @param cooldown The cooldown to remove from disk and cache.
     * @return false if the cooldown failed to remove from cache.
     */
    boolean remove(Cooldown cooldown);

    /**
     * Create a new cooldown instance using the provided properties.
     *
     * @param timeFormat The time format to use.
     * @param id The id of the given cooldown.
     * @return A fresh cooldown instance or a locally cached one.
     */
    default @NotNull Cooldown newCooldown(@NotNull ParsedTimeFormat timeFormat, @NotNull String id) {
        return Optional.ofNullable(getCooldown(id)).orElseGet(() -> {
            Cooldown cooldown = new Cooldown() {
                private final long time;

                {
                    this.time = abv(timeFormat.toSeconds());
                }

                @Override
                public String getId() {
                    return id;
                }

                @Override
                public long getCooldown() {
                    return time;
                }
            };
            cooldown.save();
            return cooldown;
        });
    }
}
