package io.github.sanctum.labyrinth.loci.location;

import org.jetbrains.annotations.NotNull;

/**
 * An object that can be represented as a position.
 *
 * @since 1.8.3
 * @author ms5984
 */
@FunctionalInterface
public interface PositionLike {
    /**
     * Gets this object as a position.
     *
     * @return a position
     */
    @NotNull Position asPosition();
}
