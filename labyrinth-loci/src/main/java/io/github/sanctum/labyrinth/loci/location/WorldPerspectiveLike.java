package io.github.sanctum.labyrinth.loci.location;

import org.jetbrains.annotations.NotNull;

/**
 * An object that can be represented as a world perspective.
 *
 * @since 1.9.0
 * @author ms5984
 */
@FunctionalInterface
public interface WorldPerspectiveLike {
    /**
     * Gets this object as a world perspective.
     *
     * @return a world perspective
     */
    @NotNull WorldPerspective asWorldPerspective();
}
