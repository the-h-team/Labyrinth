package io.github.sanctum.labyrinth.loci.location;

import org.jetbrains.annotations.NotNull;

/**
 * An object that can be represented as a perspective.
 *
 * @since 1.8.3
 * @author ms5984
 */
@FunctionalInterface
public interface PerspectiveLike {
    /**
     * Gets this object as a perspective.
     *
     * @return a perspective
     */
    @NotNull Perspective asPerspective();
}
