package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.library.Cooldown;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CooldownService {
    /**
     * Get a list of all pre-cached cooldowns.
     *
     * @return a list of all cached cooldowns
     */
    @NotNull List<Cooldown> getCooldowns();
}
