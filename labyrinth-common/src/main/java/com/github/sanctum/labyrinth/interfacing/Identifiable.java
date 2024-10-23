package com.github.sanctum.labyrinth.interfacing;

import com.github.sanctum.labyrinth.data.service.PlayerSearch;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Identifiable {

    @NotNull String getName();

    default @NotNull UUID getUniqueId() {
        return UUID.nameUUIDFromBytes("LABYRINTH;DEFAULT".getBytes());
    }

    default boolean isPlayer() {
        return PlayerSearch.of(getName()) != null;
    }

    default boolean isEntity() {
        return !isPlayer() && Bukkit.getEntity(getUniqueId()) != null;
    }

    default Player getAsPlayer() {
        return Bukkit.getPlayer(getUniqueId());
    }

    default Entity getAsEntity() {
        return Bukkit.getEntity(getUniqueId());
    }

    static @NotNull Identifiable wrap(@NotNull Entity entity) {
        return new Identifiable() {
            @Override
            public @NotNull String getName() {
                return entity.getName();
            }

            @Override
            public @NotNull UUID getUniqueId() {
                return entity.getUniqueId();
            }
        };
    }

}
