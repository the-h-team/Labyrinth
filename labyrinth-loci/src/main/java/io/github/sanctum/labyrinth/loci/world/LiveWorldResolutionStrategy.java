package io.github.sanctum.labyrinth.loci.world;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides a strategy for converting and comparing world references using
 * information only present at runtime.
 *
 * @since 1.8.3
 * @author ms5984
 */
@ApiStatus.OverrideOnly
public interface LiveWorldResolutionStrategy {
    /**
     * Checks if a world reference is present on the server (runtime).
     *
     * @param worldReference a world reference
     * @return true if the world is present
     */
    boolean isPresent(@NotNull WorldReference worldReference);

    /**
     * Attempts to convert a UUID-based reference to a name-based reference.
     *
     * @param byUID a UUID-based reference
     * @return a name-based reference or null if the world represented by
     * {@code byUUID} could not be found
     */
    @Nullable WorldReference.ByName convertToByName(@NotNull WorldReference.ByUID byUID);

    /**
     * Attempts to convert a name-based reference to a UUID-based reference.
     *
     * @param byName a name-based reference
     * @return a UUID-based reference or null if the world represented by
     * {@code byName} could not be found
     */
    @Nullable WorldReference.ByUID convertToByUUID(@NotNull WorldReference.ByName byName);

    /**
     * Tests whether a UUID-based reference and a name-based reference refer
     * to the same world.
     * <p>
     * If either world is not present, this method will return false.
     *
     * @param byUID a UUID-based reference
     * @param byName a name-based reference
     * @return true if the worlds are found and refer to the same world
     */
    default boolean isSame(@NotNull WorldReference.ByUID byUID, @NotNull WorldReference.ByName byName) {
        if (!isPresent(byUID) || !isPresent(byName)) {
            return false;
        }
        final WorldReference.ByUID toByUID = convertToByUUID(byName);
        if (toByUID == null) return false;
        return toByUID.equals(byUID);
    }
}
