package io.github.sanctum.labyrinth.loci.location;

import org.jetbrains.annotations.NotNull;

/**
 * An object that can be represented as a world position.
 *
 * @since 1.8.3
 * @author ms5984
 */
@FunctionalInterface
public interface WorldPositionLike {
    /**
     * Gets this object as a world position.
     *
     * @return a world position
     */
    @NotNull WorldPosition asWorldPosition();
}
