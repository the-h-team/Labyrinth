package io.github.sanctum.labyrinth.loci.world;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Refers to a world.
 *
 * @since 1.8.3
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface WorldReference {
    /**
     * Gets a reference to a world by its name.
     *
     * @param name the name of the world
     * @return a world reference
     * @implNote No attempt will be made to validate the provided world name.
     */
    static ByName name(@NotNull String name) {
        return new ByNameImpl(name);
    }

    /**
     * Gets a reference to a world by its Unique ID.
     *
     * @param uid the unique ID of the world
     * @return a world reference
     * @implNote No attempt will be made to validate the provided unique ID.
     */
    static ByUID uid(@NotNull UUID uid) {
        return new ByUIDImpl(uid);
    }

    /**
     * Refers to a world by its name.
     *
     * @since 1.8.3
     */
    @ApiStatus.NonExtendable
    interface ByName extends WorldReference {
        /**
         * Gets the name of the world.
         *
         * @return the name of the world
         */
        @NotNull String getName();
    }

    /**
     * Refers to a world by its Unique ID.
     *
     * @since 1.8.3
     */
    @ApiStatus.NonExtendable
    interface ByUID extends WorldReference {
        /**
         * Gets the unique ID of the world.
         *
         * @return the unique ID of the world
         */
        @NotNull UUID getUID();
    }
}
