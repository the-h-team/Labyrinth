package io.github.sanctum.labyrinth.loci.world;

import org.jetbrains.annotations.NotNull;

/**
 * An object that has an associated world.
 *
 * @since 1.8.3
 * @author ms5984
 */
@FunctionalInterface
public interface HasWorld {
    /**
     * Gets the world reference of this object.
     *
     * @return a world reference
     */
    @NotNull WorldReference getWorld();
}
