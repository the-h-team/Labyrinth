package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.library.Cooldown;
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
}
